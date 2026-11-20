package com.apptrackstudy.ui.theme

import androidx.compose.ui.graphics.Color

// Palet Primer & Aksen Jejak Ilmu (Hangat & Menenangkan)
val PrimaryLight = Color(0xFF20635B)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFA7EFE3)
val OnPrimaryContainerLight = Color(0xFF00201D)

val SecondaryLight = Color(0xFF8B5416)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFDDB8)
val OnSecondaryContainerLight = Color(0xFF2D1600)

val TertiaryLight = Color(0xFF8B4240)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFDAD8)
val OnTertiaryContainerLight = Color(0xFF3B0709)

val BackgroundLight = Color(0xFFF8FAF9)
val OnBackgroundLight = Color(0xFF191C1B)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF191C1B)
val SurfaceVariantLight = Color(0xFFDAE5E1)
val OnSurfaceVariantLight = Color(0xFF3F4946)

// Dark Theme Colors
val PrimaryDark = Color(0xFF8BD3C7)
val OnPrimaryDark = Color(0xFF003732)
val PrimaryContainerDark = Color(0xFF005049)
val OnPrimaryContainerDark = Color(0xFFA7EFE3)

val SecondaryDark = Color(0xFFFFB870)
val OnSecondaryDark = Color(0xFF4A2800)
val SecondaryContainerDark = Color(0xFF693D00)
val OnSecondaryContainerDark = Color(0xFFFFDDB8)

val TertiaryDark = Color(0xFFFFB3B1)
val OnTertiaryDark = Color(0xFF561D1C)
val TertiaryContainerDark = Color(0xFF722B2A)
val OnTertiaryContainerDark = Color(0xFFFFDAD8)

val BackgroundDark = Color(0xFF111413)
val OnBackgroundDark = Color(0xFFE1E3E1)
val SurfaceDark = Color(0xFF191C1B)
val OnSurfaceDark = Color(0xFFE1E3E1)
val SurfaceVariantDark = Color(0xFF3F4946)
val OnSurfaceVariantDark = Color(0xFFBFC9C5)

// Palet warna aksen untuk Subject (Mata Kuliah / Materi)
val SubjectColors = listOf(
    Color(0xFF10B981), // 0: Emerald
    Color(0xFF6366F1), // 1: Indigo
    Color(0xFFF59E0B), // 2: Amber
    Color(0xFFF43F5E), // 3: Rose
    Color(0xFF06B6D4), // 4: Cyan
    Color(0xFF8B5CF6), // 5: Violet
    Color(0xFF14B8A6), // 6: Teal
    Color(0xFFFF7A59)  // 7: Coral
)

fun getSubjectColor(index: Int): Color {
    val safeIndex = if (index >= 0) index % SubjectColors.size else 0
    return SubjectColors[safeIndex]
}
