package com.apptrackstudy.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.Subject
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.model.SubTopicProgress
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.ui.components.ProgressBarWithPercentage
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import com.apptrackstudy.ui.theme.getSubjectColor

/**
 * Kartu ringkasan to-do Subject pada tab Beranda.
 * Menampilkan strip warna aksen di sisi kiri, emoji dalam lingkaran pastel,
 * persentase penyelesaian, dan progress bar melengkung.
 */
@Composable
fun SubjectSummaryCard(
    subjectProgress: SubjectProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subject = subjectProgress.subject
    val accentColor = getSubjectColor(subject.colorIndex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
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
            // Strip Warna Aksen di Sisi Kiri Kartu
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            // Konten Kartu
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lingkaran Emoji Pastel
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subject.emoji,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (subjectProgress.isCompleted) "Selesai 100% 🎉" else "${subjectProgress.progressPercentage.toInt()}% tercapai",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (subjectProgress.isCompleted) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Buka di Peta Belajar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progress bar melengkung halus
                ProgressBarWithPercentage(
                    progressPercentage = subjectProgress.progressPercentage,
                    completedTasks = subjectProgress.completedTasksCount,
                    totalTasks = subjectProgress.totalTasksCount,
                    accentColor = accentColor,
                    trackHeight = 7.dp,
                    showCountText = true
                )
            }
        }
    }
}

@Preview(name = "Subject Summary Light", showBackground = true)
@Composable
private fun SubjectSummaryCardPreviewLight() {
    val sampleProgress = SubjectProgress(
        subject = Subject(
            id = 1,
            title = "Kalkulus Lanjut",
            emoji = "📐",
            colorIndex = 1
        ),
        subTopics = listOf(
            SubTopicProgress(
                subTopic = SubTopic(id = 1, subjectId = 1, title = "Bab 1: Limit"),
                tasks = listOf(
                    StudyTask(id = 1, subTopicId = 1, title = "Materi 1", isDone = true),
                    StudyTask(id = 2, subTopicId = 1, title = "Materi 2", isDone = true),
                    StudyTask(id = 3, subTopicId = 1, title = "Materi 3", isDone = false)
                )
            )
        )
    )

    AppTrackStudyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            SubjectSummaryCard(
                subjectProgress = sampleProgress,
                onClick = {}
            )
        }
    }
}

@Preview(name = "Subject Summary Dark", showBackground = true)
@Composable
private fun SubjectSummaryCardPreviewDark() {
    val sampleProgress = SubjectProgress(
        subject = Subject(
            id = 2,
            title = "Pemrograman Kotlin",
            emoji = "☕",
            colorIndex = 0
        ),
        subTopics = listOf(
            SubTopicProgress(
                subTopic = SubTopic(id = 2, subjectId = 2, title = "Coroutines"),
                tasks = listOf(
                    StudyTask(id = 4, subTopicId = 2, title = "Flow", isDone = true),
                    StudyTask(id = 5, subTopicId = 2, title = "StateFlow", isDone = true)
                )
            )
        )
    )

    AppTrackStudyTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            SubjectSummaryCard(
                subjectProgress = sampleProgress,
                onClick = {}
            )
        }
    }
}
