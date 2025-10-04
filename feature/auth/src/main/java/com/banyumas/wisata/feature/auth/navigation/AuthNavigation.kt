package com.banyumas.wisata.feature.auth.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.feature.auth.screen.LoginScreen
import com.banyumas.wisata.feature.auth.screen.RegisterScreen
import com.banyumas.wisata.feature.auth.screen.ResetPasswordScreen
import kotlinx.serialization.Serializable

@Serializable
object AuthGraphRoute

@Serializable
object LoginRoute

@Serializable
object RegisterRoute

@Serializable
object ResetPasswordRoute

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
private fun rememberSharedAuthViewModel(navController: NavController): UserViewModel {
    // Dapatkan back stack entry dari parent navigation graph
    val parentEntry = remember {
        navController.getBackStackEntry(AuthGraphRoute)
    }
    // Buat/dapatkan ViewModel yang terikat pada parent entry tersebut
    return hiltViewModel(parentEntry)
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToResetPassword: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    navigation<AuthGraphRoute>(
        startDestination = LoginRoute,
    ) {
        composable<LoginRoute> {
            // Cukup panggil helper function kita
            val authViewModel = rememberSharedAuthViewModel(navController)
            LoginScreen(
                navigateToHome = onLoginSuccess,
                onForgotPasswordClick = onNavigateToResetPassword,
                onSignupClick = onNavigateToRegister,
                viewModel = authViewModel,
            )
        }

        composable<RegisterRoute> {
            // Gunakan helper yang sama
            val authViewModel = rememberSharedAuthViewModel(navController)
            RegisterScreen(
                onSignInClick = onBackToLogin,
                viewModel = authViewModel,
            )
        }

        composable<ResetPasswordRoute> {
            // Gunakan helper yang sama
            val authViewModel = rememberSharedAuthViewModel(navController)
            ResetPasswordScreen(
                viewModel = authViewModel,
                onSignInClick = onBackToLogin,
            )
        }
    }
}