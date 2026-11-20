package com.apptrackstudy.domain.model

/**
 * Domain model untuk Task (Materi Inti / Checklist item).
 */
data class StudyTask(
    val id: Long = 0,
    val subTopicId: Long,
    val title: String,
    val isDone: Boolean = false,
    val doneAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
