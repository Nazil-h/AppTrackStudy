package com.apptrackstudy.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Relasi 1 SubTopic ke banyak Task
 */
data class SubTopicWithTasksRelation(
    @Embedded
    val subTopic: SubTopicEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "subTopicId"
    )
    val tasks: List<TaskEntity>
)

/**
 * Relasi 1 Subject ke banyak SubTopic (yang masing-masing berisi Task).
 * Menghasilkan data hierarki lengkap: Subject -> SubTopic -> Task.
 */
data class SubjectWithSubTopicsRelation(
    @Embedded
    val subject: SubjectEntity,
    @Relation(
        entity = SubTopicEntity::class,
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val subTopics: List<SubTopicWithTasksRelation>
)
