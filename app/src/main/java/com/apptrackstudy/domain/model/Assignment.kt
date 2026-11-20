package com.apptrackstudy.domain.model

/**
 * Model domain untuk Tugas Kuliah.
 */
data class Assignment(
    val id: Long = 0,
    val title: String,
    val deadline: Long, // Epoch millis
    val isDone: Boolean = false,
    val doneAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    /**
     * Memeriksa apakah tugas sudah melewati deadline dan belum selesai.
     */
    fun isOverdue(currentTime: Long = System.currentTimeMillis()): Boolean {
        return !isDone && currentTime > deadline
    }

    /**
     * Menghitung sisa milidetik menuju deadline.
     */
    fun remainingMillis(currentTime: Long = System.currentTimeMillis()): Long {
        return deadline - currentTime
    }
}

/**
 * Kategori pengelompokan daftar tugas.
 */
enum class AssignmentGroup(val displayName: String) {
    OVERDUE("Terlambat"),
    TODAY("Hari ini"),
    THIS_WEEK("Minggu ini"),
    LATER("Nanti"),
    DONE("Selesai")
}
