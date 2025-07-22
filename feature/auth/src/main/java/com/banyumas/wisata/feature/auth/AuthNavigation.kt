package com.banyumas.wisata.feature.auth

import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import kotlinx.serialization.Serializable

@Serializable
object AuthGraphRoute

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object ResetPasswordRoute

fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    navigate(AuthGraphRoute, navOptions)
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onBackToLogin: () -> Unit
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> { backStackEntry ->
            val parentEntry =
                remember(backStackEntry) { navController.getBackStackEntry(AuthGraphRoute) }
            val authViewModel: UserViewModel = hiltViewModel(parentEntry)

            LoginScreen(
                navigateToHome = onLoginSuccess,
                onForgotPasswordClick = onNavigateToResetPassword,
                onSignupClick = onNavigateToRegister,
                viewModel = authViewModel
            )
        }

        composable<RegisterRoute> {
            val parentEntry = navController.getBackStackEntry(AuthGraphRoute)
            val authViewModel: UserViewModel = hiltViewModel(parentEntry)

            RegisterScreen(
                onSignInClick = onBackToLogin,
                viewModel = authViewModel
            )
        }

        composable<ResetPasswordRoute> {
            val parentEntry = navController.getBackStackEntry(AuthGraphRoute)
            val authViewModel: UserViewModel = hiltViewModel(parentEntry)

            ResetPasswordScreen(
                viewModel = authViewModel,
                onSignInClick = onBackToLogin
            )
        }
    }
}