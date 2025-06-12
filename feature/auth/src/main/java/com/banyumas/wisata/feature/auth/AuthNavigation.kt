package com.banyumas.wisata.feature.auth

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.banyumas.wisata.core.model.User
import kotlinx.serialization.Serializable

@Serializable object AuthGraphRoute

@Serializable object LoginRoute

@Serializable object RegisterRoute

@Serializable object ResetPasswordRoute

// Fungsi untuk navigasi ke graph ini
fun NavController.navigateToAuthGraph(navOptions: NavOptions? = null) {
    navigate(AuthGraphRoute, navOptions)
}

// Fungsi builder untuk graph ini
fun NavGraphBuilder.authGraph(
    onLoginSuccess: (User) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onBackToLogin: () -> Unit
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute
    ) {
        composable<LoginRoute> {
            val authViewModel: UserViewModel = hiltViewModel()
            LoginScreen(
                navigateToHome = onLoginSuccess,
                onForgotPasswordClick = onNavigateToResetPassword,
                onSignupClick = onNavigateToRegister,
                viewModel = authViewModel
            )
        }
        composable<RegisterRoute> {
            val authViewModel: UserViewModel = hiltViewModel()
            RegisterScreen(
                onSignInClick = onBackToLogin,
                viewModel = authViewModel
            )
        }
        composable<ResetPasswordRoute> {
            val authViewModel: UserViewModel = hiltViewModel()
            ResetPasswordScreen(
                viewModel = authViewModel,
                onSignInClick = onBackToLogin
            )
        }
    }
}