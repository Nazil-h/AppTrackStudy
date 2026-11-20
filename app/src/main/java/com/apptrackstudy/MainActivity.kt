package com.apptrackstudy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.ui.MainScreen
import com.apptrackstudy.ui.MainViewModel
import com.apptrackstudy.ui.theme.AppTrackStudyTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Single-Activity utama untuk aplikasi Jejak Ilmu.
 * Mendukung Hilt Dependency Injection dan konfigurasi Dynamic Theme.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            val isDarkTheme = when (themeMode) {
                UserPreferencesRepository.THEME_LIGHT -> false
                UserPreferencesRepository.THEME_DARK -> true
                else -> isSystemInDarkTheme()
            }

            AppTrackStudyTheme(darkTheme = isDarkTheme) {
                MainScreen()
            }
        }
    }
}
