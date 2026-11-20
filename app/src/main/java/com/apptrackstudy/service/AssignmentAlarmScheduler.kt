package com.apptrackstudy.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.apptrackstudy.domain.util.AssignmentReminderCalculator

/**
 * Scheduler berbasis AlarmManager untuk menjadwalkan notifikasi pengingat deadline tugas.
 * Mendukung Android 12+ dengan pengecekan izin alarm eksak (canScheduleExactAlarms)
 * dan fallback otomatis ke alarm tidak eksak jika izin tidak diberikan.
 */
object AssignmentAlarmScheduler {

    const val EXTRA_ASSIGNMENT_ID = "extra_assignment_id"
    const val EXTRA_ASSIGNMENT_TITLE = "extra_assignment_title"
    const val EXTRA_ASSIGNMENT_DEADLINE = "extra_assignment_deadline"

    /**
     * Menjadwalkan pengingat berikutnya untuk sebuah tugas kuliah.
     */
    fun scheduleNextReminder(
        context: Context,
        assignmentId: Long,
        title: String,
        deadlineMillis: Long
    ) {
        val now = System.currentTimeMillis()
        val nextReminder = AssignmentReminderCalculator.calculateNextReminderTime(deadlineMillis, now) ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(EXTRA_ASSIGNMENT_ID, assignmentId)
            putExtra(EXTRA_ASSIGNMENT_TITLE, title)
            putExtra(EXTRA_ASSIGNMENT_DEADLINE, deadlineMillis)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            assignmentId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextReminder,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextReminder,
                pendingIntent
            )
        }
    }

    /**
     * Membatalkan jadwal alarm untuk tugas tertentu (misal saat tugas selesai atau dihapus).
     */
    fun cancelReminder(context: Context, assignmentId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            assignmentId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
