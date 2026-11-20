package com.apptrackstudy.di

import android.content.Context
import androidx.room.Room
import com.apptrackstudy.data.local.AppDatabase
import com.apptrackstudy.data.local.dao.SubjectDao
import com.apptrackstudy.data.local.dao.SubTopicDao
import com.apptrackstudy.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSubjectDao(database: AppDatabase): SubjectDao = database.subjectDao()

    @Provides
    fun provideSubTopicDao(database: AppDatabase): SubTopicDao = database.subTopicDao()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()
}
