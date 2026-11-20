package com.apptrackstudy.ui.track

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopicProgress
import com.apptrackstudy.ui.components.ConfirmDeleteDialog
import com.apptrackstudy.ui.components.EmptyStateView
import com.apptrackstudy.ui.components.JejakIlmuTopAppBar
import com.apptrackstudy.ui.components.SubjectFormDialog
import com.apptrackstudy.ui.components.TextInputDialog
import com.apptrackstudy.ui.track.components.SubjectCardView
import kotlinx.coroutines.flow.collectLatest

/**
 * Layar Peta Belajar (Study Track) - Tab 2.
 * Menampilkan hierarki 3 level: Subject -> SubTopic -> Task.
 * Mendukung expand/collapse (tahan rotasi layar), CRUD di setiap level,
 * checkbox penyelesaian task dengan animasi strikethrough, update progress real-time,
 * dan Snackbar dengan aksi "Urungkan" saat penghapusan.
 */
@Composable
fun TrackScreen(
    modifier: Modifier = Modifier,
    viewModel: TrackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Menangani Event Snackbar Undo
    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collectLatest { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            }
        }
    }

    Scaffold(
        topBar = {
            JejakIlmuTopAppBar(
                title = "Peta Belajar",
                actions = {
                    IconButton(onClick = { viewModel.showAddSubjectDialog() }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Mata Kuliah",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showAddSubjectDialog() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = { Text("Mata Kuliah") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = MaterialTheme.shapes.large
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                uiState.subjects.isEmpty() -> {
                    EmptyStateView(
                        title = "Peta Belajar Masih Kosong",
                        description = "Mulai petualangan belajarmu dengan membuat mata kuliah atau materi utama pertamamu!",
                        icon = Icons.Outlined.AccountTree,
                        actionButtonText = "Tambah Mata Kuliah Pertama",
                        onActionClick = { viewModel.showAddSubjectDialog() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(
                            items = uiState.subjects,
                            key = { it.subject.id }
                        ) { subjectProgress ->
                            val isExpanded = uiState.expandedSubjectIds.contains(subjectProgress.subject.id)

                            SubjectCardView(
                                subjectProgress = subjectProgress,
                                isExpanded = isExpanded,
                                expandedSubTopicIds = uiState.expandedSubTopicIds,
                                onToggleExpand = { viewModel.toggleSubjectExpanded(subjectProgress.subject.id) },
                                onToggleSubTopicExpand = { subTopicId ->
                                    viewModel.toggleSubTopicExpanded(subTopicId)
                                },
                                onAddSubTopicClick = {
                                    viewModel.showAddSubTopicDialog(
                                        subjectProgress.subject.id,
                                        subjectProgress.subject.title
                                    )
                                },
                                onEditSubjectClick = {
                                    viewModel.showEditSubjectDialog(subjectProgress.subject)
                                },
                                onDeleteSubjectClick = {
                                    viewModel.showConfirmDeleteDialog(
                                        itemType = DeleteItemType.SUBJECT,
                                        title = subjectProgress.subject.title,
                                        itemData = subjectProgress
                                    )
                                },
                                onAddTaskClick = { subTopicId, subTopicTitle ->
                                    viewModel.showAddTaskDialog(subTopicId, subTopicTitle)
                                },
                                onEditSubTopicClick = { subTopic ->
                                    viewModel.showEditSubTopicDialog(subTopic)
                                },
                                onDeleteSubTopicClick = { subTopicProgress ->
                                    viewModel.showConfirmDeleteDialog(
                                        itemType = DeleteItemType.SUB_TOPIC,
                                        title = subTopicProgress.subTopic.title,
                                        itemData = subTopicProgress
                                    )
                                },
                                onToggleTaskDone = { taskId, isDone ->
                                    viewModel.toggleTaskDone(taskId, isDone)
                                },
                                onEditTaskClick = { task ->
                                    viewModel.showEditTaskDialog(task)
                                },
                                onDeleteTaskClick = { task ->
                                    viewModel.showConfirmDeleteDialog(
                                        itemType = DeleteItemType.TASK,
                                        title = task.title,
                                        itemData = task
                                    )
                                }
                            )
                        }

                        // Jarak bawah agar konten terbawah tidak tertutup FAB
                        item {
                            Spacer(modifier = Modifier.height(88.dp))
                        }
                    }
                }
            }
        }

        // --- Render Dialog Aktif ---
        when (val dialog = uiState.activeDialog) {
            is TrackDialogState.AddSubject -> {
                SubjectFormDialog(
                    onSave = { title, emoji, colorIndex, targetDate ->
                        viewModel.saveSubject(
                            title = title,
                            emoji = emoji,
                            colorIndex = colorIndex,
                            targetDate = targetDate
                        )
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            is TrackDialogState.EditSubject -> {
                SubjectFormDialog(
                    initialSubject = dialog.subject,
                    onSave = { title, emoji, colorIndex, targetDate ->
                        viewModel.saveSubject(
                            id = dialog.subject.id,
                            title = title,
                            emoji = emoji,
                            colorIndex = colorIndex,
                            targetDate = targetDate
                        )
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            is TrackDialogState.AddSubTopic -> {
                TextInputDialog(
                    dialogTitle = "Tambah Sub-materi (${dialog.subjectTitle})",
                    label = "Nama Sub-materi",
                    placeholder = "Contoh: Bab 1 - Pengantar",
                    onConfirm = { title ->
                        viewModel.saveSubTopic(subjectId = dialog.subjectId, title = title)
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            is TrackDialogState.EditSubTopic -> {
                TextInputDialog(
                    dialogTitle = "Edit Sub-materi",
                    initialValue = dialog.subTopic.title,
                    label = "Nama Sub-materi",
                    onConfirm = { title ->
                        viewModel.saveSubTopic(
                            id = dialog.subTopic.id,
                            subjectId = dialog.subTopic.subjectId,
                            title = title
                        )
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            is TrackDialogState.AddTask -> {
                TextInputDialog(
                    dialogTitle = "Tambah Materi Inti (${dialog.subTopicTitle})",
                    label = "Checklist Materi",
                    placeholder = "Contoh: Pahami konsep dasar variabel",
                    onConfirm = { title ->
                        viewModel.saveTask(subTopicId = dialog.subTopicId, title = title)
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            is TrackDialogState.EditTask -> {
                TextInputDialog(
                    dialogTitle = "Edit Materi Inti",
                    initialValue = dialog.task.title,
                    label = "Checklist Materi",
                    onConfirm = { title ->
                        viewModel.saveTask(
                            id = dialog.task.id,
                            subTopicId = dialog.task.subTopicId,
                            title = title
                        )
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            is TrackDialogState.ConfirmDelete -> {
                val deleteMessage = when (dialog.itemType) {
                    DeleteItemType.SUBJECT ->
                        "Apakah kamu yakin ingin menghapus mata kuliah \"${dialog.title}\"? Seluruh sub-materi dan checklist materi di dalamnya juga akan terhapus."
                    DeleteItemType.SUB_TOPIC ->
                        "Apakah kamu yakin ingin menghapus sub-materi \"${dialog.title}\"? Seluruh checklist materi di dalamnya juga akan terhapus."
                    DeleteItemType.TASK ->
                        "Apakah kamu yakin ingin menghapus materi \"${dialog.title}\"?"
                }

                ConfirmDeleteDialog(
                    title = "Konfirmasi Hapus",
                    message = deleteMessage,
                    onConfirm = {
                        when (dialog.itemType) {
                            DeleteItemType.SUBJECT -> {
                                viewModel.deleteSubject(dialog.itemData as SubjectProgress)
                            }
                            DeleteItemType.SUB_TOPIC -> {
                                viewModel.deleteSubTopic(dialog.itemData as SubTopicProgress)
                            }
                            DeleteItemType.TASK -> {
                                viewModel.deleteTask(dialog.itemData as StudyTask)
                            }
                        }
                    },
                    onDismiss = { viewModel.dismissDialog() }
                )
            }

            null -> { /* Tidak ada dialog aktif */ }
        }
    }
}
