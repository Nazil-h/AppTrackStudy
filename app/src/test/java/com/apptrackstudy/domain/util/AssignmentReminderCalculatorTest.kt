package com.apptrackstudy.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.TimeUnit

class AssignmentReminderCalculatorTest {

    @Test
    fun calculateNextReminderTime_caseGreaterThan12Hours() {
        // Contoh: deadline 26 jam dari sekarang.
        // Kelipatan 12 jam sebelum deadline adalah 24 jam dan 12 jam sebelum deadline.
        // Pengingat berikutnya di masa depan adalah 24 jam sebelum deadline (yaitu 2 jam dari sekarang).
        val now = 1_000_000_000L
        val deadline = now + TimeUnit.HOURS.toMillis(26)

        val nextReminder = AssignmentReminderCalculator.calculateNextReminderTime(deadline, now)
        assertNotNull(nextReminder)

        val expected = deadline - TimeUnit.HOURS.toMillis(24)
        assertEquals(expected, nextReminder)
        assertTrue(nextReminder!! > now)
    }

    @Test
    fun calculateNextReminderTime_caseExactMultipleOf12Hours() {
        // Contoh: deadline tepat 24 jam dari sekarang.
        // Pengingat 24 jam terjadi tepat saat ini (now).
        // Pengingat berikutnya di masa depan yang dijadwalkan adalah 12 jam sebelum deadline (12 jam dari sekarang).
        val now = 1_000_000_000L
        val deadline = now + TimeUnit.HOURS.toMillis(24)

        val nextReminder = AssignmentReminderCalculator.calculateNextReminderTime(deadline, now)
        assertNotNull(nextReminder)

        val expected = deadline - TimeUnit.HOURS.toMillis(12)
        assertEquals(expected, nextReminder)
        assertTrue(nextReminder!! > now)
    }

    @Test
    fun calculateNextReminderTime_caseLessThan12Hours_schedules1HourBefore() {
        // Contoh: deadline 5 jam dari sekarang (kurang dari 12 jam).
        // Pengingat berikutnya adalah 1 jam sebelum deadline (yaitu 4 jam dari sekarang).
        val now = 1_000_000_000L
        val deadline = now + TimeUnit.HOURS.toMillis(5)

        val nextReminder = AssignmentReminderCalculator.calculateNextReminderTime(deadline, now)
        assertNotNull(nextReminder)

        val expected = deadline - TimeUnit.HOURS.toMillis(1)
        assertEquals(expected, nextReminder)
        assertEquals(now + TimeUnit.HOURS.toMillis(4), nextReminder)
    }

    @Test
    fun calculateNextReminderTime_caseLessThan1Hour_returnsNull() {
        // Contoh: deadline tinggal 30 menit lagi (sudah lewat batas pengingat 1 jam).
        val now = 1_000_000_000L
        val deadline = now + TimeUnit.MINUTES.toMillis(30)

        val nextReminder = AssignmentReminderCalculator.calculateNextReminderTime(deadline, now)
        assertNull(nextReminder)
    }

    @Test
    fun calculateNextReminderTime_caseAlreadyPassed_returnsNull() {
        // Contoh: deadline sudah lewat (misal 1 jam yang lalu).
        val now = 1_000_000_000L
        val deadline = now - TimeUnit.HOURS.toMillis(1)

        val nextReminder = AssignmentReminderCalculator.calculateNextReminderTime(deadline, now)
        assertNull(nextReminder)
    }

    @Test
    fun formatRemainingTime_formatsCorrectly() {
        val now = 1_000_000_000L

        // 2 hari 5 jam lagi
        val future2Days5Hours = now + TimeUnit.DAYS.toMillis(2) + TimeUnit.HOURS.toMillis(5)
        assertEquals("2 hari 5 jam lagi", AssignmentReminderCalculator.formatRemainingTime(future2Days5Hours, now))

        // Terlambat 3 jam
        val past3Hours = now - TimeUnit.HOURS.toMillis(3)
        assertEquals("Terlambat 3 jam", AssignmentReminderCalculator.formatRemainingTime(past3Hours, now))
    }
}
