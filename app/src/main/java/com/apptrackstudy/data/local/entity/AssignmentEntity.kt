package com.apptrackstudy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room untuk fitur Tab Tugas (Assignment).
 * Berisi nama tugas, batas waktu (deadline), dan status penyelesaian.
 */
@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val deadline: Long, // Epoch millis
    val isDone: Boolean = false,
    val doneAt: Long? = null, // Epoch millis saat ditandai selesai
    val createdAt: Long = System.currentTimeMillis()
)
