package com.apptrackstudy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas SubTopic (Sub-materi)
 * Level 2 pada hierarki pembelajaran.
 * Jika Subject dihapus, seluruh SubTopic di bawahnya otomatis terhapus (CASCADE).
 */
@Entity(
    tableName = "sub_topics",
    foreignKeys = [
        ForeignKey(
            entity = SubjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subjectId"])
    ]
)
data class SubTopicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subjectId: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)
