package com.apptrackstudy.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.ui.theme.AppTrackStudyTheme

/**
 * Grid kartu statistik pencapaian belajar pada Tab Profil.
 * Tersusun dalam tata letak 2 kolom yang simetris dengan angka besar dan ikon rounded.
 */
@Composable
fun StatCardsSection(
    stats: StudyStats,
    onTimeAssignments: Int = 0,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Statistik Belajar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )

        // Baris 1: Total Materi & Materi Selesai
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SingleStatItem(
                title = "Total Materi",
                value = stats.totalTasks.toString(),
                icon = Icons.AutoMirrored.Filled.FormatListBulleted,
                iconColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            SingleStatItem(
                title = "Materi Selesai",
                value = stats.completedTasks.toString(),
                icon = Icons.Default.CheckCircle,
                iconColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Baris 2: Progress Total & Mata Kuliah
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SingleStatItem(
                title = "Progress Total",
                value = "${stats.overallProgressPercentage.toInt()}%",
                icon = Icons.Default.Percent,
                iconColor = Color(0xFF6366F1),
                modifier = Modifier.weight(1f)
            )

            SingleStatItem(
                title = "Mata Kuliah",
                value = stats.totalSubjects.toString(),
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconColor = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Baris 3: Streak Belajar & Tugas Tepat Waktu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SingleStatItem(
                title = "Streak Belajar",
                value = "${stats.streakDays} Hari",
                icon = Icons.Default.LocalFireDepartment,
                iconColor = Color(0xFFFF6D00),
                modifier = Modifier.weight(1f)
            )

            SingleStatItem(
                title = "Tugas Tepat Waktu",
                value = onTimeAssignments.toString(),
                icon = Icons.Default.AssignmentTurnedIn,
                iconColor = Color(0xFF0284C7),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SingleStatItem(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

@Preview(name = "Stat Cards Light", showBackground = true)
@Composable
private fun StatCardsSectionPreviewLight() {
    val sampleStats = StudyStats(
        totalTasks = 24,
        completedTasks = 16,
        totalSubjects = 4,
        completedToday = 5,
        streakDays = 7
    )

    AppTrackStudyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            StatCardsSection(stats = sampleStats, onTimeAssignments = 3)
        }
    }
}
