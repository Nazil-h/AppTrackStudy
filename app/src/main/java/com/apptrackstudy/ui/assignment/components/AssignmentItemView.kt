package com.apptrackstudy.ui.assignment.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.Assignment
import com.apptrackstudy.domain.util.AssignmentReminderCalculator
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import com.apptrackstudy.ui.theme.PillShape
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Item kartu tugas kuliah dengan checkbox, deadline, sisa waktu ramah pembaca,
 * peringatan tenggat waktu, dan menu titik tiga (edit/hapus).
 */
@Composable
fun AssignmentItemView(
    assignment: Assignment,
    onCheckedChange: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val now = remember { System.currentTimeMillis() }
    val remainingMillis = assignment.deadline - now
    val isOverdue = !assignment.isDone && remainingMillis < 0
    val isUrgent = !assignment.isDone && remainingMillis in 0..TimeUnit.HOURS.toMillis(24)

    val dateFormat = remember { SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault()) }
    val deadlineFormatted = remember(assignment.deadline) {
        dateFormat.format(Date(assignment.deadline))
    }
    val remainingText = remember(assignment.deadline, assignment.isDone) {
        if (assignment.isDone) "Selesai" else AssignmentReminderCalculator.formatRemainingTime(assignment.deadline, now)
    }

    var menuExpanded by remember { mutableStateOf(false) }

    val cardBgColor by animateColorAsState(
        targetValue = when {
            assignment.isDone -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            isOverdue -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.22f)
            isUrgent -> Color(0xFFFEF3C7).copy(alpha = 0.45f) // Soft Amber
            else -> MaterialTheme.colorScheme.surface
        },
        label = "assignmentCardBg"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardBgColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (assignment.isDone) 0.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox Tugas
            Checkbox(
                checked = assignment.isDone,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Info Tugas: Nama, Deadline & Badge Sisa Waktu
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = assignment.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (assignment.isDone) FontWeight.Normal else FontWeight.SemiBold,
                    textDecoration = if (assignment.isDone) TextDecoration.LineThrough else TextDecoration.None,
                    color = when {
                        assignment.isDone -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
                        isOverdue -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Deadline teks
                    Text(
                        text = deadlineFormatted,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Badge Sisa Waktu / Peringatan dalam Pill
                    Surface(
                        shape = PillShape,
                        color = when {
                            assignment.isDone -> Color(0xFF10B981).copy(alpha = 0.15f)
                            isOverdue -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                            isUrgent -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isOverdue) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            } else if (isUrgent) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Text(
                                text = remainingText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    assignment.isDone -> Color(0xFF047857)
                                    isOverdue -> MaterialTheme.colorScheme.error
                                    isUrgent -> Color(0xFFB45309)
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Menu Titik Tiga
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opsi tugas",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            menuExpanded = false
                            onEditClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Hapus", color = MaterialTheme.colorScheme.error) },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        },
                        onClick = {
                            menuExpanded = false
                            onDeleteClick()
                        }
                    )
                }
            }
        }
    }
}

@Preview(name = "Assignment Item Light", showBackground = true)
@Composable
private fun AssignmentItemPreviewLight() {
    val sample = Assignment(
        id = 1,
        title = "Makalah Metodologi Penelitian",
        deadline = System.currentTimeMillis() + 3600000 * 18
    )
    AppTrackStudyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            AssignmentItemView(
                assignment = sample,
                onCheckedChange = {},
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}
