package com.banyumas.wisata.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.banyumas.wisata.model.Role
import com.banyumas.wisata.model.User

@Composable
fun rememberWBNavController(
    navController: NavHostController = rememberNavController()
): WBNavController = remember(navController) {
    WBNavController(navController)
}

@Stable
class WBNavController(
    val navController: NavHostController,
) {
    fun upPress() {
        navController.navigateUp()
    }

    fun navigateToBottomBarRoute(route: String) {
        if (route != navController.currentDestination?.route) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true

                popUpTo(findStartDestination(navController.graph).id) {
                    saveState = true
                }
            }
        }
    }

    fun navigateToDetail(destinationId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate(Screen.DetailScreen.createRoute(destinationId))
        }
    }

    fun navigateToLogin() {
        navController.navigate(Screen.LoginScreen) {
            popUpTo(Screen.SplashScreen.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

    fun navigateToHome(user: User) {
        val targetRoute = when (user.role) {
            Role.USER -> Screen.HomeScreen.createRoute(user.id)
            Role.ADMIN -> Screen.DashboardScreen.createRoute(user.id)
        }
        navController.navigate(targetRoute) {
            popUpTo(Screen.SplashScreen.route) { inclusive = true }
            launchSingleTop = true
        }
    }

    fun navigateToResetPassword() {
        navController.navigate(Screen.ForgotPasswordScreen.route)
    }

    fun navigateToRegister() {
        navController.navigate(Screen.RegisterScreen.route)
    }

    fun navigateToFetchDB() {
        navController.navigate(Screen.FetchScreen.route)
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
