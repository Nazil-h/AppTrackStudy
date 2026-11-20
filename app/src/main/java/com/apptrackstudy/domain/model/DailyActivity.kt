package com.apptrackstudy.domain.model

import java.time.LocalDate

/**
 * Data penyelesaian materi harian untuk grafik batang aktivitas.
 */
data class DailyActivity(
    val date: LocalDate,
    val dayLabel: String, // Misal: "Sen", "Sel", "Rab", dll.
    val completedCount: Int,
    val isToday: Boolean
)
