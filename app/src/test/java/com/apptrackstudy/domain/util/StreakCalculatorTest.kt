package com.apptrackstudy.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class StreakCalculatorTest {

    private val zoneId = ZoneId.of("UTC")
    private val today = LocalDate.of(2026, 11, 20)

    private fun dateToMillis(date: LocalDate): Long {
        return date.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    @Test
    fun calculateStreak_emptyList_returnsZero() {
        val streak = StreakCalculator.calculateStreak(
            doneAtTimestamps = emptyList(),
            zoneId = zoneId,
            currentDate = today
        )
        assertEquals(0, streak)
    }

    @Test
    fun calculateStreak_completedTodayOnly_returnsOne() {
        val timestamps = listOf(dateToMillis(today))
        val streak = StreakCalculator.calculateStreak(
            doneAtTimestamps = timestamps,
            zoneId = zoneId,
            currentDate = today
        )
        assertEquals(1, streak)
    }

    @Test
    fun calculateStreak_consecutiveDaysIncludingToday_returnsCorrectStreak() {
        val timestamps = listOf(
            dateToMillis(today),
            dateToMillis(today.minusDays(1)),
            dateToMillis(today.minusDays(2))
        )
        val streak = StreakCalculator.calculateStreak(
            doneAtTimestamps = timestamps,
            zoneId = zoneId,
            currentDate = today
        )
        assertEquals(3, streak)
    }

    @Test
    fun calculateStreak_completedYesterdayNotToday_streakRemainsActive() {
        val timestamps = listOf(
            dateToMillis(today.minusDays(1)),
            dateToMillis(today.minusDays(2))
        )
        val streak = StreakCalculator.calculateStreak(
            doneAtTimestamps = timestamps,
            zoneId = zoneId,
            currentDate = today
        )
        assertEquals(2, streak)
    }

    @Test
    fun calculateStreak_brokenStreak_returnsZero() {
        // Terakhir belajar 2 hari yang lalu (kemarin dan hari ini kosong)
        val timestamps = listOf(
            dateToMillis(today.minusDays(2)),
            dateToMillis(today.minusDays(3))
        )
        val streak = StreakCalculator.calculateStreak(
            doneAtTimestamps = timestamps,
            zoneId = zoneId,
            currentDate = today
        )
        assertEquals(0, streak)
    }

    @Test
    fun calculateStreak_multipleTasksSameDay_countsAsSingleDay() {
        val morningToday = today.atTime(8, 0).atZone(zoneId).toInstant().toEpochMilli()
        val nightToday = today.atTime(21, 0).atZone(zoneId).toInstant().toEpochMilli()
        val yesterday = dateToMillis(today.minusDays(1))

        val timestamps = listOf(nightToday, morningToday, yesterday)
        val streak = StreakCalculator.calculateStreak(
            doneAtTimestamps = timestamps,
            zoneId = zoneId,
            currentDate = today
        )
        assertEquals(2, streak)
    }

    @Test
    fun countCompletedOnDate_countsCorrectly() {
        val t1 = today.atTime(8, 0).atZone(zoneId).toInstant().toEpochMilli()
        val t2 = today.atTime(14, 0).atZone(zoneId).toInstant().toEpochMilli()
        val yesterday = dateToMillis(today.minusDays(1))

        val timestamps = listOf(t1, t2, yesterday)
        val countToday = StreakCalculator.countCompletedOnDate(timestamps, zoneId, today)
        val countYesterday = StreakCalculator.countCompletedOnDate(timestamps, zoneId, today.minusDays(1))

        assertEquals(2, countToday)
        assertEquals(1, countYesterday)
    }

    @Test
    fun getWeeklyActivities_returnsSevenDaysWithCorrectCounts() {
        val tToday = today.atTime(10, 0).atZone(zoneId).toInstant().toEpochMilli()
        val tYesterday = today.minusDays(1).atTime(15, 0).atZone(zoneId).toInstant().toEpochMilli()
        val tThreeDaysAgo = today.minusDays(3).atTime(9, 0).atZone(zoneId).toInstant().toEpochMilli()

        val timestamps = listOf(tToday, tYesterday, tThreeDaysAgo)
        val weekly = StreakCalculator.getWeeklyActivities(timestamps, zoneId, today)

        assertEquals(7, weekly.size)
        // Hari terakhir adalah hari ini
        val todayActivity = weekly.last()
        assertEquals(today, todayActivity.date)
        assertEquals(1, todayActivity.completedCount)
        assertEquals(true, todayActivity.isToday)

        // Hari kemarin
        val yesterdayActivity = weekly[5]
        assertEquals(today.minusDays(1), yesterdayActivity.date)
        assertEquals(1, yesterdayActivity.completedCount)

        // Hari 3 hari yang lalu
        val threeDaysAgoActivity = weekly[3]
        assertEquals(today.minusDays(3), threeDaysAgoActivity.date)
        assertEquals(1, threeDaysAgoActivity.completedCount)
    }
}
