package com.apptrackstudy.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/**
 * Helper scheduler untuk mengatur pengingat belajar harian via WorkManager.
 */
object DailyReminderScheduler {

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val now = LocalDateTime.now()
        var targetTime = now.with(LocalTime.of(hour.coerceIn(0, 23), minute.coerceIn(0, 59), 0))
        if (!targetTime.isAfter(now)) {
            targetTime = targetTime.plusDays(1)
        }

        val initialDelayMinutes = Duration.between(now, targetTime).toMinutes().coerceAtLeast(1)

        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DailyReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            workRequest
        )
    }

    fun cancelDailyReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DailyReminderWorker.WORK_NAME)
    }
}
