package com.apptrackstudy.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.domain.model.FocusTimerData
import com.apptrackstudy.domain.model.TimerStatus
import com.apptrackstudy.service.FocusTimerManager
import com.apptrackstudy.worker.DailyReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val timerManager: FocusTimerManager,
    @param:ApplicationContext private val context: Context
) : ViewModel() {

    val themeMode: StateFlow<String> = preferencesRepository.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferencesRepository.THEME_SYSTEM
        )

    val timerState: StateFlow<FocusTimerData> = timerManager.timerState

    private val _showTimerSheet = MutableStateFlow(false)
    val showTimerSheet: StateFlow<Boolean> = _showTimerSheet.asStateFlow()

    private val _navigateToAssignments = MutableStateFlow(false)
    val navigateToAssignments: StateFlow<Boolean> = _navigateToAssignments.asStateFlow()

    init {
        // Sinkronisasi penjadwalan pengingat harian (WorkManager) secara otomatis
        viewModelScope.launch {
            combine(
                preferencesRepository.dailyReminderEnabled,
                preferencesRepository.dailyReminderHour,
                preferencesRepository.dailyReminderMinute
            ) { enabled, hour, minute ->
                Triple(enabled, hour, minute)
            }.collectLatest { (enabled, hour, minute) ->
                if (enabled) {
                    DailyReminderScheduler.scheduleDailyReminder(context, hour, minute)
                } else {
                    DailyReminderScheduler.cancelDailyReminder(context)
                }
            }
        }
    }

    fun navigateToAssignmentsTab() {
        _navigateToAssignments.value = true
    }

    fun onNavigatedToAssignmentsTab() {
        _navigateToAssignments.value = false
    }

    fun openTimer(taskId: Long? = null, taskTitle: String? = null) {
        if (taskId != null && taskTitle != null) {
            val current = timerManager.timerState.value
            if (current.status == TimerStatus.IDLE) {
                timerManager.updateState(
                    current.copy(
                        linkedTaskId = taskId,
                        linkedTaskTitle = taskTitle
                    )
                )
            }
        }
        _showTimerSheet.value = true
    }

    fun closeTimerSheet() {
        _showTimerSheet.value = false
    }

    fun startTimer(workMinutes: Int, breakMinutes: Int) {
        val current = timerManager.timerState.value
        timerManager.startTimer(
            taskId = current.linkedTaskId,
            taskTitle = current.linkedTaskTitle,
            workMinutes = workMinutes,
            breakMinutes = breakMinutes
        )
    }

    fun pauseTimer() {
        timerManager.pauseTimer()
    }

    fun resumeTimer() {
        timerManager.resumeTimer()
    }

    fun stopTimer() {
        timerManager.stopTimer()
    }

    fun switchMode() {
        timerManager.switchMode()
    }

    fun updateDurations(workMin: Int, breakMin: Int) {
        timerManager.updateDurations(workMin, breakMin)
    }
}
