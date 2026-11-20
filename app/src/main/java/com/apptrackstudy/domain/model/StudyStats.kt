package com.apptrackstudy.domain.model

/**
 * Statistik ringkasan belajar untuk tab Beranda & Profil.
 */
data class StudyStats(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalSubjects: Int = 0,
    val completedToday: Int = 0,
    val streakDays: Int = 0
) {
    val overallProgressPercentage: Float
        get() = if (totalTasks > 0) {
            (completedTasks.toFloat() / totalTasks.toFloat()) * 100f
        } else {
            0f
        }
}
