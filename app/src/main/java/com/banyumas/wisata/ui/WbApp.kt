package com.banyumas.wisata.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun AppNavigation() {
    val appState = rememberWbAppState()
    //Ganti localUser dengan state dari viewModel
    val currentUser = LocalUser.current

    if (currentUser != null) {
        MainAppScaffold(
            appState = appState,
        )
    } else {
        AuthNavHost(
            appState = appState,
        )
    }
}

@Composable
fun MainAppScaffold(
    appState: WbAppState,
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
        NavHost(
            navController = appState.navController,
            startDestination = DashboardGraphRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
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
                onLogout = { appState.navigateToLogin() },
                onDelete = {}
            )
            detailScreen(
                onBackClick = { appState.navigateUp() },
                onEditClick = { destinationId ->
//                    appState.navController.navigateToManageDestination(destinationId)
                }
            )
        }
    }
}


@Composable
fun AuthNavHost(
    appState: WbAppState,
) {
    NavHost(
        navController = appState.navController,
        modifier = Modifier.fillMaxSize(),
        startDestination = AuthGraphRoute
    ) {
        authGraph(
            navController = appState.navController,
            onLoginSuccess = appState::navigateToHome,
            onNavigateToRegister = appState::navigateToRegister,
            onNavigateToResetPassword = appState::navigateToResetPassword,
            onBackToLogin = appState::navigateUp,
        )
    }
}
