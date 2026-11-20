package com.apptrackstudy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.apptrackstudy.ui.home.HomeScreen
import com.apptrackstudy.ui.profile.ProfileScreen
import com.apptrackstudy.ui.track.TrackScreen

/**
 * NavHost utama aplikasi Jejak Ilmu.
 * Mengontrol perpindahan antar tab utama dan detail layar.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
        composable(route = Screen.Track.route) {
            TrackScreen()
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
