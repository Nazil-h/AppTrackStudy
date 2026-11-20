package com.apptrackstudy.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubjectProgressTest {

    @Test
    fun subTopicProgress_withEmptyTasks_returnsZeroPercent_noDivisionByZero() {
        val subTopic = SubTopic(id = 1, subjectId = 1, title = "Pengenalan")
        val progress = SubTopicProgress(subTopic = subTopic, tasks = emptyList())

        assertEquals(0, progress.totalTasksCount)
        assertEquals(0, progress.completedTasksCount)
        assertEquals(0f, progress.progressPercentage, 0.001f)
        assertFalse(progress.isCompleted)
    }

    @Test
    fun subTopicProgress_withPartialTasks_calculatesCorrectPercentage() {
        val subTopic = SubTopic(id = 1, subjectId = 1, title = "Pengenalan")
        val tasks = listOf(
            StudyTask(id = 1, subTopicId = 1, title = "Materi 1", isDone = true),
            StudyTask(id = 2, subTopicId = 1, title = "Materi 2", isDone = false),
            StudyTask(id = 3, subTopicId = 1, title = "Materi 3", isDone = false),
            StudyTask(id = 4, subTopicId = 1, title = "Materi 4", isDone = true)
        )
        val progress = SubTopicProgress(subTopic = subTopic, tasks = tasks)

        assertEquals(4, progress.totalTasksCount)
        assertEquals(2, progress.completedTasksCount)
        assertEquals(50f, progress.progressPercentage, 0.001f)
        assertFalse(progress.isCompleted)
    }

    @Test
    fun subTopicProgress_withAllTasksCompleted_returns100Percent() {
        val subTopic = SubTopic(id = 1, subjectId = 1, title = "Pengenalan")
        val tasks = listOf(
            StudyTask(id = 1, subTopicId = 1, title = "Materi 1", isDone = true),
            StudyTask(id = 2, subTopicId = 1, title = "Materi 2", isDone = true)
        )
        val progress = SubTopicProgress(subTopic = subTopic, tasks = tasks)

        assertEquals(2, progress.totalTasksCount)
        assertEquals(2, progress.completedTasksCount)
        assertEquals(100f, progress.progressPercentage, 0.001f)
        assertTrue(progress.isCompleted)
    }

    @Test
    fun subjectProgress_withNoSubTopicsOrTasks_returnsZeroPercent() {
        val subject = Subject(id = 1, title = "Kalkulus", emoji = "📐")
        val progress = SubjectProgress(subject = subject, subTopics = emptyList())

        assertEquals(0, progress.totalTasksCount)
        assertEquals(0, progress.completedTasksCount)
        assertEquals(0f, progress.progressPercentage, 0.001f)
        assertFalse(progress.isCompleted)
    }

    @Test
    fun subjectProgress_withMultipleSubTopics_calculatesAccumulatedProgress() {
        val subject = Subject(id = 1, title = "Algoritma & Struktur Data", emoji = "💻")

        val subTopic1 = SubTopicProgress(
            subTopic = SubTopic(id = 1, subjectId = 1, title = "Bab 1: Array"),
            tasks = listOf(
                StudyTask(id = 1, subTopicId = 1, title = "Array 1D", isDone = true),
                StudyTask(id = 2, subTopicId = 1, title = "Array 2D", isDone = true)
            )
        )

        val subTopic2 = SubTopicProgress(
            subTopic = SubTopic(id = 2, subjectId = 1, title = "Bab 2: Linked List"),
            tasks = listOf(
                StudyTask(id = 3, subTopicId = 2, title = "Singly Linked List", isDone = true),
                StudyTask(id = 4, subTopicId = 2, title = "Doubly Linked List", isDone = false),
                StudyTask(id = 5, subTopicId = 2, title = "Circular Linked List", isDone = false)
            )
        )

        val progress = SubjectProgress(subject = subject, subTopics = listOf(subTopic1, subTopic2))

        assertEquals(5, progress.totalTasksCount)
        assertEquals(3, progress.completedTasksCount)
        assertEquals(60f, progress.progressPercentage, 0.001f)
        assertFalse(progress.isCompleted)
    }

    @Test
    fun subjectProgress_withAllSubTopicsCompleted_returns100Percent() {
        val subject = Subject(id = 1, title = "Kalkulus", emoji = "📐")

        val subTopic = SubTopicProgress(
            subTopic = SubTopic(id = 1, subjectId = 1, title = "Limit"),
            tasks = listOf(
                StudyTask(id = 1, subTopicId = 1, title = "Limit Aljabar", isDone = true),
                StudyTask(id = 2, subTopicId = 1, title = "Limit Trigonometri", isDone = true)
            )
        )

        val progress = SubjectProgress(subject = subject, subTopics = listOf(subTopic))

        assertEquals(2, progress.totalTasksCount)
        assertEquals(2, progress.completedTasksCount)
        assertEquals(100f, progress.progressPercentage, 0.001f)
        assertTrue(progress.isCompleted)
    }
}
