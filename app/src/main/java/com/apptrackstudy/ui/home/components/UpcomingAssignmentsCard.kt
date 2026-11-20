package com.apptrackstudy.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.Assignment
import com.apptrackstudy.domain.util.AssignmentReminderCalculator
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import com.apptrackstudy.ui.theme.PillShape
import java.util.concurrent.TimeUnit

/**
 * Kartu ringkasan "Deadline Terdekat" (maks 3 tugas) di tab Beranda.
 */
@Composable
fun UpcomingAssignmentsCard(
    assignments: List<Assignment>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Kartu
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Deadline Terdekat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(PillShape)
                        .clickable(onClick = onViewAllClick)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lihat Semua",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (assignments.isEmpty()) {
                Text(
                    text = "Tidak ada tenggat waktu tugas dalam waktu dekat. Santai sejenak! ☕",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    assignments.take(3).forEachIndexed { index, assignment ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        UpcomingAssignmentItem(
                            assignment = assignment,
                            onClick = onViewAllClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpcomingAssignmentItem(
    assignment: Assignment,
    onClick: () -> Unit
) {
    val now = remember { System.currentTimeMillis() }
    val remainingMillis = assignment.deadline - now
    val isOverdue = remainingMillis < 0
    val isUrgent = remainingMillis in 0..TimeUnit.HOURS.toMillis(24)

    val remainingText = remember(assignment.deadline) {
        AssignmentReminderCalculator.formatRemainingTime(assignment.deadline, now)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = assignment.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = PillShape,
            color = when {
                isOverdue -> MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                isUrgent -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            }
        ) {
            Text(
                text = remainingText,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = when {
                    isOverdue -> MaterialTheme.colorScheme.error
                    isUrgent -> Color(0xFFB45309)
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 3.dp),
                fontSize = 10.sp
            )
        }
    }
}

@Preview(name = "Upcoming Assignments Light", showBackground = true)
@Composable
private fun UpcomingAssignmentsPreviewLight() {
    val sample = listOf(
        Assignment(id = 1, title = "Laporan Praktikum Fisika", deadline = System.currentTimeMillis() + 3600000 * 5),
        Assignment(id = 2, title = "Makalah Metodologi Penelitian", deadline = System.currentTimeMillis() + 3600000 * 36)
    )

    AppTrackStudyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            UpcomingAssignmentsCard(assignments = sample, onViewAllClick = {})
        }
    }
}
