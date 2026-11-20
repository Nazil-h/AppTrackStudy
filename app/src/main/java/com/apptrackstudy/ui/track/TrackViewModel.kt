package com.apptrackstudy.ui.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.Subject
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.model.SubTopicProgress
import com.apptrackstudy.domain.repository.StudyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * State untuk dialog yang sedang aktif di layar Peta Belajar.
 */
sealed interface TrackDialogState {
    data object AddSubject : TrackDialogState
    data class EditSubject(val subject: Subject) : TrackDialogState
    data class AddSubTopic(val subjectId: Long, val subjectTitle: String) : TrackDialogState
    data class EditSubTopic(val subTopic: SubTopic) : TrackDialogState
    data class AddTask(val subTopicId: Long, val subTopicTitle: String) : TrackDialogState
    data class EditTask(val task: StudyTask) : TrackDialogState
    data class ConfirmDelete(
        val itemType: DeleteItemType,
        val title: String,
        val itemData: Any
    ) : TrackDialogState
}

enum class DeleteItemType {
    SUBJECT, SUB_TOPIC, TASK
}

/**
 * Event untuk Snackbar dengan aksi Undo (Urungkan).
 */
data class SnackbarMessage(
    val message: String,
    val actionLabel: String = "Urungkan"
)

/**
 * Cache item yang baru saja dihapus untuk mendukung fitur Undo.
 */
sealed interface DeletedItem {
    data class SubjectItem(val data: SubjectProgress) : DeletedItem
    data class SubTopicItem(val data: SubTopicProgress) : DeletedItem
    data class TaskItem(val data: StudyTask) : DeletedItem
}

data class TrackUiState(
    val subjects: List<SubjectProgress> = emptyList(),
    val expandedSubjectIds: Set<Long> = emptySet(),
    val expandedSubTopicIds: Set<Long> = emptySet(),
    val activeDialog: TrackDialogState? = null,
    val celebrationSubject: Subject? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class TrackViewModel @Inject constructor(
    private val repository: StudyRepository
) : ViewModel() {

    private val _expandedSubjectIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _expandedSubTopicIds = MutableStateFlow<Set<Long>>(emptySet())
    private val _activeDialog = MutableStateFlow<TrackDialogState?>(null)
    private val _celebrationSubject = MutableStateFlow<Subject?>(null)
    private var previouslyCompletedSubjectIds: Set<Long>? = null

    private val _snackbarEvents = MutableSharedFlow<SnackbarMessage>()
    val snackbarEvents: SharedFlow<SnackbarMessage> = _snackbarEvents.asSharedFlow()

    private var lastDeletedItem: DeletedItem? = null

    private val subjectsWithProgress = repository.getSubjectsWithProgress().onEach { list ->
        val currentCompletedIds = list.filter { it.isCompleted }.map { it.subject.id }.toSet()
        val prevIds = previouslyCompletedSubjectIds
        if (prevIds == null) {
            previouslyCompletedSubjectIds = currentCompletedIds
        } else {
            val newlyCompletedSubject = list.firstOrNull { it.isCompleted && !prevIds.contains(it.subject.id) }
            previouslyCompletedSubjectIds = currentCompletedIds
            if (newlyCompletedSubject != null) {
                _celebrationSubject.value = newlyCompletedSubject.subject
            }
        }
    }

    val uiState: StateFlow<TrackUiState> = combine(
        subjectsWithProgress,
        _expandedSubjectIds,
        _expandedSubTopicIds,
        _activeDialog,
        _celebrationSubject
    ) { subjects, expSubjects, expSubTopics, dialog, celebration ->
        TrackUiState(
            subjects = subjects,
            expandedSubjectIds = expSubjects,
            expandedSubTopicIds = expSubTopics,
            activeDialog = dialog,
            celebrationSubject = celebration,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrackUiState(isLoading = true)
    )

    fun dismissCelebration() {
        _celebrationSubject.value = null
    }

    // --- Expand / Collapse (Survives configuration changes via ViewModel) ---
    fun toggleSubjectExpanded(subjectId: Long) {
        val current = _expandedSubjectIds.value
        _expandedSubjectIds.value = if (current.contains(subjectId)) {
            current - subjectId
        } else {
            current + subjectId
        }
    }

    fun toggleSubTopicExpanded(subTopicId: Long) {
        val current = _expandedSubTopicIds.value
        _expandedSubTopicIds.value = if (current.contains(subTopicId)) {
            current - subTopicId
        } else {
            current + subTopicId
        }
    }

    // --- Dialog Management ---
    fun showAddSubjectDialog() {
        _activeDialog.value = TrackDialogState.AddSubject
    }

    fun showEditSubjectDialog(subject: Subject) {
        _activeDialog.value = TrackDialogState.EditSubject(subject)
    }

    fun showAddSubTopicDialog(subjectId: Long, subjectTitle: String) {
        _activeDialog.value = TrackDialogState.AddSubTopic(subjectId, subjectTitle)
    }

    fun showEditSubTopicDialog(subTopic: SubTopic) {
        _activeDialog.value = TrackDialogState.EditSubTopic(subTopic)
    }

    fun showAddTaskDialog(subTopicId: Long, subTopicTitle: String) {
        _activeDialog.value = TrackDialogState.AddTask(subTopicId, subTopicTitle)
    }

    fun showEditTaskDialog(task: StudyTask) {
        _activeDialog.value = TrackDialogState.EditTask(task)
    }

    fun showConfirmDeleteDialog(itemType: DeleteItemType, title: String, itemData: Any) {
        _activeDialog.value = TrackDialogState.ConfirmDelete(itemType, title, itemData)
    }

    fun dismissDialog() {
        _activeDialog.value = null
    }

    // --- Subject Operations ---
    fun saveSubject(
        id: Long = 0,
        title: String,
        emoji: String,
        colorIndex: Int,
        targetDate: Long?
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                val newSubject = Subject(
                    title = title.trim(),
                    emoji = emoji,
                    colorIndex = colorIndex,
                    targetDate = targetDate
                )
                val newId = repository.insertSubject(newSubject)
                // Otomatis buka kartu Subject yang baru dibuat
                _expandedSubjectIds.value = _expandedSubjectIds.value + newId
            } else {
                val existing = Subject(
                    id = id,
                    title = title.trim(),
                    emoji = emoji,
                    colorIndex = colorIndex,
                    targetDate = targetDate
                )
                repository.updateSubject(existing)
            }
            dismissDialog()
        }
    }

    fun deleteSubject(subjectProgress: SubjectProgress) {
        viewModelScope.launch {
            lastDeletedItem = DeletedItem.SubjectItem(subjectProgress)
            repository.deleteSubject(subjectProgress.subject.id)
            dismissDialog()
            _snackbarEvents.emit(
                SnackbarMessage(
                    message = "Mata kuliah \"${subjectProgress.subject.title}\" berhasil dihapus"
                )
            )
        }
    }

    // --- SubTopic Operations ---
    fun saveSubTopic(
        id: Long = 0,
        subjectId: Long,
        title: String
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                val newSubTopic = SubTopic(
                    subjectId = subjectId,
                    title = title.trim()
                )
                val newId = repository.insertSubTopic(newSubTopic)
                // Otomatis buka subtopic baru dan pastikan subject induknya terbuka
                _expandedSubjectIds.value = _expandedSubjectIds.value + subjectId
                _expandedSubTopicIds.value = _expandedSubTopicIds.value + newId
            } else {
                val existing = SubTopic(
                    id = id,
                    subjectId = subjectId,
                    title = title.trim()
                )
                repository.updateSubTopic(existing)
            }
            dismissDialog()
        }
    }

    fun deleteSubTopic(subTopicProgress: SubTopicProgress) {
        viewModelScope.launch {
            lastDeletedItem = DeletedItem.SubTopicItem(subTopicProgress)
            repository.deleteSubTopic(subTopicProgress.subTopic.id)
            dismissDialog()
            _snackbarEvents.emit(
                SnackbarMessage(
                    message = "Sub-materi \"${subTopicProgress.subTopic.title}\" berhasil dihapus"
                )
            )
        }
    }

    // --- Task Operations ---
    fun saveTask(
        id: Long = 0,
        subTopicId: Long,
        title: String
    ) {
        viewModelScope.launch {
            if (id == 0L) {
                val newTask = StudyTask(
                    subTopicId = subTopicId,
                    title = title.trim()
                )
                repository.insertTask(newTask)
                _expandedSubTopicIds.value = _expandedSubTopicIds.value + subTopicId
            } else {
                val existing = StudyTask(
                    id = id,
                    subTopicId = subTopicId,
                    title = title.trim()
                )
                repository.updateTask(existing)
            }
            dismissDialog()
        }
    }

    fun deleteTask(task: StudyTask) {
        viewModelScope.launch {
            lastDeletedItem = DeletedItem.TaskItem(task)
            repository.deleteTask(task.id)
            dismissDialog()
            _snackbarEvents.emit(
                SnackbarMessage(
                    message = "Materi \"${task.title}\" berhasil dihapus"
                )
            )
        }
    }

    fun toggleTaskDone(taskId: Long, isDone: Boolean) {
        viewModelScope.launch {
            repository.setTaskDone(taskId, isDone)
        }
    }

    // --- Undo Deletion ---
    fun undoDelete() {
        val itemToRestore = lastDeletedItem ?: return
        viewModelScope.launch {
            when (itemToRestore) {
                is DeletedItem.SubjectItem -> {
                    repository.restoreSubject(itemToRestore.data)
                }
                is DeletedItem.SubTopicItem -> {
                    repository.restoreSubTopic(itemToRestore.data)
                }
                is DeletedItem.TaskItem -> {
                    repository.restoreTask(itemToRestore.data)
                }
            }
            lastDeletedItem = null
        }
    }
}
