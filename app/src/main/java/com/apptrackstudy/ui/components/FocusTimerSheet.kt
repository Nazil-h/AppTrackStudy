package com.apptrackstudy.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.FocusTimerData
import com.apptrackstudy.domain.model.TimerMode
import com.apptrackstudy.domain.model.TimerStatus

private val WorkDurationPresets = listOf(15, 20, 25, 30, 45, 60)
private val BreakDurationPresets = listOf(3, 5, 10, 15)

/**
 * Bottom Sheet interaktif untuk Timer Fokus Pomodoro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusTimerSheet(
    timerData: FocusTimerData,
    onStart: (workMin: Int, breakMin: Int) -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onSwitchMode: () -> Unit,
    onUpdateDurations: (workMin: Int, breakMin: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSettings by remember { mutableStateOf(false) }

    val accentColor = if (timerData.mode == TimerMode.WORK) {
        MaterialTheme.colorScheme.primary
    } else {
        Color(0xFF0D9488) // Teal santai untuk istirahat
    }

    val animatedProgress by animateFloatAsState(
        targetValue = timerData.progress,
        label = "timerProgressAnimation"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (timerData.mode == TimerMode.WORK) {
                                Icons.Default.SelfImprovement
                            } else {
                                Icons.Default.FreeBreakfast
                            },
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = timerData.mode.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row {
                    if (timerData.status == TimerStatus.IDLE) {
                        IconButton(onClick = { showSettings = !showSettings }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan durasi",
                                tint = if (showSettings) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Label Materi Terkait jika ada
            if (!timerData.linkedTaskTitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = accentColor.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Materi:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = timerData.linkedTaskTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Timer Lingkaran Besar
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = { 1f },
                    modifier = Modifier.size(220.dp),
                    color = accentColor.copy(alpha = 0.12f),
                    strokeWidth = 10.dp
                )

                // Active Progress Track
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(220.dp),
                    color = accentColor,
                    strokeWidth = 10.dp
                )

                // Angka Countdown MM:SS
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timerData.formattedTime,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (timerData.status) {
                            TimerStatus.RUNNING -> "Sedang Berjalan..."
                            TimerStatus.PAUSED -> "Dijeda"
                            TimerStatus.COMPLETED -> "Selesai! 🎉"
                            TimerStatus.IDLE -> if (timerData.mode == TimerMode.WORK) "Siap Fokus" else "Waktunya Istirahat"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pengaturan Durasi (Expandable saat IDLE)
            AnimatedVisibility(visible = showSettings && timerData.status == TimerStatus.IDLE) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    Text(
                        text = "Durasi Sesi Fokus (Menit)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(WorkDurationPresets) { minutes ->
                            FilterChip(
                                selected = timerData.workDurationMinutes == minutes,
                                onClick = {
                                    onUpdateDurations(minutes, timerData.breakDurationMinutes)
                                },
                                label = { Text("${minutes}m") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Durasi Istirahat (Menit)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(BreakDurationPresets) { minutes ->
                            FilterChip(
                                selected = timerData.breakDurationMinutes == minutes,
                                onClick = {
                                    onUpdateDurations(timerData.workDurationMinutes, minutes)
                                },
                                label = { Text("${minutes}m") }
                            )
                        }
                    }
                }
            }

            // Tombol Kontrol Utama
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Stop / Reset
                if (timerData.status != TimerStatus.IDLE) {
                    IconButton(
                        onClick = onStop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset timer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }

                // Tombol Mulai / Jeda / Lanjut
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                        .clickable {
                            when (timerData.status) {
                                TimerStatus.IDLE, TimerStatus.COMPLETED -> onStart(
                                    timerData.workDurationMinutes,
                                    timerData.breakDurationMinutes
                                )
                                TimerStatus.RUNNING -> onPause()
                                TimerStatus.PAUSED -> onResume()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (timerData.status) {
                            TimerStatus.RUNNING -> Icons.Default.Pause
                            else -> Icons.Default.PlayArrow
                        },
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Tombol Ganti Mode (Fokus <-> Istirahat)
                if (timerData.status == TimerStatus.IDLE) {
                    Spacer(modifier = Modifier.width(20.dp))
                    FilledTonalButton(
                        onClick = onSwitchMode,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            text = if (timerData.mode == TimerMode.WORK) "Ke Istirahat" else "Ke Fokus",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}
