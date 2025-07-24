package com.banyumas.wisata.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import com.banyumas.wisata.core.designsystem.components.WbNavigationBar
import com.banyumas.wisata.core.designsystem.components.WbNavigationBarItem
import com.banyumas.wisata.core.model.LocalUser
import com.banyumas.wisata.feature.auth.AuthGraphRoute
import com.banyumas.wisata.feature.auth.authGraph
import com.banyumas.wisata.feature.bookmarks.navigation.bookmarksScreen
import com.banyumas.wisata.feature.dashboard.navigation.DashboardGraphRoute
import com.banyumas.wisata.feature.dashboard.navigation.dashboardGraph
import com.banyumas.wisata.feature.detail.navigation.detailScreen
import com.banyumas.wisata.feature.detail.navigation.navigateToDetail
import com.banyumas.wisata.feature.profile.navigation.profileScreen
import com.banyumas.wisata.navigation.TopLevelDestination
import timber.log.Timber

@Composable
fun WbApp() {
    val appState = rememberWbAppState()
    val currentUser = LocalUser.current

    LaunchedEffect(currentUser) {
        Timber.d("WbApp: currentUser changed to $currentUser")

        if (currentUser != null) {
            val currentRoute = appState.navController.currentDestination?.route
            if (currentRoute?.contains("auth") == true || currentRoute == null) {
                Timber.d("WbApp: Navigating to dashboard")
                appState.navController.navigate(DashboardGraphRoute) {
                    // Clear auth stack
                    popUpTo(AuthGraphRoute) { inclusive = true }
                }
            }
        } else {
            val currentRoute = appState.navController.currentDestination?.route
            if (currentRoute?.contains("auth") != true) {
                Timber.d("WbApp: Navigating to auth")
                appState.navController.navigate(AuthGraphRoute) {
                    // Clear main app stack
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    // Determine start destination
    val startDestination = if (currentUser != null) DashboardGraphRoute else AuthGraphRoute

    // Conditional UI based on user state
    if (currentUser != null) {
        // Authenticated user - show main app with bottom navigation
        MainAppScaffold(
            appState = appState,
            startDestination = startDestination
        )
    } else {
        // Unauthenticated user - show auth screens only
        AuthOnlyNavHost(
            appState = appState,
            startDestination = startDestination
        )
    }
}

@Composable
private fun MainAppScaffold(
    appState: WbAppState,
    startDestination: Any
) {
    Scaffold(
        bottomBar = {
            WbNavigationBar {
                TopLevelDestination.entries.forEach { destination ->
                    val isSelected = appState.currentTopLevelDestination == destination
                    WbNavigationBarItem(
                        selected = isSelected,
                        onClick = { appState.navigateToTopLevelDestination(destination) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) destination.selectedIcon else destination.unselectedIcon,
                                contentDescription = null
                            )
                        },
                        selectedIcon = {
                            Icon(
                                imageVector = destination.selectedIcon,
                                contentDescription = null
                            )
                        },
                        label = { Text(stringResource(id = destination.iconTextId)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        UnifiedNavHost(
            appState = appState,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun AuthOnlyNavHost(
    appState: WbAppState,
    startDestination: Any
) {
    UnifiedNavHost(
        appState = appState,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun UnifiedNavHost(
    appState: WbAppState,
    startDestination: Any,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = appState.navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // AUTH GRAPH - Always available
        authGraph(
            navController = appState.navController,
            onLoginSuccess = {
                // Don't manually navigate - let LaunchedEffect handle it
                Timber.d("AuthGraph: Login success, LocalUser should update automatically")
            },
            onNavigateToRegister = appState::navigateToRegister,
            onNavigateToResetPassword = appState::navigateToResetPassword,
            onBackToLogin = appState::navigateUp,
        )

        // MAIN APP GRAPHS - Only accessible when authenticated
        dashboardGraph(
            onDestinationClick = { destinationId ->
                appState.navController.navigateToDetail(destinationId)
            },
            navController = appState.navController
        )

        bookmarksScreen(
            onDestinationClick = { destinationId ->
                appState.navController.navigateToDetail(destinationId)
            }
        )

        profileScreen(
            onLogout = {
                // Don't manually navigate - let LaunchedEffect handle it
                Timber.d("ProfileScreen: Logout triggered, LocalUser should update automatically")
            },
            onDelete = {}
        )

        detailScreen(
            onBackClick = { appState.navigateUp() },
            onEditClick = { destinationId ->
                // appState.navController.navigateToManageDestination(destinationId)
            }
        )
    }
}