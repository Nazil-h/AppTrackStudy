package com.apptrackstudy.ui.track.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.model.SubTopicProgress
import com.apptrackstudy.ui.components.ProgressBarWithPercentage
import com.apptrackstudy.ui.theme.getSubjectColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Kartu Utama Subject (Mata Kuliah) yang dapat di-expand/collapse.
 * Berisi emoji, nama mata kuliah, target tanggal, progress bar, dan daftar SubTopic.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubjectCardView(
    subjectProgress: SubjectProgress,
    isExpanded: Boolean,
    expandedSubTopicIds: Set<Long>,
    onToggleExpand: () -> Unit,
    onToggleSubTopicExpand: (subTopicId: Long) -> Unit,
    onAddSubTopicClick: () -> Unit,
    onEditSubjectClick: () -> Unit,
    onDeleteSubjectClick: () -> Unit,
    onAddTaskClick: (subTopicId: Long, subTopicTitle: String) -> Unit,
    onEditSubTopicClick: (SubTopic) -> Unit,
    onDeleteSubTopicClick: (SubTopicProgress) -> Unit,
    onToggleTaskDone: (taskId: Long, isDone: Boolean) -> Unit,
    onEditTaskClick: (StudyTask) -> Unit,
    onDeleteTaskClick: (StudyTask) -> Unit,
    modifier: Modifier = Modifier
) {
    val subject = subjectProgress.subject
    val accentColor = getSubjectColor(subject.colorIndex)
    var showMenu by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "arrowRotationSubject"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Kartu Subject
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onToggleExpand,
                        onLongClick = { showMenu = true }
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lingkaran Emoji dengan background aksen lembut
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subject.emoji,
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Judul Mata Kuliah & Info Target
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subject.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Target tanggal jika ada
                    if (subject.targetDate != null) {
                        val formattedDate = remember(subject.targetDate) {
                            val sdf = SimpleDateFormat("dd MMM yyyy", Locale.forLanguageTag("id-ID"))
                            sdf.format(Date(subject.targetDate))
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Target: $formattedDate",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Tombol Panah Expand/Collapse
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Tutup mata kuliah" else "Buka mata kuliah",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(arrowRotation)
                    )
                }

                // Menu Opsi Titik Tiga
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu opsi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tambah Sub-materi") },
                            onClick = {
                                showMenu = false
                                onAddSubTopicClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Mata Kuliah") },
                            onClick = {
                                showMenu = false
                                onEditSubjectClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Hapus Mata Kuliah", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDeleteSubjectClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }
            }

            // Progress Bar Subject Keseluruhan
            ProgressBarWithPercentage(
                progressPercentage = subjectProgress.progressPercentage,
                completedTasks = subjectProgress.completedTasksCount,
                totalTasks = subjectProgress.totalTasksCount,
                accentColor = accentColor,
                trackHeight = 7.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Konten SubTopics (Sub-materi) yang dapat di-expand
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    if (subjectProgress.subTopics.isNotEmpty()) {
                        subjectProgress.subTopics.forEach { subTopicProgress ->
                            val isSubExpanded = expandedSubTopicIds.contains(subTopicProgress.subTopic.id)
                            SubTopicItemView(
                                subTopicProgress = subTopicProgress,
                                isExpanded = isSubExpanded,
                                accentColor = accentColor,
                                onToggleExpand = { onToggleSubTopicExpand(subTopicProgress.subTopic.id) },
                                onAddTaskClick = {
                                    onAddTaskClick(subTopicProgress.subTopic.id, subTopicProgress.subTopic.title)
                                },
                                onEditSubTopicClick = { onEditSubTopicClick(subTopicProgress.subTopic) },
                                onDeleteSubTopicClick = { onDeleteSubTopicClick(subTopicProgress) },
                                onToggleTaskDone = onToggleTaskDone,
                                onEditTaskClick = onEditTaskClick,
                                onDeleteTaskClick = onDeleteTaskClick
                            )
                        }
                    } else {
                        // Empty state jika mata kuliah belum punya sub-materi
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = "Mata kuliah ini belum memiliki sub-materi. Tekan tombol di bawah untuk membuat sub-materi pertama.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tombol Tambah Sub-materi
                    OutlinedButton(
                        onClick = onAddSubTopicClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Tambah Sub-materi",
                            color = accentColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SubjectCardViewPreview() {
    val sampleSubjectProgress = SubjectProgress(
        subject = com.apptrackstudy.domain.model.Subject(
            id = 1,
            title = "Kalkulus Lanjut",
            emoji = "📐",
            colorIndex = 1,
            targetDate = System.currentTimeMillis() + 86400000L * 30
        ),
        subTopics = listOf(
            SubTopicProgress(
                subTopic = SubTopic(id = 1, subjectId = 1, title = "Bab 1: Limit & Kekontinuan"),
                tasks = listOf(
                    StudyTask(id = 1, subTopicId = 1, title = "Definisi Epsilon-Delta", isDone = true),
                    StudyTask(id = 2, subTopicId = 1, title = "Teorema Nilai Antara", isDone = false)
                )
            ),
            SubTopicProgress(
                subTopic = SubTopic(id = 2, subjectId = 1, title = "Bab 2: Turunan Parsial"),
                tasks = listOf(
                    StudyTask(id = 3, subTopicId = 2, title = "Aturan Rantai Multivariabel", isDone = true),
                    StudyTask(id = 4, subTopicId = 2, title = "Vektor Gradien & Bidang Singgung", isDone = true)
                )
            )
        )
    )

    com.apptrackstudy.ui.theme.AppTrackStudyTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SubjectCardView(
                subjectProgress = sampleSubjectProgress,
                isExpanded = true,
                expandedSubTopicIds = setOf(1L),
                onToggleExpand = {},
                onToggleSubTopicExpand = {},
                onAddSubTopicClick = {},
                onEditSubjectClick = {},
                onDeleteSubjectClick = {},
                onAddTaskClick = { _, _ -> },
                onEditSubTopicClick = {},
                onDeleteSubTopicClick = {},
                onToggleTaskDone = { _, _ -> },
                onEditTaskClick = {},
                onDeleteTaskClick = {}
            )
        }
    }
}

