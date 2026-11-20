package com.apptrackstudy.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entitas Task (Materi Inti / Checklist item)
 * Level 3 pada hierarki pembelajaran.
 * Jika SubTopic dihapus, seluruh Task di bawahnya otomatis terhapus (CASCADE).
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = SubTopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["subTopicId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["subTopicId"])
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subTopicId: Long,
    val title: String,
    val isDone: Boolean = false,
    val doneAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)
