package com.apptrackstudy.ui.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptrackstudy.domain.model.Assignment
import com.apptrackstudy.domain.model.AssignmentGroup
import com.apptrackstudy.domain.repository.AssignmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AssignmentUiState(
    val groupedAssignments: Map<AssignmentGroup, List<Assignment>> = emptyMap(),
    val totalCount: Int = 0,
    val pendingCount: Int = 0,
    val isLoading: Boolean = false
)

sealed interface AssignmentEvent {
    data class ShowUndoSnackbar(val message: String, val deletedAssignment: Assignment) : AssignmentEvent
    data class ShowSnackbar(val message: String) : AssignmentEvent
}

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) : ViewModel() {

    private val _eventFlow = MutableSharedFlow<AssignmentEvent>()
    val eventFlow: SharedFlow<AssignmentEvent> = _eventFlow.asSharedFlow()

    private val _showAddEditSheet = MutableStateFlow(false)
    val showAddEditSheet: StateFlow<Boolean> = _showAddEditSheet.asStateFlow()

    private val _editingAssignment = MutableStateFlow<Assignment?>(null)
    val editingAssignment: StateFlow<Assignment?> = _editingAssignment.asStateFlow()

    val uiState: StateFlow<AssignmentUiState> = assignmentRepository.getAllAssignments()
        .map { assignments ->
            val grouped = groupAssignments(assignments)
            AssignmentUiState(
                groupedAssignments = grouped,
                totalCount = assignments.size,
                pendingCount = assignments.count { !it.isDone },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AssignmentUiState(isLoading = true)
        )

    fun openAddSheet() {
        _editingAssignment.value = null
        _showAddEditSheet.value = true
    }

    fun openEditSheet(assignment: Assignment) {
        _editingAssignment.value = assignment
        _showAddEditSheet.value = true
    }

    fun dismissAddEditSheet() {
        _showAddEditSheet.value = false
        _editingAssignment.value = null
    }

    fun saveAssignment(title: String, deadlineMillis: Long) {
        val currentEdit = _editingAssignment.value
        viewModelScope.launch {
            if (currentEdit != null) {
                assignmentRepository.updateAssignment(
                    currentEdit.copy(
                        title = title,
                        deadline = deadlineMillis
                    )
                )
                _eventFlow.emit(AssignmentEvent.ShowSnackbar("Tugas berhasil diperbarui"))
            } else {
                assignmentRepository.insertAssignment(
                    Assignment(
                        title = title,
                        deadline = deadlineMillis
                    )
                )
                _eventFlow.emit(AssignmentEvent.ShowSnackbar("Tugas baru berhasil ditambahkan"))
            }
            dismissAddEditSheet()
        }
    }

    fun toggleAssignmentDone(assignment: Assignment, isDone: Boolean) {
        viewModelScope.launch {
            assignmentRepository.setAssignmentDone(assignment.id, isDone)
            if (isDone) {
                _eventFlow.emit(AssignmentEvent.ShowSnackbar("Tugas '${assignment.title}' selesai! 🎉 (+15 XP jika tepat waktu)"))
            }
        }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            assignmentRepository.deleteAssignment(assignment.id)
            _eventFlow.emit(
                AssignmentEvent.ShowUndoSnackbar(
                    message = "Tugas '${assignment.title}' dihapus",
                    deletedAssignment = assignment
                )
            )
        }
    }

    fun undoDelete(assignment: Assignment) {
        viewModelScope.launch {
            assignmentRepository.restoreAssignment(assignment)
            _eventFlow.emit(AssignmentEvent.ShowSnackbar("Tugas '${assignment.title}' dipulihkan"))
        }
    }

    companion object {
        fun groupAssignments(assignments: List<Assignment>): Map<AssignmentGroup, List<Assignment>> {
            val now = System.currentTimeMillis()

            val todayStart = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todayEnd = Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
                set(Calendar.SECOND, 59)
                set(Calendar.MILLISECOND, 999)
            }.timeInMillis

            val weekEnd = todayEnd + (6 * 24 * 60 * 60 * 1000L)

            val overdueList = mutableListOf<Assignment>()
            val todayList = mutableListOf<Assignment>()
            val thisWeekList = mutableListOf<Assignment>()
            val laterList = mutableListOf<Assignment>()
            val doneList = mutableListOf<Assignment>()

            // Urutkan berdasarkan deadline terdekat
            val sorted = assignments.sortedBy { it.deadline }

            for (assignment in sorted) {
                when {
                    assignment.isDone -> doneList.add(assignment)
                    assignment.deadline < now -> overdueList.add(assignment)
                    assignment.deadline in todayStart..todayEnd -> todayList.add(assignment)
                    assignment.deadline in (todayEnd + 1)..weekEnd -> thisWeekList.add(assignment)
                    else -> laterList.add(assignment)
                }
            }

            val map = linkedMapOf<AssignmentGroup, List<Assignment>>()
            if (overdueList.isNotEmpty()) map[AssignmentGroup.OVERDUE] = overdueList
            if (todayList.isNotEmpty()) map[AssignmentGroup.TODAY] = todayList
            if (thisWeekList.isNotEmpty()) map[AssignmentGroup.THIS_WEEK] = thisWeekList
            if (laterList.isNotEmpty()) map[AssignmentGroup.LATER] = laterList
            if (doneList.isNotEmpty()) map[AssignmentGroup.DONE] = doneList

            return map
        }
    }
}
