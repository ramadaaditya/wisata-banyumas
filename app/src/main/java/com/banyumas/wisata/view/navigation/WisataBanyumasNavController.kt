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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object MainDestinations {
    const val HOME_ROUTE = "home"
    const val DASHBOARD_ROUTE = "dashboard"
    const val SPLASH_ROUTE = "splash"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"
    const val RESET_ROUTE = "reset"
    const val DETAIL_ROUTE = "detail"
    const val DESTINATION_ID_KEY = "placeId"
    const val FETCH_ROUTE = "fetch"
    const val USER_ID_KEY = "userId"
    const val ORIGIN = "origin"
}

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

    fun navigateToDetail(placeId: String, from: NavBackStackEntry) {
        if (from.lifecycleIsResumed()) {
            navController.navigate("${MainDestinations.DETAIL_ROUTE}/$placeId")
        }
    }

    fun navigateToLogin() {
        navController.navigate(MainDestinations.LOGIN_ROUTE)
    }

    fun navigateToHome(user: User) {
        val encodedId = URLEncoder.encode(user.id, StandardCharsets.UTF_8.toString())
        val targetRoute = when (user.role) {
            Role.USER -> "${MainDestinations.HOME_ROUTE}/$encodedId"
            Role.ADMIN -> "${MainDestinations.DASHBOARD_ROUTE}/$encodedId"
        }
        navController.navigate(targetRoute) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun navigateToResetPassword() {
        navController.navigate(MainDestinations.RESET_ROUTE)
    }

    fun navigateToRegister() {
        navController.navigate(MainDestinations.REGISTER_ROUTE)
    }

    fun navigateToFetchDB() {
        navController.navigate(MainDestinations.FETCH_ROUTE)
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
