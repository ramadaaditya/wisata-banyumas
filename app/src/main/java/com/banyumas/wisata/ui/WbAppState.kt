package com.banyumas.wisata.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.banyumas.wisata.feature.auth.navigation.AuthGraphRoute
import com.banyumas.wisata.feature.auth.navigation.LoginRoute
import com.banyumas.wisata.feature.auth.navigation.RegisterRoute
import com.banyumas.wisata.feature.auth.navigation.ResetPasswordRoute
import com.banyumas.wisata.feature.bookmarks.navigation.navigateToBookmarks
import com.banyumas.wisata.feature.dashboard.navigation.DashboardRoute
import com.banyumas.wisata.feature.dashboard.navigation.navigateToDashboard
import com.banyumas.wisata.feature.profile.navigation.navigateToProfile
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
    val currentDestination: NavDestination?
        @Composable get() = navController.currentBackStackEntryAsState().value?.destination


    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(route = topLevelDestination.route) == true
            }
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

            when (topLevelDestination) {
                TopLevelDestination.DASHBOARD -> navController.navigateToDashboard(
                    topLevelNavOptions
                )

                TopLevelDestination.BOOKMARKS -> navController.navigateToBookmarks(
                    topLevelNavOptions
                )

                TopLevelDestination.PROFILE -> navController.navigateToProfile(topLevelNavOptions)
            }
        }
    }

//    fun navigateToSearch() = navController.navigateToSearch()

    val shouldShowBottomBar: Boolean
        @Composable get() = currentTopLevelDestination != null

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
        navController.navigate(DashboardRoute) {
            popUpTo(AuthGraphRoute) {
                inclusive = true
            }
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}
