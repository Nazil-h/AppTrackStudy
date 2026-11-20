package com.apptrackstudy.domain.util

import com.apptrackstudy.domain.model.DailyActivity
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

object StreakCalculator {

    /**
     * Menghitung streak belajar harian secara akurat berdasarkan daftar epoch timestamp (doneAt) dari Task.
     * Aturan:
     * 1. Jika ada task selesai hari ini, streak dihitung mundur berurutan dari hari ini.
     * 2. Jika hari ini belum ada task selesai tetapi kemarin ada, streak kemarin tetap aktif (belum putus).
     * 3. Jika kemarin dan hari ini tidak ada task selesai, streak = 0.
     */
    fun calculateStreak(
        doneAtTimestamps: List<Long>,
        zoneId: ZoneId = ZoneId.systemDefault(),
        currentDate: LocalDate = LocalDate.now(zoneId)
    ): Int {
        if (doneAtTimestamps.isEmpty()) return 0

        val distinctDates: Set<LocalDate> = doneAtTimestamps.map { timestamp ->
            Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate()
        }.toSet()

        val yesterday = currentDate.minusDays(1)

        var checkDate = when {
            distinctDates.contains(currentDate) -> currentDate
            distinctDates.contains(yesterday) -> yesterday
            else -> return 0
        }

        var streak = 0
        while (distinctDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }

    /**
     * Menghitung jumlah materi yang selesai pada tanggal tertentu (default hari ini).
     */
    fun countCompletedOnDate(
        doneAtTimestamps: List<Long>,
        zoneId: ZoneId = ZoneId.systemDefault(),
        targetDate: LocalDate = LocalDate.now(zoneId)
    ): Int {
        return doneAtTimestamps.count { timestamp ->
            Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate() == targetDate
        }
    }

    /**
     * Menghasilkan daftar aktivitas penyelesaian materi untuk 7 hari terakhir (hari ini - 6 hari sampai hari ini).
     */
    fun getWeeklyActivities(
        doneAtTimestamps: List<Long>,
        zoneId: ZoneId = ZoneId.systemDefault(),
        currentDate: LocalDate = LocalDate.now(zoneId)
    ): List<DailyActivity> {
        val completedDates = doneAtTimestamps.map { timestamp ->
            Instant.ofEpochMilli(timestamp).atZone(zoneId).toLocalDate()
        }

        val countsByDate = completedDates.groupingBy { it }.eachCount()

        return (6 downTo 0).map { daysAgo ->
            val date = currentDate.minusDays(daysAgo.toLong())
            val dayLabel = when (date.dayOfWeek) {
                DayOfWeek.MONDAY -> "Sen"
                DayOfWeek.TUESDAY -> "Sel"
                DayOfWeek.WEDNESDAY -> "Rab"
                DayOfWeek.THURSDAY -> "Kam"
                DayOfWeek.FRIDAY -> "Jum"
                DayOfWeek.SATURDAY -> "Sab"
                DayOfWeek.SUNDAY -> "Min"
                else -> ""
            }
            DailyActivity(
                date = date,
                dayLabel = dayLabel,
                completedCount = countsByDate[date] ?: 0,
                isToday = daysAgo == 0
            )
        }
    }
}
