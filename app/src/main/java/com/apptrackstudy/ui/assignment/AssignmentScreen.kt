package com.apptrackstudy.ui.assignment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptrackstudy.domain.model.AssignmentGroup
import com.apptrackstudy.ui.assignment.components.AddEditAssignmentSheet
import com.apptrackstudy.ui.assignment.components.AssignmentItemView
import com.apptrackstudy.ui.components.EmptyStateView
import com.apptrackstudy.ui.components.JejakIlmuTopAppBar
import kotlinx.coroutines.flow.collectLatest

/**
 * Tab 3 (Baru): Tugas Kuliah (Assignment Tracking)
 * Menampilkan daftar tugas dengan deadline terdekat, pengelompokan waktu,
 * indikator tenggat waktu, dan pengingat berkala.
 */
@Composable
fun AssignmentScreen(
    modifier: Modifier = Modifier,
    viewModel: AssignmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showAddEditSheet by viewModel.showAddEditSheet.collectAsState()
    val editingAssignment by viewModel.editingAssignment.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is AssignmentEvent.ShowUndoSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = "Urungkan",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.undoDelete(event.deletedAssignment)
                    }
                }
                is AssignmentEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            JejakIlmuTopAppBar(
                title = "Tugas Kuliah",
                actions = {
                    FloatingActionButton(
                        onClick = { viewModel.openAddSheet() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(40.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tambah Tugas",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
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
                    com.apptrackstudy.ui.components.ShimmerCardList(
                        count = 3,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                uiState.totalCount == 0 -> {
                    EmptyStateView(
                        title = "Belum Ada Tugas Kuliah",
                        description = "Catat tugas, laporan praktikum, atau kuis agar kamu tidak terlewat deadline penting!",
                        actionButtonText = "Tambah Tugas Pertama",
                        onActionClick = { viewModel.openAddSheet() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Iterasi per grup (Terlambat, Hari ini, Minggu ini, Nanti, Selesai)
                        uiState.groupedAssignments.forEach { (group, assignments) ->
                            item(key = "header_${group.name}") {
                                GroupHeader(group = group, count = assignments.size)
                            }

                            items(
                                items = assignments,
                                key = { it.id }
                            ) { assignment ->
                                AssignmentItemView(
                                    assignment = assignment,
                                    onCheckedChange = { isChecked ->
                                        viewModel.toggleAssignmentDone(assignment, isChecked)
                                    },
                                    onEditClick = { viewModel.openEditSheet(assignment) },
                                    onDeleteClick = { viewModel.deleteAssignment(assignment) }
                                )
                            }

                            item(key = "spacer_${group.name}") {
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }
                }
            }
        }

        // Bottom Sheet Tambah / Edit Tugas
        if (showAddEditSheet) {
            AddEditAssignmentSheet(
                assignment = editingAssignment,
                onDismiss = { viewModel.dismissAddEditSheet() },
                onSave = { title, deadlineMillis ->
                    viewModel.saveAssignment(title, deadlineMillis)
                }
            )
        }
    }
}

@Composable
private fun GroupHeader(
    group: AssignmentGroup,
    count: Int,
    modifier: Modifier = Modifier
) {
    val (headerColor, badgeBgColor, badgeTextColor) = when (group) {
        AssignmentGroup.OVERDUE -> Triple(
            MaterialTheme.colorScheme.error,
            MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
            MaterialTheme.colorScheme.error
        )
        AssignmentGroup.TODAY -> Triple(
            Color(0xFFD97706),
            Color(0xFFFEF3C7),
            Color(0xFFB45309)
        )
        AssignmentGroup.THIS_WEEK -> Triple(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
            MaterialTheme.colorScheme.primary
        )
        AssignmentGroup.LATER -> Triple(
            MaterialTheme.colorScheme.onSurfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        AssignmentGroup.DONE -> Triple(
            Color(0xFF059669),
            Color(0xFFD1FAE5),
            Color(0xFF047857)
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = group.displayName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = headerColor
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = com.apptrackstudy.ui.theme.PillShape,
            color = badgeBgColor
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = badgeTextColor,
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 2.dp),
                fontSize = 11.sp
            )
        }
    }
}
