package com.banyumas.wisata.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.util.trace
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.banyumas.wisata.core.model.User
import com.banyumas.wisata.feature.auth.LoginRoute
import com.banyumas.wisata.feature.auth.RegisterRoute
import com.banyumas.wisata.feature.auth.ResetPasswordRoute
import com.banyumas.wisata.feature.bookmarks.navigation.navigateToBookmarks
import com.banyumas.wisata.feature.dashboard.navigation.navigateToDashboard
import com.banyumas.wisata.feature.profile.navigation.navigateToProfile


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
        @Composable get() {
            val currentEntry =
                navController.currentBackStackEntryFlow.collectAsState(initial = null)

            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }

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

    fun navigateToLogin() {
        navController.navigate(LoginRoute)
    }

    fun navigateToRegister() {
        navController.navigate(RegisterRoute)
    }

    fun navigateToResetPassword() {
        navController.navigate(ResetPasswordRoute)
    }

    /**
     * Navigasi ke home screen setelah login berhasil,
     * sekaligus menghapus seluruh backstack auth graph.
     */
    fun navigateToHome(user: User) {
        val newRoute = Screen.MainGraph.createRoute(user.id, user.role.name)
        navController.navigate(newRoute) {
            popUpTo(Screen.AuthGraph.route) {
                inclusive = true // Hapus auth graph dari backstack
            }
        }
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}
