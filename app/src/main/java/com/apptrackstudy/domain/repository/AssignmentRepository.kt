package com.apptrackstudy.domain.repository

import com.apptrackstudy.domain.model.Assignment
import kotlinx.coroutines.flow.Flow

/**
 * Kontrak repository untuk manajemen data Tugas (Assignment).
 */
interface AssignmentRepository {
    fun getAllAssignments(): Flow<List<Assignment>>
    fun getUpcomingAssignments(limit: Int = 3): Flow<List<Assignment>>
    fun getCompletedOnTimeCount(): Flow<Int>
    suspend fun insertAssignment(assignment: Assignment): Long
    suspend fun updateAssignment(assignment: Assignment)
    suspend fun setAssignmentDone(assignmentId: Long, isDone: Boolean)
    suspend fun deleteAssignment(assignmentId: Long)
    suspend fun restoreAssignment(assignment: Assignment)
}
