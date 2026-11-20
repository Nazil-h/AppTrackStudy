package com.apptrackstudy.ui.home.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import com.apptrackstudy.ui.theme.PillShape
import java.util.Calendar

/**
 * Kartu sapaan waktu-adaptif (GreetingCard) dengan gradient halus dan ilustrasi/emoji.
 * Warna gradient secara akurat mendeteksi tema aktif (Light/Dark) dari MaterialTheme
 * dan memiliki layout kompak yang tidak memakan tempat berlebih.
 */
@Composable
fun GreetingCard(
    greetingTitle: String,
    greetingEmoji: String,
    greetingMessage: String,
    stats: StudyStats,
    modifier: Modifier = Modifier
) {
    // Deteksi akurat dari tema MaterialTheme yang aktif (bukan hanya sistem OS)
    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }

    // Gradient halus berdasarkan waktu hari & mode tema aktif
    val gradientColors = remember(hour, isDark) {
        if (isDark) {
            when (hour) {
                in 4..10 -> listOf(Color(0xFF2A221A), Color(0xFF1B2423)) // Pagi Dark
                in 11..14 -> listOf(Color(0xFF142723), Color(0xFF192430)) // Siang Dark
                in 15..17 -> listOf(Color(0xFF291B1D), Color(0xFF1B222E)) // Sore Dark
                in 18..21 -> listOf(Color(0xFF151E2E), Color(0xFF131924)) // Malam Dark
                else -> listOf(Color(0xFF191726), Color(0xFF121620)) // Larut Malam Dark
            }
        } else {
            when (hour) {
                in 4..10 -> listOf(Color(0xFFFEF4E5), Color(0xFFFDE8D8)) // Pagi Golden Sunrise
                in 11..14 -> listOf(Color(0xFFE8F8F4), Color(0xFFD8F1EB)) // Siang Solar Teal
                in 15..17 -> listOf(Color(0xFFFDF1E9), Color(0xFFFCE2D6)) // Sore Sunset Peach
                in 18..21 -> listOf(Color(0xFFF0F3FF), Color(0xFFE4EBFE)) // Malam Soft Indigo
                else -> listOf(Color(0xFFF5EEFD), Color(0xFFE8DEFC)) // Larut Malam Quiet Violet
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradientColors))
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header Sapaan + Ilustrasi Emoji
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Wadah Ilustrasi Emoji
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDark) MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                                else Color.White.copy(alpha = 0.85f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = greetingEmoji,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = greetingTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Kalimat motivasi hangat acak
                Text(
                    text = greetingMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Baris Info Kecil: Materi Selesai Hari Ini & Streak Belajar dalam Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Info Materi Selesai Hari Ini
                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.65f else 0.85f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (stats.completedToday > 0) "${stats.completedToday} materi selesai" else "0 materi hari ini",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }

                    // Info Streak Belajar
                    Surface(
                        shape = PillShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.65f else 0.85f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFFF6D00),
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (stats.streakDays > 0) "${stats.streakDays} hari streak" else "Mulai streak!",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Greeting Card Light", showBackground = true)
@Composable
private fun GreetingCardPreviewLight() {
    AppTrackStudyTheme(darkTheme = false) {
        Box(modifier = Modifier.padding(16.dp)) {
            GreetingCard(
                greetingTitle = "Selamat Pagi, Haidar!",
                greetingEmoji = "🌅",
                greetingMessage = "Awali hari dengan secangkir semangat untuk menggapai impianmu hari ini.",
                stats = StudyStats(completedToday = 3, streakDays = 5)
            )
        }
    }
}

@Preview(name = "Greeting Card Dark", showBackground = true)
@Composable
private fun GreetingCardPreviewDark() {
    AppTrackStudyTheme(darkTheme = true) {
        Box(modifier = Modifier.padding(16.dp)) {
            GreetingCard(
                greetingTitle = "Selamat Malam, Haidar!",
                greetingEmoji = "🌙",
                greetingMessage = "Review materi hari ini sebentar sebelum tidur ya. Kerja kerasmu luar biasa!",
                stats = StudyStats(completedToday = 5, streakDays = 7)
            )
        }
    }
}
