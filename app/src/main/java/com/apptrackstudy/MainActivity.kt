package com.apptrackstudy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.service.FocusTimerService
import com.apptrackstudy.service.TaskReminderReceiver
import com.apptrackstudy.ui.MainScreen
import com.apptrackstudy.ui.MainViewModel
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity utama untuk aplikasi Jejak Ilmu.
 * Mendukung Hilt Dependency Injection, Dynamic Theme, dan Intent handling notifikasi timer & tugas.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        requestNotificationPermissionIfNeeded()
        handleNotificationIntents(intent)

        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                UserPreferencesRepository.THEME_LIGHT -> false
                UserPreferencesRepository.THEME_DARK -> true
                else -> isSystemInDarkTheme()
            }

            AppTrackStudyTheme(darkTheme = isDarkTheme) {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntents(intent)
    }

    private fun handleNotificationIntents(intent: Intent?) {
        if (intent?.getBooleanExtra(FocusTimerService.EXTRA_OPEN_TIMER, false) == true) {
            viewModel.openTimer()
        }
        if (intent?.getBooleanExtra(TaskReminderReceiver.EXTRA_NAVIGATE_TO_ASSIGNMENTS, false) == true) {
            viewModel.navigateToAssignmentsTab()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
