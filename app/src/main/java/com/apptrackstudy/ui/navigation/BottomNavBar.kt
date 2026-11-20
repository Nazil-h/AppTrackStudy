package com.apptrackstudy.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.apptrackstudy.ui.theme.AppTrackStudyTheme

/**
 * Bottom Navigation Bar dengan 4 tab (Beranda, Peta Belajar, Tugas, Profil).
 * Menggunakan pill indicator M3, ikon filled/outline konsisten, dan border pemisah halus.
 */
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
            thickness = 0.75.dp
        )
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            bottomNavItems.forEach { screen ->
                val isSelected = currentRoute == screen.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (isSelected) screen.selectedIcon else screen.icon,
                            contentDescription = screen.title,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    label = {
                        Text(
                            text = screen.title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

@Preview(name = "Bottom Nav Light", showBackground = true)
@Composable
private fun BottomNavPreviewLight() {
    AppTrackStudyTheme(darkTheme = false) {
        val dummyNav = rememberNavController()
        BottomNavBar(navController = dummyNav, currentRoute = Screen.Home.route)
    }
}

@Preview(name = "Bottom Nav Dark", showBackground = true)
@Composable
private fun BottomNavPreviewDark() {
    AppTrackStudyTheme(darkTheme = true) {
        val dummyNav = rememberNavController()
        BottomNavBar(navController = dummyNav, currentRoute = Screen.Track.route)
    }
}
