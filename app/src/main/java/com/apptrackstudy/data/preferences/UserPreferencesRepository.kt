package com.apptrackstudy.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository untuk mengelola preferensi pengguna via DataStore (offline & reaktif).
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val dataStore = context.userDataStore

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val THEME_MODE = stringPreferencesKey("theme_mode") // SYSTEM, LIGHT, DARK
        val DAILY_REMINDER = booleanPreferencesKey("daily_reminder")
        val DAILY_REMINDER_HOUR = intPreferencesKey("daily_reminder_hour")
        val DAILY_REMINDER_MINUTE = intPreferencesKey("daily_reminder_minute")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val TOTAL_FOCUS_MINUTES = intPreferencesKey("total_focus_minutes")
    }

    /**
     * Nama pengguna. Default bernilai "Mahasiswa".
     */
    val userName: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.USER_NAME] ?: DEFAULT_USER_NAME
        }

    /**
     * Mode tema: "SYSTEM", "LIGHT", atau "DARK". Default "SYSTEM".
     */
    val themeMode: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.THEME_MODE] ?: THEME_SYSTEM
        }

    /**
     * Status notifikasi pengingat harian.
     */
    val dailyReminderEnabled: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER] ?: true
        }

    /**
     * Status apakah onboarding / input nama pertama kali sudah selesai.
     */
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] ?: false
        }

    /**
     * Total menit fokus yang telah diakumulasi dari sesi timer Pomodoro.
     */
    val totalFocusMinutes: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.TOTAL_FOCUS_MINUTES] ?: 0
        }

    /**
     * Jam pengingat harian (0-23). Default: 19 (jam 7 malam).
     */
    val dailyReminderHour: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] ?: DEFAULT_REMINDER_HOUR
        }

    /**
     * Menit pengingat harian (0-59). Default: 0.
     */
    val dailyReminderMinute: Flow<Int> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] ?: DEFAULT_REMINDER_MINUTE
        }

    suspend fun setUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name.trim().ifEmpty { DEFAULT_USER_NAME }
        }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER] = enabled
        }
    }

    suspend fun setDailyReminderTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DAILY_REMINDER_HOUR] = hour
            preferences[PreferencesKeys.DAILY_REMINDER_MINUTE] = minute
        }
    }

    suspend fun addFocusMinutes(minutes: Int) {
        if (minutes <= 0) return
        dataStore.edit { preferences ->
            val current = preferences[PreferencesKeys.TOTAL_FOCUS_MINUTES] ?: 0
            preferences[PreferencesKeys.TOTAL_FOCUS_MINUTES] = current + minutes
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun clearUserPreferences() {
        dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.TOTAL_FOCUS_MINUTES)
        }
    }

    companion object {
        const val DEFAULT_USER_NAME = "Mahasiswa"
        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"
        const val DEFAULT_REMINDER_HOUR = 19
        const val DEFAULT_REMINDER_MINUTE = 0
    }
}
