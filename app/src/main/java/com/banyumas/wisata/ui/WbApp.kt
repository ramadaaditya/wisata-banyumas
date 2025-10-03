package com.banyumas.wisata.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import com.banyumas.wisata.core.designsystem.components.WbNavigationBar
import com.banyumas.wisata.core.designsystem.components.WbNavigationBarItem
import com.banyumas.wisata.core.model.LocalUser
import com.banyumas.wisata.feature.auth.AuthGraphRoute
import com.banyumas.wisata.feature.auth.authGraph
import com.banyumas.wisata.feature.bookmarks.navigation.bookmarksScreen
import com.banyumas.wisata.feature.dashboard.navigation.DashboardRoute
import com.banyumas.wisata.feature.dashboard.navigation.dashboardGraph
import com.banyumas.wisata.feature.detail.navigation.detailScreen
import com.banyumas.wisata.feature.detail.navigation.navigateToDetail
import com.banyumas.wisata.feature.profile.navigation.profileScreen
import com.banyumas.wisata.navigation.TopLevelDestination

@Composable
fun WbApp(
    appState: WbAppState
) {
    val currentUser = LocalUser.current
    val startDestination = remember {
        if (currentUser != null) DashboardRoute else AuthGraphRoute
    }
    Scaffold(
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                WbNavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        WbNavigationBarItem(
                            selected = appState.currentTopLevelDestination == destination,
                            onClick = { appState.navigateToTopLevelDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = if (appState.currentTopLevelDestination == destination) {
                                        destination.selectedIcon
                                    } else {
                                        destination.unselectedIcon
                                    },
                                    contentDescription = null,
                                )
                            },
                            label = { Text(text = stringResource(id = destination.iconTextId)) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            NavHost(
                navController = appState.navController,
                startDestination = startDestination,
            ) {
                authGraph(
                    onLoginSuccess = {
                        appState.navController.navigate(DashboardRoute) {
                            popUpTo(AuthGraphRoute) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = appState::navigateToRegister,
                    onNavigateToResetPassword = appState::navigateToResetPassword,
                    onBackToLogin = appState::navigateUp,
                    navController = appState.navController
                )

                dashboardGraph(
                    onDestinationClick = { destinationId ->
                        appState.navController.navigateToDetail(destinationId)
                    },
                )

                bookmarksScreen(
                    onDestinationClick = { destinationId ->
                        appState.navController.navigateToDetail(destinationId)
                    }
                )

                profileScreen(
                    onLogout = {
                        appState.navController.navigate(AuthGraphRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDelete = {}
                )

                detailScreen(
                    onBackClick = { appState.navigateUp() },
                    onEditClick = { /* ... */ }
                )
            }
        }
    }
}