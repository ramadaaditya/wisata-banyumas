package com.banyumas.wisata.feature.profile.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.feature.profile.ProfileScreen
import kotlinx.serialization.Serializable

@Serializable
object ProfileRoute

fun NavController.navigateToProfile(navOptions: NavOptions? = null) =
    navigate(route = ProfileRoute, navOptions)

fun NavGraphBuilder.profileScreen(
    onLogout: () -> Unit,
    onDelete: () -> Unit
) {
    composable<ProfileRoute> {
        val userViewModel = hiltViewModel<UserViewModel>()
        ProfileScreen(
            viewModel =
                userViewModel,
            onLogout = onLogout,
            onDelete = onDelete
        )
    }
}