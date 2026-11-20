package com.apptrackstudy.domain.model

/**
 * Domain model untuk SubTopic (Sub-materi).
 */
data class SubTopic(
    val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)
