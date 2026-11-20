package com.apptrackstudy.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import com.apptrackstudy.ui.theme.PillShape

/**
 * Komponen progress bar animasi dengan persentase dan label jumlah task.
 * Menggunakan sudut pill melengkung penuh dan animasi pengisian yang halus.
 */
@Composable
fun ProgressBarWithPercentage(
    progressPercentage: Float,
    completedTasks: Int,
    totalTasks: Int,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    trackHeight: Dp = 8.dp,
    showCountText: Boolean = true
) {
    // Normalisasi progress 0.0f sampai 1.0f
    val targetProgress = (progressPercentage / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
        label = "progressAnimation"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCountText) {
                Text(
                    text = if (totalTasks > 0) "$completedTasks dari $totalTasks materi selesai" else "Belum ada materi",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Spacer(modifier = Modifier.size(1.dp))
            }

            Text(
                text = "${progressPercentage.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(PillShape),
            color = accentColor,
            trackColor = accentColor.copy(alpha = 0.16f),
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(name = "Light Mode", showBackground = true)
@Composable
private fun ProgressBarPreviewLight() {
    AppTrackStudyTheme(darkTheme = false) {
        Surface(modifier = Modifier.padding(16.dp)) {
            ProgressBarWithPercentage(
                progressPercentage = 65f,
                completedTasks = 13,
                totalTasks = 20
            )
        }
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun ProgressBarPreviewDark() {
    AppTrackStudyTheme(darkTheme = true) {
        Surface(modifier = Modifier.padding(16.dp)) {
            ProgressBarWithPercentage(
                progressPercentage = 80f,
                completedTasks = 8,
                totalTasks = 10
            )
        }
    }
}
