package com.apptrackstudy.data.repository

import android.content.Context
import com.apptrackstudy.data.local.dao.AssignmentDao
import com.apptrackstudy.data.mapper.toDomain
import com.apptrackstudy.data.mapper.toEntity
import com.apptrackstudy.domain.model.Assignment
import com.apptrackstudy.domain.repository.AssignmentRepository
import com.apptrackstudy.service.AssignmentAlarmScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentRepositoryImpl @Inject constructor(
    private val assignmentDao: AssignmentDao,
    @param:ApplicationContext private val context: Context
) : AssignmentRepository {

    override fun getAllAssignments(): Flow<List<Assignment>> {
        return assignmentDao.getAllAssignments().map { list -> list.map { it.toDomain() } }
    }

    override fun getUpcomingAssignments(limit: Int): Flow<List<Assignment>> {
        return assignmentDao.getUpcomingPendingAssignments(limit).map { list -> list.map { it.toDomain() } }
    }

    override fun getCompletedOnTimeCount(): Flow<Int> {
        return assignmentDao.getCompletedOnTimeCount()
    }

    override suspend fun insertAssignment(assignment: Assignment): Long {
        val insertedId = assignmentDao.insertAssignment(assignment.toEntity())
        if (!assignment.isDone && assignment.deadline > System.currentTimeMillis()) {
            AssignmentAlarmScheduler.scheduleNextReminder(
                context = context,
                assignmentId = insertedId,
                title = assignment.title,
                deadlineMillis = assignment.deadline
            )
        }
        return insertedId
    }

    override suspend fun updateAssignment(assignment: Assignment) {
        assignmentDao.updateAssignment(assignment.toEntity())
        if (assignment.isDone || assignment.deadline <= System.currentTimeMillis()) {
            AssignmentAlarmScheduler.cancelReminder(context, assignment.id)
        } else {
            AssignmentAlarmScheduler.scheduleNextReminder(
                context = context,
                assignmentId = assignment.id,
                title = assignment.title,
                deadlineMillis = assignment.deadline
            )
        }
    }

    override suspend fun setAssignmentDone(assignmentId: Long, isDone: Boolean) {
        val existing = assignmentDao.getAssignmentByIdDirect(assignmentId) ?: return
        val now = System.currentTimeMillis()
        val updated = existing.copy(
            isDone = isDone,
            doneAt = if (isDone) now else null
        )
        assignmentDao.updateAssignment(updated)

        if (isDone) {
            AssignmentAlarmScheduler.cancelReminder(context, assignmentId)
        } else if (updated.deadline > now) {
            AssignmentAlarmScheduler.scheduleNextReminder(
                context = context,
                assignmentId = assignmentId,
                title = updated.title,
                deadlineMillis = updated.deadline
            )
        }
    }

    override suspend fun deleteAssignment(assignmentId: Long) {
        AssignmentAlarmScheduler.cancelReminder(context, assignmentId)
        assignmentDao.deleteAssignment(assignmentId)
    }

    override suspend fun restoreAssignment(assignment: Assignment) {
        assignmentDao.insertAssignment(assignment.toEntity())
        if (!assignment.isDone && assignment.deadline > System.currentTimeMillis()) {
            AssignmentAlarmScheduler.scheduleNextReminder(
                context = context,
                assignmentId = assignment.id,
                title = assignment.title,
                deadlineMillis = assignment.deadline
            )
        }
    }
}
