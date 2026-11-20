package com.apptrackstudy.domain.model

enum class TimerMode(val title: String) {
    WORK("Sesi Fokus"),
    BREAK("Waktu Istirahat")
}

enum class TimerStatus {
    IDLE, RUNNING, PAUSED, COMPLETED
}

/**
 * State snapshot timer fokus Pomodoro.
 */
data class FocusTimerData(
    val status: TimerStatus = TimerStatus.IDLE,
    val mode: TimerMode = TimerMode.WORK,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val linkedTaskId: Long? = null,
    val linkedTaskTitle: String? = null,
    val workDurationMinutes: Int = 25,
    val breakDurationMinutes: Int = 5
) {
    /**
     * Progress berjalan (0.0f sampai 1.0f).
     */
    val progress: Float
        get() = if (totalSeconds > 0) {
            1f - (remainingSeconds.toFloat() / totalSeconds.toFloat())
        } else {
            0f
        }

    /**
     * Waktu tersisa dalam format MM:SS.
     */
    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "%02d:%02d".format(minutes, seconds)
        }
}
