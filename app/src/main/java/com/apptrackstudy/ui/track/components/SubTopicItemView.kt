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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.SubTopicProgress
import com.apptrackstudy.ui.components.ProgressBarWithPercentage

/**
 * Kartu Sub-materi (SubTopic) dengan visual tree guide line (garis penuntun tipis vertikal)
 * untuk memperjelas hubungan hierarki antara SubTopic dan Task-task di dalamnya.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubTopicItemView(
    subTopicProgress: SubTopicProgress,
    isExpanded: Boolean,
    accentColor: Color,
    onToggleExpand: () -> Unit,
    onAddTaskClick: () -> Unit,
    onEditSubTopicClick: () -> Unit,
    onDeleteSubTopicClick: () -> Unit,
    onToggleTaskDone: (taskId: Long, isDone: Boolean) -> Unit,
    onEditTaskClick: (StudyTask) -> Unit,
    onDeleteTaskClick: (StudyTask) -> Unit,
    onStartTaskTimerClick: (StudyTask) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val subTopic = subTopicProgress.subTopic
    var showMenu by remember { mutableStateOf(false) }

    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 260),
        label = "arrowRotationSubTopic"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar SubTopic
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = onToggleExpand,
                        onLongClick = { showMenu = true }
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Aksen Titik Warna Bulat
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Judul SubTopic & Info singkat progress
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subTopic.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (subTopicProgress.totalTasksCount > 0) {
                        Text(
                            text = "${subTopicProgress.completedTasksCount}/${subTopicProgress.totalTasksCount} materi selesai (${subTopicProgress.progressPercentage.toInt()}%)",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (subTopicProgress.isCompleted) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                // Tombol Panah Expand/Collapse (Animasi Rotasi)
                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Tutup sub-materi" else "Buka sub-materi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
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
                            contentDescription = "Menu opsi sub-materi",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tambah Materi Inti") },
                            onClick = {
                                showMenu = false
                                onAddTaskClick()
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
                            text = { Text("Edit Sub-materi") },
                            onClick = {
                                showMenu = false
                                onEditSubTopicClick()
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
                            text = { Text("Hapus Sub-materi", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                showMenu = false
                                onDeleteSubTopicClick()
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

            // Konten Task dengan Visual Tree Guide Line
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(260)) + fadeIn(animationSpec = tween(260)),
                exit = shrinkVertically(animationSpec = tween(220)) + fadeOut(animationSpec = tween(220))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    if (subTopicProgress.totalTasksCount > 0) {
                        // Mini progress bar untuk sub-materi
                        ProgressBarWithPercentage(
                            progressPercentage = subTopicProgress.progressPercentage,
                            completedTasks = subTopicProgress.completedTasksCount,
                            totalTasks = subTopicProgress.totalTasksCount,
                            accentColor = accentColor,
                            trackHeight = 5.dp,
                            showCountText = false,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
                        )

                        // Daftar Task dengan Garis Penuntun Hierarki (Tree Guide Line)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(start = 4.dp)
                        ) {
                            // Garis Penuntun Hierarki Tipis
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
                                    .width(2.dp)
                                    .fillMaxHeight()
                                    .background(accentColor.copy(alpha = 0.28f))
                            )

                            // Kolom Daftar Tugas
                            Column(modifier = Modifier.weight(1f)) {
                                subTopicProgress.tasks.forEach { task ->
                                    TaskItemView(
                                        task = task,
                                        accentColor = accentColor,
                                        onToggleDone = { isDone -> onToggleTaskDone(task.id, isDone) },
                                        onEditClick = { onEditTaskClick(task) },
                                        onDeleteClick = { onDeleteTaskClick(task) },
                                        onStartTimerClick = { onStartTaskTimerClick(task) }
                                    )
                                }
                            }
                        }
                    } else {
                        // Empty state jika belum ada task di subtopic ini
                        Text(
                            text = "Belum ada checklist materi inti. Tekan tombol di bawah untuk menambah.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    // Tombol Tambah Task
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onAddTaskClick,
                            modifier = Modifier.height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Tambah Materi",
                                style = MaterialTheme.typography.labelMedium,
                                color = accentColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
