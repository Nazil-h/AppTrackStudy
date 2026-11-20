package com.apptrackstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.apptrackstudy.data.local.entity.SubjectEntity
import com.apptrackstudy.data.local.entity.SubjectWithSubTopicsRelation
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk operasi tabel Subject.
 */
@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Update
    suspend fun updateSubject(subject: SubjectEntity)

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Long)

    @Query("SELECT * FROM subjects ORDER BY createdAt DESC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectById(id: Long): Flow<SubjectEntity?>

    @Transaction
    @Query("SELECT * FROM subjects ORDER BY createdAt DESC")
    fun getSubjectsWithDetails(): Flow<List<SubjectWithSubTopicsRelation>>

    @Transaction
    @Query("SELECT * FROM subjects WHERE id = :id")
    fun getSubjectWithDetailsById(id: Long): Flow<SubjectWithSubTopicsRelation?>

    @Query("SELECT COUNT(*) FROM subjects")
    fun getSubjectsCount(): Flow<Int>
}
