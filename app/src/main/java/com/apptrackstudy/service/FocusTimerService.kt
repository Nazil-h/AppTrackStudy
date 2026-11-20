package com.apptrackstudy.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.apptrackstudy.MainActivity
import com.apptrackstudy.R
import com.apptrackstudy.data.preferences.UserPreferencesRepository
import com.apptrackstudy.domain.model.FocusTimerData
import com.apptrackstudy.domain.model.TimerMode
import com.apptrackstudy.domain.model.TimerStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusTimerService : Service() {

    @Inject
    lateinit var timerManager: FocusTimerManager

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var timerJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "JejakIlmu:FocusTimerWakeLock"
        ).apply {
            setReferenceCounted(false)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L).takeIf { it != -1L }
                val taskTitle = intent.getStringExtra(EXTRA_TASK_TITLE)
                val workMin = intent.getIntExtra(EXTRA_WORK_MINUTES, 25)
                val breakMin = intent.getIntExtra(EXTRA_BREAK_MINUTES, 5)
                startNewTimer(taskId, taskTitle, workMin, breakMin)
            }
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_STOP -> stopTimer()
            ACTION_SWITCH_MODE -> {
                val modeName = intent.getStringExtra(EXTRA_NEW_MODE) ?: TimerMode.WORK.name
                val newMode = TimerMode.valueOf(modeName)
                switchMode(newMode)
            }
        }
        return START_NOT_STICKY
    }

    private fun startNewTimer(
        taskId: Long?,
        taskTitle: String?,
        workMin: Int,
        breakMin: Int
    ) {
        val totalSec = workMin * 60
        val state = FocusTimerData(
            status = TimerStatus.RUNNING,
            mode = TimerMode.WORK,
            totalSeconds = totalSec,
            remainingSeconds = totalSec,
            linkedTaskId = taskId,
            linkedTaskTitle = taskTitle,
            workDurationMinutes = workMin,
            breakDurationMinutes = breakMin
        )
        timerManager.updateState(state)

        startForegroundServiceWithNotification(state)
        startCountdown()
    }

    private fun startCountdown() {
        timerJob?.cancel()
        wakeLock?.acquire(3 * 60 * 60 * 1000L) // Safety timeout 3 jam

        timerJob = serviceScope.launch(Dispatchers.Default) {
            while (isActive) {
                delay(1000L)
                val current = timerManager.timerState.value
                if (current.status == TimerStatus.RUNNING) {
                    val newRemaining = current.remainingSeconds - 1
                    if (newRemaining <= 0) {
                        onTimerFinished(current)
                        break
                    } else {
                        val updated = current.copy(remainingSeconds = newRemaining)
                        timerManager.updateState(updated)
                        updateForegroundNotification(updated)
                    }
                }
            }
        }
    }

    private fun pauseTimer() {
        val current = timerManager.timerState.value
        if (current.status == TimerStatus.RUNNING) {
            val paused = current.copy(status = TimerStatus.PAUSED)
            timerManager.updateState(paused)
            updateForegroundNotification(paused)
            if (wakeLock?.isHeld == true) wakeLock?.release()
        }
    }

    private fun resumeTimer() {
        val current = timerManager.timerState.value
        if (current.status == TimerStatus.PAUSED) {
            val resumed = current.copy(status = TimerStatus.RUNNING)
            timerManager.updateState(resumed)
            updateForegroundNotification(resumed)
            startCountdown()
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        if (wakeLock?.isHeld == true) wakeLock?.release()

        val current = timerManager.timerState.value
        val defaultSec = current.workDurationMinutes * 60
        timerManager.updateState(
            current.copy(
                status = TimerStatus.IDLE,
                mode = TimerMode.WORK,
                totalSeconds = defaultSec,
                remainingSeconds = defaultSec
            )
        )

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun switchMode(newMode: TimerMode) {
        timerJob?.cancel()
        val current = timerManager.timerState.value
        val durationSec = if (newMode == TimerMode.WORK) {
            current.workDurationMinutes * 60
        } else {
            current.breakDurationMinutes * 60
        }

        val updated = current.copy(
            status = TimerStatus.IDLE,
            mode = newMode,
            totalSeconds = durationSec,
            remainingSeconds = durationSec
        )
        timerManager.updateState(updated)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun onTimerFinished(finishedData: FocusTimerData) {
        if (wakeLock?.isHeld == true) wakeLock?.release()

        val nextMode = if (finishedData.mode == TimerMode.WORK) TimerMode.BREAK else TimerMode.WORK
        val nextDurationSec = if (nextMode == TimerMode.WORK) {
            finishedData.workDurationMinutes * 60
        } else {
            finishedData.breakDurationMinutes * 60
        }

        val completedState = finishedData.copy(
            status = TimerStatus.COMPLETED,
            remainingSeconds = 0,
            mode = nextMode,
            totalSeconds = nextDurationSec
        )
        timerManager.updateState(completedState)

        if (finishedData.mode == TimerMode.WORK) {
            serviceScope.launch {
                preferencesRepository.addFocusMinutes(finishedData.workDurationMinutes)
            }
        }

        // Tampilkan notifikasi perayaan selesai
        sendCompletionNotification(finishedData)

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun startForegroundServiceWithNotification(state: FocusTimerData) {
        val notification = buildTimerNotification(state)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val foregroundServiceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            }
            startForeground(NOTIFICATION_ID, notification, foregroundServiceType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun updateForegroundNotification(state: FocusTimerData) {
        val notification = buildTimerNotification(state)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun buildTimerNotification(state: FocusTimerData): Notification {
        val title = if (!state.linkedTaskTitle.isNullOrBlank()) {
            "${state.mode.title} • ${state.linkedTaskTitle}"
        } else {
            state.mode.title
        }

        val contentText = "${state.formattedTime} tersisa"

        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_TIMER, true)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeAction = if (state.status == TimerStatus.RUNNING) {
            val pauseIntent = Intent(this, FocusTimerService::class.java).apply {
                action = ACTION_PAUSE
            }
            val pausePendingIntent = PendingIntent.getService(
                this,
                1,
                pauseIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_pause,
                "Jeda",
                pausePendingIntent
            ).build()
        } else {
            val resumeIntent = Intent(this, FocusTimerService::class.java).apply {
                action = ACTION_RESUME
            }
            val resumePendingIntent = PendingIntent.getService(
                this,
                2,
                resumeIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play,
                "Lanjut",
                resumePendingIntent
            ).build()
        }

        val stopIntent = Intent(this, FocusTimerService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            3,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Berhenti",
            stopPendingIntent
        ).build()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(contentText)
            .setProgress(state.totalSeconds, state.totalSeconds - state.remainingSeconds, false)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(state.status == TimerStatus.RUNNING)
            .setOnlyAlertOnce(true)
            .addAction(pauseResumeAction)
            .addAction(stopAction)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun sendCompletionNotification(finishedData: FocusTimerData) {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_OPEN_TIMER, true)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            4,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = if (finishedData.mode == TimerMode.WORK) {
            "Sesi fokus selesai! Waktunya istirahat sejenak 5 menit. 🎉"
        } else {
            "Waktu istirahat selesai! Siap untuk sesi fokus berikutnya? 💪"
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Waktu Habis! ⏰")
            .setContentText(message)
            .setContentIntent(openAppPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(COMPLETION_NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Timer Fokus",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Notifikasi status timer fokus Pomodoro"
            setShowBadge(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        if (wakeLock?.isHeld == true) wakeLock?.release()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "focus_timer_channel"
        const val NOTIFICATION_ID = 1001
        const val COMPLETION_NOTIFICATION_ID = 1002

        const val ACTION_START = "com.apptrackstudy.ACTION_START_TIMER"
        const val ACTION_PAUSE = "com.apptrackstudy.ACTION_PAUSE_TIMER"
        const val ACTION_RESUME = "com.apptrackstudy.ACTION_RESUME_TIMER"
        const val ACTION_STOP = "com.apptrackstudy.ACTION_STOP_TIMER"
        const val ACTION_SWITCH_MODE = "com.apptrackstudy.ACTION_SWITCH_MODE"

        const val EXTRA_TASK_ID = "extra_task_id"
        const val EXTRA_TASK_TITLE = "extra_task_title"
        const val EXTRA_WORK_MINUTES = "extra_work_minutes"
        const val EXTRA_BREAK_MINUTES = "extra_break_minutes"
        const val EXTRA_NEW_MODE = "extra_new_mode"
        const val EXTRA_OPEN_TIMER = "extra_open_timer"
    }
}
