package com.banyumas.wisata.feature.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.banyumas.wisata.feature.detail.DetailRouteScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailDestination(val destinationId: String)

fun NavController.navigateToDetail(destinationId: String, navOptions: NavOptions? = null) {
    this.navigate(DetailDestination(destinationId = destinationId), navOptions)
}

fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
) {
    composable<DetailDestination> {
        DetailRouteScreen(
            onBackClick = onBackClick,
            onEditClick = onEditClick,
        )
    }
}