package com.apptrackstudy.domain.model

/**
 * Domain model untuk Subject (Mata Kuliah / Materi Pokok).
 */
data class Subject(
    val id: Long = 0,
    val title: String,
    val emoji: String,
    val colorIndex: Int = 0,
    val targetDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
