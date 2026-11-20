package com.apptrackstudy.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.apptrackstudy.data.local.entity.SubTopicEntity
import com.apptrackstudy.data.local.entity.SubTopicWithTasksRelation
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk operasi tabel SubTopic.
 */
@Dao
interface SubTopicDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTopic(subTopic: SubTopicEntity): Long

    @Update
    suspend fun updateSubTopic(subTopic: SubTopicEntity)

    @Delete
    suspend fun deleteSubTopic(subTopic: SubTopicEntity)

    @Query("DELETE FROM sub_topics WHERE id = :id")
    suspend fun deleteSubTopicById(id: Long)

    @Query("SELECT * FROM sub_topics WHERE subjectId = :subjectId ORDER BY createdAt ASC")
    fun getSubTopicsBySubjectId(subjectId: Long): Flow<List<SubTopicEntity>>

    @Transaction
    @Query("SELECT * FROM sub_topics WHERE subjectId = :subjectId ORDER BY createdAt ASC")
    fun getSubTopicsWithTasksBySubjectId(subjectId: Long): Flow<List<SubTopicWithTasksRelation>>

    @Query("SELECT COUNT(*) FROM sub_topics WHERE subjectId = :subjectId")
    fun getSubTopicsCountBySubjectId(subjectId: Long): Flow<Int>
}
