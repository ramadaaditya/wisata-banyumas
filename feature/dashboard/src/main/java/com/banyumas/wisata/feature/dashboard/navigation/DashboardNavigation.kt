package com.banyumas.wisata.feature.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.banyumas.wisata.feature.dashboard.DashboardScreen
import kotlinx.serialization.Serializable

@Serializable
data object DashboardRoute

@Serializable
data object DashboardBaseRoute

fun NavController.navigateToDashboard(navOptions: NavOptions) =
    navigate(route = DashboardRoute, navOptions)

fun NavGraphBuilder.dashboardSection(
    onDestinationClick: (String) -> Unit,
    detailDestination: NavGraphBuilder.() -> Unit
) {
    navigation<DashboardBaseRoute>(startDestination = DashboardRoute) {
        composable<DashboardRoute> {
            DashboardScreen(
                onDestinationClick = onDestinationClick,
                navigateToAddDestination = {}
            )
        }
        detailDestination()
    }
}