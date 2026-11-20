package com.apptrackstudy.data.repository

import com.apptrackstudy.data.local.AppDatabase
import com.apptrackstudy.data.local.dao.SubjectDao
import com.apptrackstudy.data.local.dao.SubTopicDao
import com.apptrackstudy.data.local.dao.TaskDao
import com.apptrackstudy.data.mapper.toDomain
import com.apptrackstudy.data.mapper.toEntity
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.Subject
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StudyRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val subjectDao: SubjectDao,
    private val subTopicDao: SubTopicDao,
    private val taskDao: TaskDao
) : StudyRepository {

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getSubjectsWithProgress(): Flow<List<SubjectProgress>> {
        return subjectDao.getSubjectsWithDetails().map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getSubjectProgressById(id: Long): Flow<SubjectProgress?> {
        return subjectDao.getSubjectWithDetailsById(id).map { relation ->
            relation?.toDomain()
        }
    }

    override suspend fun insertSubject(subject: Subject): Long {
        return subjectDao.insertSubject(subject.toEntity())
    }

    override suspend fun updateSubject(subject: Subject) {
        subjectDao.updateSubject(subject.toEntity())
    }

    override suspend fun deleteSubject(subjectId: Long) {
        subjectDao.deleteSubjectById(subjectId)
    }

    override suspend fun insertSubTopic(subTopic: SubTopic): Long {
        return subTopicDao.insertSubTopic(subTopic.toEntity())
    }

    override suspend fun updateSubTopic(subTopic: SubTopic) {
        subTopicDao.updateSubTopic(subTopic.toEntity())
    }

    override suspend fun deleteSubTopic(subTopicId: Long) {
        subTopicDao.deleteSubTopicById(subTopicId)
    }

    override suspend fun insertTask(task: StudyTask): Long {
        return taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: StudyTask) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    override suspend fun setTaskDone(taskId: Long, isDone: Boolean) {
        val doneAt = if (isDone) System.currentTimeMillis() else null
        taskDao.updateTaskStatus(taskId, isDone, doneAt)
    }

    override fun getStudyStats(): Flow<StudyStats> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return combine(
            taskDao.getTotalTasksCount(),
            taskDao.getCompletedTasksCount(),
            subjectDao.getSubjectsCount(),
            taskDao.getCompletedTasksCountBetween(startOfDay, endOfDay)
        ) { totalTasks, completedTasks, totalSubjects, completedToday ->
            StudyStats(
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                totalSubjects = totalSubjects,
                completedToday = completedToday,
                streakDays = if (completedToday > 0) 1 else 0
            )
        }
    }

    override suspend fun clearAllData() {
        database.clearAllTables()
    }
}
