package com.apptrackstudy.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.apptrackstudy.MainActivity
import com.apptrackstudy.R
import com.apptrackstudy.domain.util.AssignmentReminderCalculator

/**
 * BroadcastReceiver untuk menerima alarm pengingat tugas kuliah dari AlarmManager.
 * Menampilkan notifikasi prioritas tinggi dan secara berantai menjadwalkan pengingat berikutnya.
 */
class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null) return

        val assignmentId = intent.getLongExtra(AssignmentAlarmScheduler.EXTRA_ASSIGNMENT_ID, -1L)
        val title = intent.getStringExtra(AssignmentAlarmScheduler.EXTRA_ASSIGNMENT_TITLE) ?: "Tugas Kuliah"
        val deadline = intent.getLongExtra(AssignmentAlarmScheduler.EXTRA_ASSIGNMENT_DEADLINE, 0L)

        if (assignmentId == -1L || deadline <= 0L) return

        // 1. Tampilkan notifikasi jika izin notifikasi diberikan
        showReminderNotification(context, assignmentId, title, deadline)

        // 2. Berantai: Jadwalkan pengingat selanjutnya jika masih ada waktu sebelum deadline
        AssignmentAlarmScheduler.scheduleNextReminder(
            context = context,
            assignmentId = assignmentId,
            title = title,
            deadlineMillis = deadline
        )
    }

    private fun showReminderNotification(
        context: Context,
        assignmentId: Long,
        title: String,
        deadline: Long
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) return
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat Tugas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi pengingat tenggat waktu / deadline tugas kuliah"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val remainingText = AssignmentReminderCalculator.formatRemainingTime(deadline)

        // Tap notifikasi membuka MainActivity dan diarahkan ke Tab Tugas
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_NAVIGATE_TO_ASSIGNMENTS, true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            assignmentId.toInt(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Pengingat Deadline Tugas 📝")
            .setContentText("$title — $remainingText")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(assignmentId.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "assignment_reminder_channel"
        const val EXTRA_NAVIGATE_TO_ASSIGNMENTS = "extra_navigate_to_assignments"
    }
}
