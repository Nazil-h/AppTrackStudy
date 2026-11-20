package com.apptrackstudy.domain.repository

import com.apptrackstudy.domain.model.DailyActivity
import com.apptrackstudy.domain.model.StudyStats
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.domain.model.Subject
import com.apptrackstudy.domain.model.SubjectProgress
import com.apptrackstudy.domain.model.SubTopic
import com.apptrackstudy.domain.model.SubTopicProgress
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak Repository utama untuk manajemen data pembelajaran.
 */
interface StudyRepository {

    // --- Subject Operations ---
    fun getAllSubjects(): Flow<List<Subject>>
    fun getSubjectsWithProgress(): Flow<List<SubjectProgress>>
    fun getSubjectProgressById(id: Long): Flow<SubjectProgress?>
    suspend fun insertSubject(subject: Subject): Long
    suspend fun updateSubject(subject: Subject)
    suspend fun deleteSubject(subjectId: Long)

    // --- SubTopic Operations ---
    suspend fun insertSubTopic(subTopic: SubTopic): Long
    suspend fun updateSubTopic(subTopic: SubTopic)
    suspend fun deleteSubTopic(subTopicId: Long)

    // --- Task Operations ---
    suspend fun insertTask(task: StudyTask): Long
    suspend fun updateTask(task: StudyTask)
    suspend fun deleteTask(taskId: Long)
    suspend fun setTaskDone(taskId: Long, isDone: Boolean)

    // --- Stats & Overview ---
    fun getStudyStats(): Flow<StudyStats>
    fun getWeeklyActivities(): Flow<List<DailyActivity>>

    // --- Reset ---
    suspend fun clearAllData()

    // --- Undo / Restore ---
    suspend fun restoreSubject(subjectProgress: SubjectProgress)
    suspend fun restoreSubTopic(subTopicProgress: SubTopicProgress)
    suspend fun restoreTask(task: StudyTask)
}
