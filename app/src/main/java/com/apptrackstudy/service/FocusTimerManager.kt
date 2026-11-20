package com.apptrackstudy.service

import android.content.Context
import android.content.Intent
import com.apptrackstudy.domain.model.FocusTimerData
import com.apptrackstudy.domain.model.TimerMode
import com.apptrackstudy.domain.model.TimerStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusTimerManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val _timerState = MutableStateFlow(FocusTimerData())
    val timerState: StateFlow<FocusTimerData> = _timerState.asStateFlow()

    fun updateState(newState: FocusTimerData) {
        _timerState.value = newState
    }

    fun startTimer(
        taskId: Long? = null,
        taskTitle: String? = null,
        workMinutes: Int = _timerState.value.workDurationMinutes,
        breakMinutes: Int = _timerState.value.breakDurationMinutes
    ) {
        val intent = Intent(context, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_START
            putExtra(FocusTimerService.EXTRA_TASK_ID, taskId ?: -1L)
            putExtra(FocusTimerService.EXTRA_TASK_TITLE, taskTitle)
            putExtra(FocusTimerService.EXTRA_WORK_MINUTES, workMinutes)
            putExtra(FocusTimerService.EXTRA_BREAK_MINUTES, breakMinutes)
        }
        context.startForegroundService(intent)
    }

    fun pauseTimer() {
        val intent = Intent(context, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeTimer() {
        val intent = Intent(context, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_RESUME
        }
        context.startService(intent)
    }

    fun stopTimer() {
        val intent = Intent(context, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_STOP
        }
        context.startService(intent)
    }

    fun switchMode() {
        val current = _timerState.value
        val newMode = if (current.mode == TimerMode.WORK) TimerMode.BREAK else TimerMode.WORK
        val duration = if (newMode == TimerMode.WORK) current.workDurationMinutes * 60 else current.breakDurationMinutes * 60

        val intent = Intent(context, FocusTimerService::class.java).apply {
            action = FocusTimerService.ACTION_SWITCH_MODE
            putExtra(FocusTimerService.EXTRA_NEW_MODE, newMode.name)
        }
        context.startService(intent)
    }

    fun updateDurations(workMinutes: Int, breakMinutes: Int) {
        val current = _timerState.value
        if (current.status == TimerStatus.IDLE) {
            val totalSec = if (current.mode == TimerMode.WORK) workMinutes * 60 else breakMinutes * 60
            _timerState.value = current.copy(
                workDurationMinutes = workMinutes,
                breakDurationMinutes = breakMinutes,
                totalSeconds = totalSec,
                remainingSeconds = totalSec
            )
        }
    }
}
