package com.apptrackstudy.domain.model

/**
 * Domain model untuk SubTopic beserta daftar Task dan perhitungan progress-nya.
 */
data class SubTopicProgress(
    val subTopic: SubTopic,
    val tasks: List<StudyTask> = emptyList()
) {
    val totalTasksCount: Int get() = tasks.size
    val completedTasksCount: Int get() = tasks.count { it.isDone }

    /**
     * Persentase progress (0% - 100%). Menghindari crash pembagian nol jika tasks kosong.
     */
    val progressPercentage: Float
        get() = if (totalTasksCount > 0) {
            (completedTasksCount.toFloat() / totalTasksCount.toFloat()) * 100f
        } else {
            0f
        }

    val isCompleted: Boolean
        get() = totalTasksCount > 0 && completedTasksCount == totalTasksCount
}

/**
 * Domain model untuk Subject beserta daftar SubTopic dan perhitungan progress akumulatif.
 */
data class SubjectProgress(
    val subject: Subject,
    val subTopics: List<SubTopicProgress> = emptyList()
) {
    val totalTasksCount: Int
        get() = subTopics.sumOf { it.totalTasksCount }

    val completedTasksCount: Int
        get() = subTopics.sumOf { it.completedTasksCount }

    /**
     * Progress Subject = (jumlah Task selesai / total Task) * 100%.
     * Jika belum ada Task, hasil 0%.
     */
    val progressPercentage: Float
        get() = if (totalTasksCount > 0) {
            (completedTasksCount.toFloat() / totalTasksCount.toFloat()) * 100f
        } else {
            0f
        }

    val isCompleted: Boolean
        get() = totalTasksCount > 0 && completedTasksCount == totalTasksCount
}
