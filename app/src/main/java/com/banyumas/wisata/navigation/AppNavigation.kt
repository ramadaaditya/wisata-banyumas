package com.banyumas.wisata.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.banyumas.wisata.core.model.LocalUser
import com.banyumas.wisata.feature.auth.AuthGraphRoute
import com.banyumas.wisata.feature.auth.authGraph
import com.banyumas.wisata.feature.bookmarks.navigation.bookmarksScreen
import com.banyumas.wisata.feature.dashboard.navigation.DashboardRoute
import com.banyumas.wisata.feature.dashboard.navigation.dashboardSection
import com.banyumas.wisata.feature.profile.navigation.profileScreen

@Composable
fun AppNavigation() {
    val appState = rememberWbAppState()
    val currentUser = LocalUser.current

    Scaffold(
        bottomBar = {
            WBBottomBar(
                destinations = TopLevelDestination.entries,
                onNavigateToDestination = appState::navigateToTopLevelDestination,
                currentDestination = appState.currentDestination
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = appState.navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = if (currentUser != null) DashboardRoute else AuthGraphRoute
        ) {
            authGraph(
                onLoginSuccess = appState::navigateToHome,
                onNavigateToRegister = appState::navigateToRegister,
                onNavigateToResetPassword = appState::navigateToResetPassword,
                onBackToLogin = appState::navigateUp
            )
            dashboardSection(
                onDestinationClick = {},
                detailDestination = {}
            )
            bookmarksScreen(
                onDestinationClick = {}
            )
            profileScreen(
                onLogout = appState::navigateToLogin,
                onDelete = {}
            )
        }
    }
}
