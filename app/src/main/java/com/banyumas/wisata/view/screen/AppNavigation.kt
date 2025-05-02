package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.banyumas.wisata.model.Role
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.BottomNavigation
import com.banyumas.wisata.view.dashboard.DashboardScreen
import com.banyumas.wisata.view.detail.DetailScreen
import com.banyumas.wisata.view.home.FavoriteScreen
import com.banyumas.wisata.view.home.HomeScreen
import com.banyumas.wisata.view.home.ProfileScreen
import com.banyumas.wisata.view.login.LoginScreen
import com.banyumas.wisata.view.login.RegisterScreen
import com.banyumas.wisata.view.login.ResetPasswordScreen
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.update.AddOrUpdateDestinationScreen
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun AppNavigation(){

    val navController = rememberNavController()
    val userViewModel: UserViewModel = hiltViewModel()
    val destinationViewModel: DestinationViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val destinationState by destinationViewModel.selectedDestination.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data

    Scaffold(
        bottomBar = {
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == Screen.Home.route ||
                currentRoute == Screen.ProfileScreen.route ||
                currentRoute == Screen.FavoriteScreen.route
            ) {
                BottomNavigation(navController = navController)
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route,
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen(
                    viewModel = userViewModel,
                    navigateToHome = { user ->
                        val targetScreen = if (user.role == Role.ADMIN)
                            Screen.DashboardScreen.route
                        else Screen.Home.route
                        navController.navigate(targetScreen) {
                            popUpTo(Screen.SplashScreen.route) {
                                inclusive = true
                            }
                        }
                    },
                    navigateToLogin = {
                        Log.d("AppNavigation", "Navigating to LoginScreen")
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.SplashScreen.route) {
                                inclusive = true
                            }
                        }
                    }
                )
            }

            composable(Screen.LoginScreen.route) {
                LoginScreen(
                    viewModel = userViewModel,
                    navigateToDashboard = {
                        navController.navigate(Screen.DashboardScreen.route) {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    },
                    navigateToHome = {
                        navController.navigate(
                            Screen.Home.route
                        ) {
                            popUpTo(Screen.LoginScreen.route) { inclusive = true }
                        }
                    },
                    onSignupClick = { navController.navigate(Screen.RegisterScreen.route) },
                    onForgotPasswordClick = {
                        navController.navigate(Screen.ForgotPasswordScreen.route)
                    }
                )
            }

            composable(Screen.RegisterScreen.route) {
                RegisterScreen(
                    onSignInClick = { navController.navigate(Screen.LoginScreen.route) },
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    navigateToDetail = { destinationId ->
                        val route = Screen.DetailScreen.createRoute(destinationId)
                        Log.d("AppNavigation", "Navigating to DetailScreen: $route")
                        navController.navigate(route)
                    },
                    userViewModel = userViewModel,
                    destinationViewModel = destinationViewModel,
                    innerPadding = innerPadding
                )
            }

            composable(Screen.DashboardScreen.route) {
                DashboardScreen(
                    navigateToDetail = { destinationId ->
                        val route = Screen.DetailScreen.createRoute(destinationId)
                        Log.d("AppNavigation", "Navigating to DetailScreen: $route")
                        navController.navigate(route)
                    },
                    onLogout = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.DashboardScreen.route) { inclusive = true }
                        }
                    },
                    userViewModel = userViewModel,
                    onAddClick = {
                        navController.navigate(Screen.AddScreen.route) {
                            popUpTo(Screen.DashboardScreen.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                route = Screen.DetailScreen.ROUTE,
                arguments = listOf(
                    navArgument("destinationId") { type = NavType.StringType }
                )
            ) { bacStackEntry ->
                val destinationId = bacStackEntry.arguments?.getString("destinationId") ?: ""
                DetailScreen(
                    userViewModel = userViewModel,
                    destinationId = destinationId,
                    onBackClick = { navController.popBackStack() },
                    onEditClick = {
                        navController.navigate(
                            Screen.UpdateScreen.createRoute(
                                destinationId
                            )
                        )
                    },
                    onAddCommentClick = {
                        navController.navigate(
                            Screen.AddReviewScreen.createRoute(
                                destinationId
                            )
                        )
                    }
                )
            }
            composable(Screen.FavoriteScreen.route) {
                FavoriteScreen(
                    userViewModel = userViewModel,
                    navigateToDetail = { destination ->
                        navController.navigate(
                            Screen.DetailScreen.createRoute(
                                destination.id
                            )
                        )
                    },
                )
            }


            composable(Screen.ProfileScreen.route) {
                ProfileScreen(
                    viewModel = userViewModel,
                    onLogout = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0)
                        }
                    },
                    onDelete = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0)
                        }
                    },
                )
            }

            composable(Screen.ForgotPasswordScreen.route) {
                ResetPasswordScreen(
                    onSignInClick = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.ForgotPasswordScreen.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(
                route = Screen.AddScreen.route
            ) {
                AddOrUpdateDestinationScreen(
                    isEditing = false,
                )
            }

            composable(
                route = Screen.UpdateScreen.ROUTE,
                arguments = listOf(navArgument("destinationId") { type = NavType.StringType })
            ) {
                val destinationId =
                    navBackStackEntry?.arguments?.getString("destinationId") ?: ""
                if (destinationId.isNotEmpty()) {
                    LaunchedEffect(destinationId) {
                        Log.d("UpdateScreen", "Fetching destination data for ID: $destinationId")
                        destinationViewModel.getDetailDestination(
                            destinationId,
                            currentUser?.id.orEmpty()
                        )
                    }
                }

                when (val selectedDestination = destinationState) {
                    is UiState.Loading -> LoadingState()
                    is UiState.Success -> {
                        AddOrUpdateDestinationScreen(
                            initialDestination = selectedDestination.data.destination,
                            isEditing = true,
                        )
                    }

                    is UiState.Error -> {
                        Text("Gagal memuat destinasi", style = MaterialTheme.typography.bodyMedium)
                    }

                    UiState.Empty -> {
                        Text(
                            "Destinasi tidak ditemukan", style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

