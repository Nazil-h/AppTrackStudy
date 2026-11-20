package com.apptrackstudy.domain.model

/**
 * Representasi badge / pencapaian mahasiswa.
 */
data class UserBadge(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean
)

/**
 * Model level, XP, dan badge pengguna.
 * Aturan XP:
 * - 10 XP per Task selesai
 * - 5 XP per menit fokus
 * - 15 XP per Tugas kuliah selesai tepat waktu
 * Level = (totalXp / 100) + 1
 */
data class UserLevel(
    val totalXp: Int = 0,
    val completedTasks: Int = 0,
    val totalFocusMinutes: Int = 0,
    val streakDays: Int = 0,
    val onTimeAssignments: Int = 0,
    val badges: List<UserBadge> = emptyList()
) {
    val currentLevel: Int get() = (totalXp / 100) + 1
    val currentLevelXp: Int get() = totalXp % 100
    val xpForNextLevel: Int get() = 100
    val levelProgress: Float get() = (currentLevelXp.toFloat() / 100f).coerceIn(0f, 1f)

    companion object {
        fun calculate(
            completedTasks: Int,
            totalFocusMinutes: Int,
            streakDays: Int,
            onTimeAssignments: Int = 0
        ): UserLevel {
            val taskXp = completedTasks * 10
            val focusXp = totalFocusMinutes * 5
            val assignmentXp = onTimeAssignments * 15
            val totalXp = taskXp + focusXp + assignmentXp

            val badges = listOf(
                UserBadge(
                    id = "first_step",
                    title = "Langkah Pertama",
                    description = "Selesaikan checklist materi pertamamu",
                    emoji = "🌱",
                    isUnlocked = completedTasks >= 1
                ),
                UserBadge(
                    id = "focus_fighter",
                    title = "Pejuang Fokus",
                    description = "Fokus belajar minimal 25 menit",
                    emoji = "⏱️",
                    isUnlocked = totalFocusMinutes >= 25
                ),
                UserBadge(
                    id = "diligent_student",
                    title = "Rajin & Tangguh",
                    description = "Selesaikan 10 checklist materi",
                    emoji = "📚",
                    isUnlocked = completedTasks >= 10
                ),
                UserBadge(
                    id = "streak_master",
                    title = "Disiplin Tinggi",
                    description = "Pertahankan 3 hari streak belajar berturut-turut",
                    emoji = "🔥",
                    isUnlocked = streakDays >= 3
                ),
                UserBadge(
                    id = "deadline_slayer",
                    title = "Penakluk Deadline",
                    description = "Selesaikan tugas kuliah sebelum tenggat waktu",
                    emoji = "⚡",
                    isUnlocked = onTimeAssignments >= 1
                ),
                UserBadge(
                    id = "deep_focus",
                    title = "Master Konsentrasi",
                    description = "Kumpulkan total 100 menit waktu fokus",
                    emoji = "🧠",
                    isUnlocked = totalFocusMinutes >= 100
                ),
                UserBadge(
                    id = "scholar",
                    title = "Cendekia Sejati",
                    description = "Capai Level 5 dalam petualangan belajarmu",
                    emoji = "👑",
                    isUnlocked = ((totalXp / 100) + 1) >= 5
                )
            )

            return UserLevel(
                totalXp = totalXp,
                completedTasks = completedTasks,
                totalFocusMinutes = totalFocusMinutes,
                streakDays = streakDays,
                onTimeAssignments = onTimeAssignments,
                badges = badges
            )
        }
    }
}
