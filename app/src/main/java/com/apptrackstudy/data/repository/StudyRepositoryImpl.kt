package com.apptrackstudy.data.repository

import com.apptrackstudy.data.local.AppDatabase
import com.apptrackstudy.data.local.dao.SubjectDao
import com.apptrackstudy.data.local.dao.SubTopicDao
import com.apptrackstudy.data.local.dao.TaskDao
import com.apptrackstudy.data.mapper.toDomain
import com.apptrackstudy.data.mapper.toEntity
import com.apptrackstudy.domain.model.DailyActivity
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.Subject
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.model.SubTopicProgress
import com.apptrackstudy.domain.repository.StudyRepository
import com.apptrackstudy.domain.util.StreakCalculator
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
        return combine(
            taskDao.getTotalTasksCount(),
            taskDao.getCompletedTasksCount(),
            subjectDao.getSubjectsCount(),
            taskDao.getAllCompletedTaskDoneAt()
        ) { totalTasks, completedTasks, totalSubjects, doneAtList ->
            val completedToday = StreakCalculator.countCompletedOnDate(doneAtList)
            val streak = StreakCalculator.calculateStreak(doneAtList)
            StudyStats(
                totalTasks = totalTasks,
                completedTasks = completedTasks,
                totalSubjects = totalSubjects,
                completedToday = completedToday,
                streakDays = streak
            )
        }
    }

    override fun getWeeklyActivities(): Flow<List<DailyActivity>> {
        return taskDao.getAllCompletedTaskDoneAt().map { doneAtList ->
            StreakCalculator.getWeeklyActivities(doneAtList)
        }
    }

    override suspend fun clearAllData() {
        database.clearAllTables()
    }

    override suspend fun restoreSubject(subjectProgress: SubjectProgress) {
        val newSubjectId = subjectDao.insertSubject(subjectProgress.subject.copy(id = 0).toEntity())
        for (st in subjectProgress.subTopics) {
            val newSubTopicId = subTopicDao.insertSubTopic(st.subTopic.copy(id = 0, subjectId = newSubjectId).toEntity())
            for (task in st.tasks) {
                taskDao.insertTask(task.copy(id = 0, subTopicId = newSubTopicId).toEntity())
            }
        }
    }

    override suspend fun restoreSubTopic(subTopicProgress: SubTopicProgress) {
        val newSubTopicId = subTopicDao.insertSubTopic(subTopicProgress.subTopic.copy(id = 0).toEntity())
        for (task in subTopicProgress.tasks) {
            taskDao.insertTask(task.copy(id = 0, subTopicId = newSubTopicId).toEntity())
        }
    }

    override suspend fun restoreTask(task: StudyTask) {
        taskDao.insertTask(task.copy(id = 0).toEntity())
    }
}
