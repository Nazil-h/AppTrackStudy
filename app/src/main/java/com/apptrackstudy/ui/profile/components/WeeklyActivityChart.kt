package com.apptrackstudy.ui.profile.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.DailyActivity

/**
 * Grafik batang aktivitas 7 hari terakhir menggunakan Compose Canvas murni.
 * Menampilkan jumlah checklist materi yang diselesaikan per hari.
 */
@Composable
fun WeeklyActivityChart(
    activities: List<DailyActivity>,
    modifier: Modifier = Modifier
) {
    val totalThisWeek = activities.sumOf { it.completedCount }
    val maxCount = (activities.maxOfOrNull { it.completedCount } ?: 0).coerceAtLeast(1)

    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(activities) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
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
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(10.dp))

                    Column {
                        Text(
                            text = "Aktivitas 7 Hari Terakhir",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Penyelesaian checklist materi",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "$totalThisWeek selesai",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Canvas Area untuk Grafik Batang
            val primaryColor = MaterialTheme.colorScheme.primary
            val secondaryBarColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
            val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            val currentAnim = animationProgress.value

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val barCount = activities.size.coerceAtLeast(1)
                    val totalAvailableForBars = canvasWidth * 0.75f
                    val barWidth = totalAvailableForBars / barCount
                    val spacing = (canvasWidth - (barWidth * barCount)) / (barCount + 1)

                    activities.forEachIndexed { index, activity ->
                        val x = spacing + index * (barWidth + spacing)
                        val barColor = if (activity.isToday) primaryColor else secondaryBarColor

                        // Gambar track latar belakang
                        drawRoundRect(
                            color = trackColor,
                            topLeft = Offset(x, 0f),
                            size = Size(barWidth, canvasHeight),
                            cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                        )

                        // Gambar batang aktif sesuai rasio count / maxCount
                        val heightFraction = (activity.completedCount.toFloat() / maxCount.toFloat())
                            .coerceIn(0f, 1f) * currentAnim

                        val barHeight = (canvasHeight * heightFraction).coerceAtLeast(
                            if (activity.completedCount > 0) 10f else 0f
                        )
                        val barTop = canvasHeight - barHeight

                        if (barHeight > 0f) {
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(x, barTop),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Baris Label Hari di bawah masing-masing batang
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                activities.forEach { activity ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (activity.completedCount > 0) "${activity.completedCount}" else "-",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (activity.isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (activity.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activity.isToday) "Hari ini" else activity.dayLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (activity.isToday) FontWeight.Bold else FontWeight.Medium,
                            color = if (activity.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(name = "Weekly Chart Light", showBackground = true)
@Composable
private fun WeeklyActivityChartPreviewLight() {
    val now = java.time.LocalDate.now()
    val sample = listOf(
        DailyActivity(now.minusDays(4), "Sen", 2, false),
        DailyActivity(now.minusDays(3), "Sel", 4, false),
        DailyActivity(now.minusDays(2), "Rab", 1, false),
        DailyActivity(now.minusDays(1), "Kam", 5, false),
        DailyActivity(now, "Jum", 3, true)
    )
    com.apptrackstudy.ui.theme.AppTrackStudyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            WeeklyActivityChart(activities = sample)
        }
    }
}

