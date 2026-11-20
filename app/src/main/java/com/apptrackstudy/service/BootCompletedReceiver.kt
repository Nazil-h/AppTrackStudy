package com.apptrackstudy.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.apptrackstudy.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * BroadcastReceiver untuk menjadwalkan ulang alarm pengingat tugas setelah HP restart.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AppDatabase.DATABASE_NAME
                )
                    .addMigrations(AppDatabase.MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()

                val pendingTasks = db.assignmentDao().getAllActivePendingList()
                val now = System.currentTimeMillis()

                for (task in pendingTasks) {
                    if (task.deadline > now) {
                        AssignmentAlarmScheduler.scheduleNextReminder(
                            context = context.applicationContext,
                            assignmentId = task.id,
                            title = task.title,
                            deadlineMillis = task.deadline
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}
