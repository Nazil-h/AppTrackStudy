package com.apptrackstudy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entitas Subject (Mata Kuliah / Materi Utama)
 * Level 1 pada hierarki pembelajaran.
 */
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val emoji: String,
    val colorIndex: Int,
    val targetDate: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
