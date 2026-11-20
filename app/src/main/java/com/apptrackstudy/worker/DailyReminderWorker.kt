package com.apptrackstudy.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.apptrackstudy.MainActivity
import com.apptrackstudy.R

class DailyReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        // Cek izin notifikasi untuk Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val isGranted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!isGranted) return Result.success()
        }

        sendReminderNotification()
        return Result.success()
    }

    private fun sendReminderNotification() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Pengingat Belajar Harian",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifikasi pengingat sesi belajar harian"
            enableVibration(true)
        }
        notificationManager.createNotificationChannel(channel)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val quotes = listOf(
            "Luangkan waktu sejenak untuk mencicil materi kuliahmu hari ini! 📚",
            "Konsistensi harian adalah kunci sukses mahasiswa berprestasi. Yuk belajar! 💡",
            "Sedikit progres hari ini jauh lebih baik daripada tidak sama sekali. Semangat! 🔥",
            "Target belajarmu sudah menanti. Yuk tuntaskan checklist materimu! 🎯"
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Waktunya Belajar! 🎓")
            .setContentText(quotes.random())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "daily_reminder_channel"
        const val NOTIFICATION_ID = 2001
        const val WORK_NAME = "daily_study_reminder_work"
    }
}
