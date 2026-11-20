package com.apptrackstudy.domain.util

import java.util.concurrent.TimeUnit

/**
 * Utility murni untuk menghitung jadwal pengingat tugas kuliah dan memformat sisa waktu.
 */
object AssignmentReminderCalculator {

    val TWELVE_HOURS_MILLIS: Long = TimeUnit.HOURS.toMillis(12)
    val ONE_HOUR_MILLIS: Long = TimeUnit.HOURS.toMillis(1)

    /**
     * Menghitung epoch millis untuk pengingat berikutnya sebelum deadline.
     * Aturan:
     * 1. Jika deadline sudah lewat (remaining <= 0), return null.
     * 2. Pengingat dikirim pada kelipatan 12 jam sebelum deadline (12 jam, 24 jam, 36 jam, dst.)
     *    selama waktu tersebut masih di masa depan (> currentMillis).
     * 3. Jika sisa waktu kurang dari 12 jam (atau kelipatan 12 jam terakhir sudah lewat),
     *    jadwalkan 1 pengingat sekitar 1 jam sebelum deadline jika masih di masa depan.
     * 4. Jika sudah kurang dari 1 jam sebelum deadline, return null.
     */
    fun calculateNextReminderTime(deadlineMillis: Long, currentMillis: Long): Long? {
        val remaining = deadlineMillis - currentMillis
        if (remaining <= 0) return null

        // Hitung kelipatan 12 jam yang masih strictly di masa depan
        // Menggunakan (remaining - 1) memastikan jika tepat pada kelipatan,
        // pengingat berikutnya yang dicari adalah jadwal selanjutnya di masa depan.
        val k = ((remaining - 1) / TWELVE_HOURS_MILLIS).toInt()

        if (k >= 1) {
            val nextReminder = deadlineMillis - (k * TWELVE_HOURS_MILLIS)
            if (nextReminder > currentMillis) {
                return nextReminder
            }
        }

        // Jika sisa waktu < 12 jam atau kelipatan 12 jam berikutnya sudah tidak ada,
        // jadwalkan 1 pengingat 1 jam sebelum deadline
        val oneHourReminder = deadlineMillis - ONE_HOUR_MILLIS
        return if (oneHourReminder > currentMillis) {
            oneHourReminder
        } else {
            null
        }
    }

    /**
     * Menghasilkan teks sisa waktu yang mudah dibaca, misal:
     * - "2 hari 5 jam lagi"
     * - "12 jam lagi"
     * - "45 menit lagi"
     * - "Terlambat 2 jam"
     */
    fun formatRemainingTime(deadlineMillis: Long, currentMillis: Long = System.currentTimeMillis()): String {
        val diff = deadlineMillis - currentMillis
        val isOverdue = diff < 0
        val absDiff = kotlin.math.abs(diff)

        val totalMinutes = TimeUnit.MILLISECONDS.toMinutes(absDiff)
        val totalHours = TimeUnit.MILLISECONDS.toHours(absDiff)
        val days = totalHours / 24
        val hours = totalHours % 24
        val minutes = totalMinutes % 60

        val timeString = when {
            days > 0 -> {
                if (hours > 0) "$days hari $hours jam" else "$days hari"
            }
            totalHours > 0 -> {
                if (minutes > 0) "$totalHours jam $minutes menit" else "$totalHours jam"
            }
            totalMinutes > 0 -> "$totalMinutes menit"
            else -> "kurang dari 1 menit"
        }

        return if (isOverdue) "Terlambat $timeString" else "$timeString lagi"
    }
}
