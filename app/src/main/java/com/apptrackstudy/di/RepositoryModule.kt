package com.apptrackstudy.di

import com.apptrackstudy.data.repository.AssignmentRepositoryImpl
import com.apptrackstudy.data.repository.StudyRepositoryImpl
import com.apptrackstudy.domain.repository.AssignmentRepository
import com.apptrackstudy.domain.repository.StudyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindStudyRepository(
        studyRepositoryImpl: StudyRepositoryImpl
    ): StudyRepository

    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(
        assignmentRepositoryImpl: AssignmentRepositoryImpl
    ): AssignmentRepository
}
