package com.apptrackstudy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.apptrackstudy.ui.components.ActiveTimerBanner
import com.apptrackstudy.ui.components.FocusTimerSheet
import com.apptrackstudy.ui.navigation.AppNavHost
import com.apptrackstudy.ui.navigation.BottomNavBar
import com.apptrackstudy.ui.navigation.Screen

/**
 * Root Screen Jejak Ilmu yang menggabungkan Scaffold, 4-Tab Bottom Navigation Bar,
 * Floating ActiveTimerBanner, dan FocusTimerSheet modal.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val timerState by viewModel.timerState.collectAsState()
    val showTimerSheet by viewModel.showTimerSheet.collectAsState()
    val navigateToAssignments by viewModel.navigateToAssignments.collectAsState()

    LaunchedEffect(navigateToAssignments) {
        if (navigateToAssignments) {
            navController.navigate(Screen.Assignments.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
            viewModel.onNavigatedToAssignmentsTab()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Column {
                ActiveTimerBanner(
                    timerData = timerState,
                    onBannerClick = { viewModel.openTimer() },
                    onPauseClick = { viewModel.pauseTimer() },
                    onResumeClick = { viewModel.resumeTimer() }
                )
                BottomNavBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            onStartTaskTimer = { task ->
                viewModel.openTimer(task.id, task.title)
            }
        )
    }

    if (showTimerSheet) {
        FocusTimerSheet(
            timerData = timerState,
            onStart = { workMin, breakMin -> viewModel.startTimer(workMin, breakMin) },
            onPause = { viewModel.pauseTimer() },
            onResume = { viewModel.resumeTimer() },
            onStop = { viewModel.stopTimer() },
            onSwitchMode = { viewModel.switchMode() },
            onUpdateDurations = { workMin, breakMin -> viewModel.updateDurations(workMin, breakMin) },
            onDismiss = { viewModel.closeTimerSheet() }
        )
    }
}
