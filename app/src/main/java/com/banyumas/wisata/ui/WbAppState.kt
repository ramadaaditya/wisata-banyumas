package com.banyumas.wisata.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.banyumas.wisata.feature.auth.AuthGraphRoute
import com.banyumas.wisata.feature.auth.LoginRoute
import com.banyumas.wisata.feature.auth.RegisterRoute
import com.banyumas.wisata.feature.auth.ResetPasswordRoute
import com.banyumas.wisata.feature.bookmarks.navigation.BookmarksRoute
import com.banyumas.wisata.feature.dashboard.navigation.DashboardGraphRoute
import com.banyumas.wisata.feature.profile.navigation.ProfileRoute
import com.banyumas.wisata.navigation.TopLevelDestination


@Composable
fun rememberWbAppState(
    navController: NavHostController = rememberNavController()
): WbAppState {
    return remember(navController) {
        WbAppState(navController)
    }
}

@Stable
class WbAppState(
    val navController: NavHostController
) {
    private val previousDestination = mutableStateOf<NavDestination?>(null)
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination

    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() = when (currentDestination?.route) {
            // PERBAIKAN: Gunakan perbandingan nama kelas yang andal
            // Ini memastikan kita tahu graph mana yang sedang aktif
            DashboardGraphRoute::class.qualifiedName -> TopLevelDestination.DASHBOARD
            BookmarksRoute::class.qualifiedName -> TopLevelDestination.BOOKMARKS
            ProfileRoute::class.qualifiedName -> TopLevelDestination.PROFILE
            else -> null
        }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        trace("Navigation: ${topLevelDestination.name}") {
            val topLevelNavOptions = navOptions {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }

            navController.navigate(topLevelDestination.route, topLevelNavOptions)
        }
    }

//    fun navigateToSearch() = navController.navigateToSearch()

    fun navigateToLogin() {
        navController.navigate(LoginRoute)
    }

    fun navigateToRegister() {
        navController.navigate(RegisterRoute)
    }

    fun navigateToResetPassword() {
        navController.navigate(ResetPasswordRoute)
    }

    fun navigateToHome() {
        navController.navigate(DashboardGraphRoute) {
            popUpTo(AuthGraphRoute) {
                inclusive = true
            }
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}
