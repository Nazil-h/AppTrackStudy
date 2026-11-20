package com.apptrackstudy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Representasi rute navigasi aplikasi Jejak Ilmu.
 * 4 Tab Bottom Navigation: Beranda, Peta Belajar, Tugas, Profil.
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    data object Home : Screen(
        route = "home",
        title = "Beranda",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    )

    data object Track : Screen(
        route = "track",
        title = "Peta Belajar",
        icon = Icons.Outlined.AutoStories,
        selectedIcon = Icons.Filled.AutoStories
    )

    data object Assignments : Screen(
        route = "assignments",
        title = "Tugas",
        icon = Icons.AutoMirrored.Outlined.Assignment,
        selectedIcon = Icons.AutoMirrored.Filled.Assignment
    )

    data object Profile : Screen(
        route = "profile",
        title = "Profil",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Track,
    Screen.Assignments,
    Screen.Profile
)
