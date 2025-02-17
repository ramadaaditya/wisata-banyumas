package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.BottomNavigation
import com.banyumas.wisata.view.components.CustomTopBar
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel


@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val userViewModel: UserViewModel = hiltViewModel()
    val destinationViewModel: DestinationViewModel = hiltViewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val destinationState by destinationViewModel.selectedDestination.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data

    Scaffold(
        topBar = {
            val currentRoute = navBackStackEntry?.destination?.route
            when (currentRoute) {
                Screen.DetailScreen.ROUTE -> {
                    CustomTopBar(
                        title = "Detail Wisata",
                        onBackClick = { navController.popBackStack() },
//                        actions = {
//                            if (destinationState is UiState.Success) {
//                                val destination =
//                                    (destinationState as UiState.Success<UiDestination>).data
//                                val isFavorite = destination.isFavorite
//                                IconButton(onClick = {
//                                    destinationViewModel.toggleFavorite(
//                                        currentUser?.id.orEmpty(),
//                                        destinationId,
//                                        !isFavorite
//                                    )
//                                }) {
//                                    Icon(
//                                        imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Default.FavoriteBorder,
//                                        contentDescription = "Toggle Favorite"
//                                    )
//                                }
//                            }
//                        }
                    )
                }

                Screen.AddScreen.route -> CustomTopBar(
                    title = "Tambah Destinasi",
                    onBackClick = { navController.popBackStack() }
                )

                Screen.UpdateScreen.ROUTE -> CustomTopBar(
                    title = "Update Destinasi",
                    onBackClick = { navController.popBackStack() }
                )
            }
        },
        bottomBar = {
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute == Screen.Home.route ||
                currentRoute == Screen.ProfileScreen.route ||
                currentRoute == Screen.FavoriteScreen.route
            ) {
                BottomNavigation(navController = navController)
            }
        }, floatingActionButton = {
            val currentRoute = navBackStackEntry?.destination?.route
            when {
                currentRoute == Screen.DashboardScreen.route && currentUser?.role == Role.ADMIN -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(Screen.AddScreen.route)
                        },
                        containerColor = AppTheme.colorScheme.primary,
                        contentColor = AppTheme.colorScheme.onPrimary,
                        modifier = Modifier.offset(y = (-32).dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Tambah Wisata",
                        )
                    }
                }

                currentRoute == Screen.DetailScreen.ROUTE -> {
                    val destinationId =
                        navBackStackEntry?.arguments?.getString("destinationId") ?: ""
                    when (currentUser?.role) {
                        Role.ADMIN -> {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(
                                        Screen.UpdateScreen.createRoute(
                                            destinationId
                                        )
                                    )
                                },
                                containerColor = AppTheme.colorScheme.primary,
                                contentColor = AppTheme.colorScheme.onPrimary,
                                modifier = Modifier.offset(y = (-32).dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit Destinasi",
                                )
                            }
                        }

                        else -> {
                            FloatingActionButton(
                                onClick = {
                                    navController.navigate(
                                        Screen.AddReviewScreen.createRoute(
                                            destinationId
                                        )
                                    )
                                },
                                containerColor = AppTheme.colorScheme.primary,
                                contentColor = AppTheme.colorScheme.onPrimary,
                                modifier = Modifier.offset(y = (-32).dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Tambah Review"
                                )
                            }
                        }

                    }
                }
            }
        }, floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route,
            modifier = Modifier.fillMaxSize()
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
                    userViewModel = userViewModel,
                    navigateToDetail = { destinationId ->
                        val route = Screen.DetailScreen.createRoute(destinationId)
                        Log.d("AppNavigation", "Navigating to DetailScreen: $route")
                        navController.navigate(route)
                    },
                )
            }

            composable(
                route = Screen.AddReviewScreen.ROUTE,
                arguments = listOf(navArgument("destinationId") { type = NavType.StringType })
            ) {
                val destinationId = it.arguments?.getString("destinationId") ?: ""

                AddReviewScreen(
                    userViewModel = userViewModel,
                    viewModel = destinationViewModel,
                    destinationId = destinationId,
                    innerPadding = innerPadding,
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
                    innerPadding = innerPadding,
                    userViewModel = userViewModel
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
                    innerPadding = innerPadding,
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
                    innerPadding = innerPadding
                )
            }


            composable(Screen.ProfileScreen.route) {
                ProfileScreen(
                    viewModel = userViewModel,
                    onLogout = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0)
                        }
                    }, innerPadding = innerPadding
                )
            }

            composable(Screen.ForgotPasswordScreen.route) {
                ResetPasswordScreen(
                    onSignInClick = {
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(Screen.ForgotPasswordScreen.route) { inclusive = true }
                        }
                    },
                    onResetPasswordSuccess = {},
                    innerPadding = innerPadding
                )
            }

            composable(
                route = Screen.AddScreen.route
            ) {
                AddOrUpdateDestinationScreen(
                    isEditing = false,
                    innerPadding = innerPadding,
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
                        destinationViewModel.getDestinationById(
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
                            innerPadding = innerPadding,
                        )
                    }

                    is UiState.Error -> {
                        Text("Gagal memuat destinasi", style = AppTheme.typography.body)
                    }

                    UiState.Empty -> {
                        Text(
                            "Destinasi tidak ditemukan", style = AppTheme.typography.body
                        )
                    }
                }
            }
        }
    }
}

