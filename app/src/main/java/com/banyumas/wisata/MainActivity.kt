package com.banyumas.wisata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.banyumas.wisata.data.model.User
import com.banyumas.wisata.view.components.BottomNavigation
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.screen.DashboardScreen
import com.banyumas.wisata.view.screen.DetailScreen
import com.banyumas.wisata.view.screen.FavoriteScreen
import com.banyumas.wisata.view.screen.FetchScreen
import com.banyumas.wisata.view.screen.ForgotPasswordScreen
import com.banyumas.wisata.view.screen.HomeScreen
import com.banyumas.wisata.view.screen.LoginScreen
import com.banyumas.wisata.view.screen.ProfileScreen
import com.banyumas.wisata.view.screen.RegisterScreen
import com.banyumas.wisata.view.screen.SplashScreen
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme
import com.banyumas.wisata.viewmodel.AppViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                FetchScreen()
            }
        }
    }
}

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: AppViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Scaffold with proper padding for edge-to-edge
    Scaffold(
        bottomBar = {
            if (currentRoute !in listOf(
                    Screen.SplashScreen.route,
                    Screen.LoginScreen.route,
                    Screen.RegisterScreen.route,
                    Screen.ForgotPasswordScreen.route,
                    Screen.DetailScreen.ROUTE,
                    Screen.DashboardScreen.route,
                )
            ) {
                BottomNavigation(navController = navController)
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing, // Use safeDrawing for edge-to-edge content
    ) {
        // Apply padding from Scaffold to NavHost
        NavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route,
            modifier = modifier.padding(bottom = it.calculateBottomPadding())
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen(
                    navController = navController,
                    checkLoginStatus = { true },
                    checkGoogleSignInStatus = { true },
                    getUserRole = { "USER" }
                )
            }
            composable(Screen.LoginScreen.route) {
                LoginScreen(
                    onSignInClick = { navController.navigate(Screen.Home.route) },
                    onSignUpCLick = { navController.navigate(Screen.RegisterScreen.route) },
                    onForgotPasswordClick = { navController.navigate(Screen.ForgotPasswordScreen.route) },
                    onGoogleSignInClick = { navController.navigate(Screen.Home.route) }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    navigateToDetail = { destination ->
                        navController.navigate(Screen.DetailScreen.createRoute(destination))
                    }
                )
            }
            composable(
                route = Screen.DetailScreen.ROUTE,
                arguments = listOf(navArgument("destinationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("destinationId") ?: ""
                DetailScreen(
                    destinationId = destinationId,
                    onBackClick = { navController.popBackStack() }
                )
            }
            composable(Screen.FavoriteScreen.route) {
                FavoriteScreen()
            }
            composable(Screen.RegisterScreen.route) {
                RegisterScreen()
            }
            composable(Screen.ForgotPasswordScreen.route) {
                ForgotPasswordScreen()
            }
            composable(Screen.DashboardScreen.route) {
                DashboardScreen(

                )
            }
            composable(Screen.ProfileScreen.route) {
                ProfileScreen(
                    user = User(
                        id = "1",
                        name = "John Doe",
                        email = "john@gmail.com"
                    ),
                    onLogout = { navController.navigate(Screen.Home.route) }
                )
            }
        }
    }
}


@Preview
@Composable
private fun MyAppPreview() {
    WisataBanyumasTheme {
        AppNavigation()
    }
}