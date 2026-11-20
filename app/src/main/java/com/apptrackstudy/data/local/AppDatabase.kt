package com.apptrackstudy.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.apptrackstudy.data.local.dao.AssignmentDao
import com.apptrackstudy.data.local.dao.SubjectDao
import com.apptrackstudy.data.local.dao.SubTopicDao
import com.apptrackstudy.data.local.dao.TaskDao
import com.apptrackstudy.data.local.entity.AssignmentEntity
import com.apptrackstudy.data.local.entity.SubjectEntity
import com.apptrackstudy.data.local.entity.SubTopicEntity
import com.apptrackstudy.data.local.entity.TaskEntity

/**
 * Room Database utama untuk Jejak Ilmu.
 * Menyimpan seluruh struktur data hierarkis Subject -> SubTopic -> Task serta Assignment (Tugas).
 */
@Database(
    entities = [
        SubjectEntity::class,
        SubTopicEntity::class,
        TaskEntity::class,
        AssignmentEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subjectDao(): SubjectDao
    abstract fun subTopicDao(): SubTopicDao
    abstract fun taskDao(): TaskDao
    abstract fun assignmentDao(): AssignmentDao

    companion object {
        const val DATABASE_NAME = "jejak_ilmu.db"

        /**
         * Migrasi dari versi 1 ke versi 2:
         * Menambahkan tabel `assignments` untuk Tab Tugas tanpa menghapus data materi sebelumnya.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `assignments` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `deadline` INTEGER NOT NULL,
                        `isDone` INTEGER NOT NULL,
                        `doneAt` INTEGER,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
