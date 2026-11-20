package com.apptrackstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.apptrackstudy.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk operasi tabel Task.
 */
@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("SELECT * FROM tasks WHERE subTopicId = :subTopicId ORDER BY createdAt ASC")
    fun getTasksBySubTopicId(subTopicId: Long): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET isDone = :isDone, doneAt = :doneAt WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, isDone: Boolean, doneAt: Long?)

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 1")
    fun getCompletedTasksCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isDone = 1 AND doneAt >= :startOfDay AND doneAt <= :endOfDay")
    fun getCompletedTasksCountBetween(startOfDay: Long, endOfDay: Long): Flow<Int>

    @Query("SELECT doneAt FROM tasks WHERE isDone = 1 AND doneAt IS NOT NULL ORDER BY doneAt DESC")
    fun getAllCompletedTaskDoneAt(): Flow<List<Long>>
}
