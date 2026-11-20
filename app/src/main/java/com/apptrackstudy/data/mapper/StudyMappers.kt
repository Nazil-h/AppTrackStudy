package com.apptrackstudy.data.mapper

import com.apptrackstudy.data.local.entity.SubjectEntity
import com.apptrackstudy.data.local.entity.SubjectWithSubTopicsRelation
import com.apptrackstudy.data.local.entity.SubTopicEntity
import com.apptrackstudy.data.local.entity.SubTopicWithTasksRelation
import com.apptrackstudy.data.local.entity.TaskEntity
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.Subject
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.model.SubTopicProgress

fun SubjectEntity.toDomain(): Subject = Subject(
    id = id,
    title = title,
    emoji = emoji,
    colorIndex = colorIndex,
    targetDate = targetDate,
    createdAt = createdAt
)

fun Subject.toEntity(): SubjectEntity = SubjectEntity(
    id = id,
    title = title,
    emoji = emoji,
    colorIndex = colorIndex,
    targetDate = targetDate,
    createdAt = createdAt
)

fun SubTopicEntity.toDomain(): SubTopic = SubTopic(
    id = id,
    subjectId = subjectId,
    title = title,
    createdAt = createdAt
)

fun SubTopic.toEntity(): SubTopicEntity = SubTopicEntity(
    id = id,
    subjectId = subjectId,
    title = title,
    createdAt = createdAt
)

fun TaskEntity.toDomain(): StudyTask = StudyTask(
    id = id,
    subTopicId = subTopicId,
    title = title,
    isDone = isDone,
    doneAt = doneAt,
    createdAt = createdAt
)

fun StudyTask.toEntity(): TaskEntity = TaskEntity(
    id = id,
    subTopicId = subTopicId,
    title = title,
    isDone = isDone,
    doneAt = doneAt,
    createdAt = createdAt
)

fun SubTopicWithTasksRelation.toDomain(): SubTopicProgress = SubTopicProgress(
    subTopic = subTopic.toDomain(),
    tasks = tasks.map { it.toDomain() }
)

fun SubjectWithSubTopicsRelation.toDomain(): SubjectProgress = SubjectProgress(
    subject = subject.toDomain(),
    subTopics = subTopics.map { it.toDomain() }
)
