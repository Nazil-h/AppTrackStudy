package com.apptrackstudy.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apptrackstudy.domain.model.StudyTask
import com.apptrackstudy.ui.assignment.AssignmentScreen
import com.apptrackstudy.ui.home.HomeScreen
import com.apptrackstudy.ui.profile.ProfileScreen
import com.apptrackstudy.ui.track.TrackScreen

/**
 * NavHost utama aplikasi Jejak Ilmu.
 * Mengontrol perpindahan antar 4 tab utama (Beranda, Peta Belajar, Tugas, Profil)
 * dengan animasi transisi yang halus dan responsif.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route,
    onStartTaskTimer: (StudyTask) -> Unit = {}
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(220)) + slideInHorizontally(initialOffsetX = { 30 }, animationSpec = tween(220))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(180)) + slideOutHorizontally(targetOffsetX = { -30 }, animationSpec = tween(180))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220)) + slideInHorizontally(initialOffsetX = { -30 }, animationSpec = tween(220))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(180)) + slideOutHorizontally(targetOffsetX = { 30 }, animationSpec = tween(180))
        }
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNavigateToTrack = {
                    navController.navigate(Screen.Track.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToAssignments = {
                    navController.navigate(Screen.Assignments.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(route = Screen.Track.route) {
            TrackScreen(
                onStartTaskTimerClick = onStartTaskTimer
            )
        }
        composable(route = Screen.Assignments.route) {
            AssignmentScreen()
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
