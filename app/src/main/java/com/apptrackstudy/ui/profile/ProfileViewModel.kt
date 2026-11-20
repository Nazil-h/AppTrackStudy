package com.apptrackstudy.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.domain.model.DailyActivity
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.domain.model.UserLevel
import com.apptrackstudy.domain.repository.AssignmentRepository
import com.apptrackstudy.domain.repository.StudyRepository
import com.apptrackstudy.worker.DailyReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

sealed interface ProfileDialogState {
    data object EditName : ProfileDialogState
    data object ThemeSelection : ProfileDialogState
    data object EditReminderTime : ProfileDialogState
    data object ConfirmResetData : ProfileDialogState
    data object AboutApp : ProfileDialogState
}

private data class ProfilePreferencesState(
    val userName: String,
    val themeMode: String,
    val dailyReminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int
)

data class ProfileUiState(
    val userName: String = UserPreferencesRepository.DEFAULT_USER_NAME,
    val themeMode: String = UserPreferencesRepository.THEME_SYSTEM,
    val dailyReminderEnabled: Boolean = true,
    val reminderHour: Int = UserPreferencesRepository.DEFAULT_REMINDER_HOUR,
    val reminderMinute: Int = UserPreferencesRepository.DEFAULT_REMINDER_MINUTE,
    val stats: StudyStats = StudyStats(),
    val onTimeAssignments: Int = 0,
    val userLevel: UserLevel = UserLevel(),
    val weeklyActivities: List<DailyActivity> = emptyList(),
    val activeDialog: ProfileDialogState? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val studyRepository: StudyRepository,
    private val assignmentRepository: AssignmentRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    private val _activeDialog = MutableStateFlow<ProfileDialogState?>(null)

    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages: SharedFlow<String> = _snackbarMessages.asSharedFlow()

    private val preferencesFlow = combine(
        preferencesRepository.userName,
        preferencesRepository.themeMode,
        preferencesRepository.dailyReminderEnabled,
        preferencesRepository.dailyReminderHour,
        preferencesRepository.dailyReminderMinute
    ) { name, theme, reminderEnabled, hour, minute ->
        ProfilePreferencesState(
            userName = name,
            themeMode = theme,
            dailyReminderEnabled = reminderEnabled,
            reminderHour = hour,
            reminderMinute = minute
        )
    }

    private val statsAndActivitiesFlow = combine(
        preferencesRepository.totalFocusMinutes,
        studyRepository.getStudyStats(),
        studyRepository.getWeeklyActivities(),
        assignmentRepository.getCompletedOnTimeCount()
    ) { focusMinutes, stats, weeklyActivities, onTimeAssignments ->
        Tuple4(focusMinutes, stats, weeklyActivities, onTimeAssignments)
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        preferencesFlow,
        statsAndActivitiesFlow,
        _activeDialog
    ) { prefs, (focusMinutes, stats, weeklyActivities, onTimeCount), dialog ->
        val userLevel = UserLevel.calculate(
            completedTasks = stats.completedTasks,
            totalFocusMinutes = focusMinutes,
            streakDays = stats.streakDays,
            onTimeAssignments = onTimeCount
        )

        ProfileUiState(
            userName = prefs.userName,
            themeMode = prefs.themeMode,
            dailyReminderEnabled = prefs.dailyReminderEnabled,
            reminderHour = prefs.reminderHour,
            reminderMinute = prefs.reminderMinute,
            stats = stats,
            onTimeAssignments = onTimeCount,
            userLevel = userLevel,
            weeklyActivities = weeklyActivities,
            activeDialog = dialog,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(isLoading = true)
    )

    // --- Dialog Controls ---
    fun showEditNameDialog() { _activeDialog.value = ProfileDialogState.EditName }
    fun showThemeSelectionDialog() { _activeDialog.value = ProfileDialogState.ThemeSelection }
    fun showReminderTimeDialog() { _activeDialog.value = ProfileDialogState.EditReminderTime }
    fun showConfirmResetDialog() { _activeDialog.value = ProfileDialogState.ConfirmResetData }
    fun showAboutDialog() { _activeDialog.value = ProfileDialogState.AboutApp }
    fun dismissDialog() { _activeDialog.value = null }

    // --- User Actions ---
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            preferencesRepository.setUserName(newName)
            dismissDialog()
            _snackbarMessages.emit("Nama berhasil diperbarui")
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
            dismissDialog()
        }
    }

    fun toggleDailyReminder(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setDailyReminderEnabled(enabled)
            val currentState = uiState.value
            if (enabled) {
                DailyReminderScheduler.scheduleDailyReminder(
                    context,
                    currentState.reminderHour,
                    currentState.reminderMinute
                )
            } else {
                DailyReminderScheduler.cancelDailyReminder(context)
            }
            val status = if (enabled) "diaktifkan" else "dinonaktifkan"
            _snackbarMessages.emit("Pengingat harian $status")
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.setDailyReminderTime(hour, minute)
            dismissDialog()
            if (uiState.value.dailyReminderEnabled) {
                DailyReminderScheduler.scheduleDailyReminder(context, hour, minute)
            }
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            _snackbarMessages.emit("Jam pengingat disetel ke $formattedTime")
        }
    }

    fun resetAllData() {
        viewModelScope.launch {
            studyRepository.clearAllData()
            preferencesRepository.clearUserPreferences()
            dismissDialog()
            _snackbarMessages.emit("Semua data berhasil direset")
        }
    }
}

private data class Tuple4<A, B, C, D>(
    val a: A,
    val b: B,
    val c: C,
    val d: D
)
