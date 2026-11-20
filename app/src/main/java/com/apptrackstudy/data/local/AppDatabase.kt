package com.apptrackstudy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.apptrackstudy.data.local.dao.SubjectDao
import com.apptrackstudy.data.local.dao.SubTopicDao
import com.apptrackstudy.data.local.dao.TaskDao
import com.apptrackstudy.data.local.entity.SubjectEntity
import com.apptrackstudy.data.local.entity.SubTopicEntity
import com.apptrackstudy.data.local.entity.TaskEntity

/**
 * Room Database utama untuk Jejak Ilmu.
 * Menyimpan seluruh struktur data hierarkis Subject -> SubTopic -> Task.
 */
@Database(
    entities = [
        SubjectEntity::class,
        SubTopicEntity::class,
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun subTopicDao(): SubTopicDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "jejak_ilmu.db"
    }
}
