package com.apptrackstudy.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Palet Warna Desain "Jejak Ilmu"
// Karakter: Hangat, Tenang, Fokus seperti buku catatan modern.
// Light mode: Warna dasar krem/putih tulang.
// Dark mode: Abu-biru gelap hangat (bukan hitam pekat).
// =========================================================================

// --- LIGHT THEME (Hangat & Nyaman di Mata) ---
val PrimaryLight = Color(0xFF1B6A5D) // Deep Pine / Teal lembut
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD3EFE8)
val OnPrimaryContainerLight = Color(0xFF00201B)

val SecondaryLight = Color(0xFF7A5835) // Warm Oak / Cokelat hangat buku
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFFFDDB8)
val OnSecondaryContainerLight = Color(0xFF2B1700)

val TertiaryLight = Color(0xFF7C5263) // Muted Dusty Rose
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFD8E5)
val OnTertiaryContainerLight = Color(0xFF311020)

val BackgroundLight = Color(0xFFFAF7F2) // Krem / Putih tulang
val OnBackgroundLight = Color(0xFF1E2421)
val SurfaceLight = Color(0xFFFFFFFF) // Surface murni kartu
val OnSurfaceLight = Color(0xFF1E2421)
val SurfaceVariantLight = Color(0xFFEFE8DD) // Surface hangat sekunder
val OnSurfaceVariantLight = Color(0xFF4C5853)
val OutlineLight = Color(0xFF828E89)
val OutlineVariantLight = Color(0xFFE2DDD3) // Border garis pemisah tipis

// --- DARK THEME (Abu-Biru Gelap Hangat, Bukan Hitam Pekat) ---
val PrimaryDark = Color(0xFF5EC5B0) // Soft Mint Teal
val OnPrimaryDark = Color(0xFF003730)
val PrimaryContainerDark = Color(0xFF005047)
val OnPrimaryContainerDark = Color(0xFFD3EFE8)

val SecondaryDark = Color(0xFFEBBF90)
val OnSecondaryDark = Color(0xFF452B0B)
val SecondaryContainerDark = Color(0xFF5F3F1F)
val OnSecondaryContainerDark = Color(0xFFFFDDB8)

val TertiaryDark = Color(0xFFEDB8CC)
val OnTertiaryDark = Color(0xFF482534)
val TertiaryContainerDark = Color(0xFF613B4B)
val OnTertiaryContainerDark = Color(0xFFFFD8E5)

val BackgroundDark = Color(0xFF141C24) // Abu-biru gelap hangat
val OnBackgroundDark = Color(0xFFE3E8EC)
val SurfaceDark = Color(0xFF1C2632) // Permukaan kartu dark yang kontras
val OnSurfaceDark = Color(0xFFE3E8EC)
val SurfaceVariantDark = Color(0xFF253342)
val OnSurfaceVariantDark = Color(0xFFA5B2BE)
val OutlineDark = Color(0xFF8996A3)
val OutlineVariantDark = Color(0xFF2D3C4C)

// =========================================================================
// 8 Warna Aksen Pastel untuk Subject (Mata Kuliah / Materi)
// Teruji kontras WCAG AA dan harmonis dengan tema buku catatan modern
// =========================================================================
val SubjectColors = listOf(
    Color(0xFF2E9F83), // 0: Mint Sage
    Color(0xFF5B6FD1), // 1: Soft Indigo
    Color(0xFFD9822B), // 2: Warm Amber
    Color(0xFFD6566B), // 3: Dusty Rose
    Color(0xFF2C92C7), // 4: Sky Blue
    Color(0xFF7E64CA), // 5: Soft Violet
    Color(0xFFC76443), // 6: Terracotta Clay
    Color(0xFF5F8B57)  // 7: Muted Matcha
)

/**
 * Mengambil warna aksen Subject secara aman berdasarkan index.
 */
fun getSubjectColor(index: Int): Color {
    val safeIndex = if (index >= 0) index % SubjectColors.size else 0
    return SubjectColors[safeIndex]
}
