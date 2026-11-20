package com.apptrackstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apptrackstudy.data.local.entity.AssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    @Query("SELECT * FROM assignments ORDER BY deadline ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE isDone = 0 ORDER BY deadline ASC")
    fun getPendingAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE isDone = 0 ORDER BY deadline ASC LIMIT :limit")
    fun getUpcomingPendingAssignments(limit: Int): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE id = :id LIMIT 1")
    fun getAssignmentById(id: Long): Flow<AssignmentEntity?>

    @Query("SELECT * FROM assignments WHERE id = :id LIMIT 1")
    suspend fun getAssignmentByIdDirect(id: Long): AssignmentEntity?

    @Query("SELECT * FROM assignments WHERE isDone = 0")
    suspend fun getAllActivePendingList(): List<AssignmentEntity>

    @Query("SELECT COUNT(*) FROM assignments WHERE isDone = 1 AND doneAt IS NOT NULL AND doneAt <= deadline")
    fun getCompletedOnTimeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Query("DELETE FROM assignments WHERE id = :id")
    suspend fun deleteAssignment(id: Long)
}
