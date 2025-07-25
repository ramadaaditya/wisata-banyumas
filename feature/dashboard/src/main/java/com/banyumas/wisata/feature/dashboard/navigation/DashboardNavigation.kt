package com.banyumas.wisata.feature.dashboard.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.feature.dashboard.DashboardScreen
import com.banyumas.wisata.feature.dashboard.DashboardViewModel
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

fun NavController.navigateToDashboard(navOptions: NavOptions) =
    navigate(route = DashboardRoute, navOptions)

fun NavGraphBuilder.dashboardGraph(
    onDestinationClick: (destinationId: String) -> Unit,
) {
    composable<DashboardRoute> {
        val userViewModel = hiltViewModel<UserViewModel>()
        val dashboardViewModel = hiltViewModel<DashboardViewModel>()
        DashboardScreen(
            onDestinationClick = onDestinationClick,
            navigateToAddDestination = {},
            userViewModel = userViewModel,
            dashboardViewModel = dashboardViewModel
        )
    }
}