package com.apptrackstudy.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.ui.components.ConfirmDeleteDialog
import com.apptrackstudy.ui.components.JejakIlmuTopAppBar
import com.apptrackstudy.ui.components.TextInputDialog
import com.apptrackstudy.ui.profile.components.AboutAppDialog
import com.apptrackstudy.ui.profile.components.BadgeGridSection
import com.apptrackstudy.ui.profile.components.ProfileHeader
import com.apptrackstudy.ui.profile.components.ReminderTimePickerDialog
import com.apptrackstudy.ui.profile.components.StatCardsSection
import com.apptrackstudy.ui.profile.components.ThemeSelectionDialog
import com.apptrackstudy.ui.profile.components.UserLevelCard
import com.apptrackstudy.ui.profile.components.WeeklyActivityChart
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

/**
 * Tab 3: Profil & Pengaturan
 * Menampilkan:
 * 1. Header Profil (Avatar inisial & Edit Nama)
 * 2. Kartu Level & XP (10 XP/task, 5 XP/menit fokus)
 * 3. Statistik Belajar Utama (Materi, Selesai, Streak)
 * 4. Grafik Batang Aktivitas 7 Hari Terakhir (Canvas Compose murni)
 * 5. Koleksi Badge Pencapaian
 * 6. Pengaturan: Mode Tema, Notifikasi Pengingat + Jam, Reset Data, Tentang Aplikasi
 */
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessages.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            JejakIlmuTopAppBar(title = "Profil & Pengaturan")
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // 1. Header Profil (Avatar inisial & Nama)
                    item {
                        ProfileHeader(
                            userName = uiState.userName,
                            onEditNameClick = { viewModel.showEditNameDialog() }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 2. Kartu Level & XP Mahasiswa (Fitur 4)
                    item {
                        UserLevelCard(userLevel = uiState.userLevel)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. Kartu Statistik Belajar
                    item {
                        StatCardsSection(
                            stats = uiState.stats,
                            onTimeAssignments = uiState.onTimeAssignments
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // 4. Grafik Batang Aktivitas 7 Hari Terakhir (Fitur 5)
                    item {
                        WeeklyActivityChart(activities = uiState.weeklyActivities)
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // 5. Koleksi Badge Pencapaian (Fitur 4)
                    item {
                        BadgeGridSection(badges = uiState.userLevel.badges)
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // 6. Bagian Pengaturan Aplikasi (Fitur 6 & Sistem)
                    item {
                        Text(
                            text = "Pengaturan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                        )

                        val themeLabel = when (uiState.themeMode) {
                            UserPreferencesRepository.THEME_LIGHT -> "Mode Terang"
                            UserPreferencesRepository.THEME_DARK -> "Mode Gelap"
                            else -> "Ikuti Sistem"
                        }

                        val formattedReminderTime = String.format(
                            Locale.getDefault(),
                            "%02d:%02d WIB",
                            uiState.reminderHour,
                            uiState.reminderMinute
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Pilihan Tema
                                SettingClickableItem(
                                    icon = Icons.Default.DarkMode,
                                    title = "Mode Tema",
                                    subtitle = themeLabel,
                                    onClick = { viewModel.showThemeSelectionDialog() }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )

                                // Toggle Pengingat Harian (WorkManager)
                                SettingSwitchItem(
                                    icon = Icons.Default.Notifications,
                                    title = "Pengingat Belajar Harian",
                                    subtitle = if (uiState.dailyReminderEnabled) "Aktif setiap $formattedReminderTime" else "Dinonaktifkan",
                                    isChecked = uiState.dailyReminderEnabled,
                                    onCheckedChange = { viewModel.toggleDailyReminder(it) }
                                )

                                // Pengaturan Jam Pengingat (hanya tampil/bisa di-tap jika pengingat aktif)
                                if (uiState.dailyReminderEnabled) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )

                                    SettingClickableItem(
                                        icon = Icons.Default.AccessTime,
                                        title = "Jam Pengingat",
                                        subtitle = formattedReminderTime,
                                        onClick = { viewModel.showReminderTimeDialog() }
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )

                                // Tentang Aplikasi
                                SettingClickableItem(
                                    icon = Icons.Default.Info,
                                    title = "Tentang Aplikasi",
                                    subtitle = "Jejak Ilmu v1.0.0",
                                    onClick = { viewModel.showAboutDialog() }
                                )

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                )

                                // Reset Semua Data
                                SettingClickableItem(
                                    icon = Icons.Default.DeleteForever,
                                    title = "Reset Semua Data",
                                    subtitle = "Hapus seluruh mata kuliah & progress",
                                    isDestructive = true,
                                    onClick = { viewModel.showConfirmResetDialog() }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // --- Dialog-dialog ---
            when (uiState.activeDialog) {
                is ProfileDialogState.EditName -> {
                    TextInputDialog(
                        dialogTitle = "Ganti Nama Panggilan",
                        initialValue = uiState.userName,
                        label = "Nama",
                        placeholder = "Masukkan nama baru...",
                        onConfirm = { newName -> viewModel.updateUserName(newName) },
                        onDismiss = { viewModel.dismissDialog() }
                    )
                }

                is ProfileDialogState.ThemeSelection -> {
                    ThemeSelectionDialog(
                        currentTheme = uiState.themeMode,
                        onSelectTheme = { mode -> viewModel.updateThemeMode(mode) },
                        onDismiss = { viewModel.dismissDialog() }
                    )
                }

                is ProfileDialogState.EditReminderTime -> {
                    ReminderTimePickerDialog(
                        initialHour = uiState.reminderHour,
                        initialMinute = uiState.reminderMinute,
                        onConfirm = { hour, minute ->
                            viewModel.updateReminderTime(hour, minute)
                        },
                        onDismiss = { viewModel.dismissDialog() }
                    )
                }

                is ProfileDialogState.ConfirmResetData -> {
                    ConfirmDeleteDialog(
                        title = "Reset Semua Data",
                        message = "Apakah kamu yakin ingin mereset semua data pembelajaran? Semua mata kuliah, sub-materi, dan riwayat checklist akan dihapus secara permanen.",
                        onConfirm = { viewModel.resetAllData() },
                        onDismiss = { viewModel.dismissDialog() }
                    )
                }

                is ProfileDialogState.AboutApp -> {
                    AboutAppDialog(
                        onDismiss = { viewModel.dismissDialog() }
                    )
                }

                null -> { /* Tidak ada dialog */ }
            }
        }
    }
}

@Composable
private fun SettingClickableItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isDestructive) MaterialTheme.colorScheme.error.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SettingSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
