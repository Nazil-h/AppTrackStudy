package com.apptrackstudy.ui.track.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.ButtonDefaults
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
import com.apptrackstudy.ui.theme.PillShape
import com.apptrackstudy.ui.theme.getSubjectColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Kartu Utama Subject (Mata Kuliah) dengan strip warna aksen di sisi kiri.
 * Dilengkapi animasi expand/collapse halus, rotasi panah 180°, dan tombol pill.
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
    onStartTaskTimerClick: (StudyTask) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subject = subjectProgress.subject
    val accentColor = getSubjectColor(subject.colorIndex)
    var showMenu by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 280),
        label = "arrowRotationSubject"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            // Strip Warna Aksen Sisi Kiri
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            // Konten Kartu
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Bar Subject
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = onToggleExpand,
                            onLongClick = { showMenu = true }
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lingkaran Emoji Pastel
                    Box(
                        modifier = Modifier
                            .size(46.dp)
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

                    // Tombol Panah Expand/Collapse (Animasi Rotasi)
                    IconButton(
                        onClick = onToggleExpand,
                        modifier = Modifier.size(40.dp)
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
                            modifier = Modifier.size(40.dp)
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

                // Progress Bar Subject Melengkung
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
                    enter = expandVertically(animationSpec = tween(280)) + fadeIn(animationSpec = tween(280)),
                    exit = shrinkVertically(animationSpec = tween(240)) + fadeOut(animationSpec = tween(240))
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
                                    onDeleteTaskClick = onDeleteTaskClick,
                                    onStartTaskTimerClick = onStartTaskTimerClick
                                )
                            }
                        } else {
                            // Empty state jika mata kuliah belum punya sub-materi
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            ) {
                                Text(
                                    text = "Mata kuliah ini belum memiliki sub-materi. Tekan tombol di bawah untuk membuat sub-materi pertama.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Tombol Tambah Sub-materi berbentuk Pill
                        OutlinedButton(
                            onClick = onAddSubTopicClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = PillShape,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = accentColor
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Tambah Sub-materi",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}
