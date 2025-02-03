package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.BottomNavigation
import com.banyumas.wisata.view.components.CustomTopBar
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.AppViewModel
import com.banyumas.wisata.viewmodel.DestinationViewModel


@Composable
fun AppNavigation(
    viewModel: AppViewModel = hiltViewModel(),
    destinationViewModel: DestinationViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val userRoleState by viewModel.userRoleState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destinationState by destinationViewModel.selectedDestination.collectAsStateWithLifecycle()
    val userId = (authState as? UiState.Success)?.data?.id.orEmpty()

    var isAdmin by remember { mutableStateOf(false) }
    var isSplashFinished by remember { mutableStateOf(false) }
    var roleUpdated by remember { mutableStateOf(false) }

    // 🔥 Pastikan role diperbarui setelah login
    LaunchedEffect(authState) {
        if (authState is UiState.Success) {
            val user = (authState as UiState.Success).data
            if (userRoleState !is UiState.Success) {
                Log.d("AppNavigation", "Fetching user role for userId: ${user.id}")
                viewModel.fetchUserRole(user.id)
            }
        }
    }

    // 🔥 Pastikan `isAdmin` diperbarui sebelum FAB dirender
    LaunchedEffect(userRoleState) {
        if (userRoleState is UiState.Success) {
            isAdmin = (userRoleState as UiState.Success<Role>).data == Role.ADMIN
            roleUpdated = true
            Log.d("AppNavigation", "Updated isAdmin: $isAdmin")
        }
    }


    Scaffold(
        topBar = {
            val currentRoute = navBackStackEntry?.destination?.route
            when (currentRoute) {
                Screen.DetailScreen.ROUTE -> {
                    val destinationId =
                        navBackStackEntry?.arguments?.getString("destinationId") ?: ""

                    CustomTopBar(title = "Detail Wisata",
                        onBackClick = { navController.popBackStack() },
                        actions = {
                            if (!isAdmin) {
                                if (destinationState is UiState.Success) {
                                    val destination =
                                        (destinationState as UiState.Success<UiDestination>).data
                                    val isFavorite = destination.isFavorite
                                    IconButton(onClick = {
                                        destinationViewModel.toggleFavorite(
                                            userId,
                                            destinationId,
                                            !isFavorite
                                        )
                                    }) {
                                        Icon(
                                            imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Default.FavoriteBorder,
                                            contentDescription = "Toggle Favorite"
                                        )
                                    }
                                }
                            }
                        }
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
        floatingActionButton = {
            val currentRoute = navBackStackEntry?.destination?.route
            Log.d(
                "AppNavigation",
                "Checking FAB visibility. Route: $currentRoute, isAdmin: $isAdmin"
            )

            if (userRoleState is UiState.Loading) {
                Log.d("AppNavigation", "Waiting for userRoleState to update, skipping FAB")
                return@Scaffold
            }

            when {
                currentRoute == Screen.DashboardScreen.ROUTE && isAdmin -> {
                    Log.d("AppNavigation", "FAB harus muncul di Dashboard")
                    FloatingActionButton(
                        onClick = { navController.navigate(Screen.AddScreen.route) },
                        containerColor = AppTheme.colorScheme.primary,
                        contentColor = AppTheme.colorScheme.onPrimary,
                        modifier = Modifier.offset(y = (-32).dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Wisata")
                    }
                }

                currentRoute == Screen.DetailScreen.ROUTE && isAdmin -> {
                    val destinationId =
                        navBackStackEntry?.arguments?.getString("destinationId") ?: ""
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
                        Icon(Icons.Default.Edit, contentDescription = "Edit Destinasi")
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute?.startsWith(Screen.Home.ROUTE.split("/{")[0]) == true ||
                currentRoute == Screen.ProfileScreen.route ||
                currentRoute == Screen.FavoriteScreen.route
            ) {
                BottomNavigation(navController = navController, currentUserId = userId)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.SplashScreen.route) {
                SplashScreen {
                    isSplashFinished = true
                    when (authState) {
                        is UiState.Success -> {
                            val user = (authState as UiState.Success).data
                            when (val roleState = userRoleState) {
                                is UiState.Success -> {
                                    val targetScreen = if (roleState.data == Role.ADMIN) {
                                        Screen.DashboardScreen.createRoute(user.id)
                                    } else {
                                        Screen.Home.createRoute(user.id)
                                    }
                                    navController.navigate(targetScreen) {
                                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                    }
                                }

                                is UiState.Loading -> {} // Tunggu hingga role di-load
                                is UiState.Error, UiState.Empty -> {
                                    navController.navigate(Screen.LoginScreen.route) {
                                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                                    }
                                }
                            }
                        }

                        is UiState.Error, UiState.Empty -> {
                            navController.navigate(Screen.LoginScreen.route) {
                                popUpTo(Screen.SplashScreen.route) { inclusive = true }
                            }
                        }

                        else -> {} // Menunggu authState stabil
                    }
                }
            }

            composable(Screen.LoginScreen.route) {
                LoginScreen(navigateToDestination = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                    }
                },
                    onSignupClick = { navController.navigate(Screen.RegisterScreen.route) },
                    onForgotPasswordClick = { navController.navigate(Screen.ForgotPasswordScreen.route) })
            }

            // RegisterScreen
            composable(Screen.RegisterScreen.route) {
                RegisterScreen(onSignInClick = { navController.navigate(Screen.LoginScreen.route) },
                    onRegisterSuccess = {
                        navController.navigate(Screen.Home.ROUTE) {
                            popUpTo(Screen.RegisterScreen.route) { inclusive = true }
                        }
                    })
            }

            // HomeScreen
            composable(
                Screen.Home.ROUTE,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val homeUserId = backStackEntry.arguments?.getString("userId").orEmpty()
                Log.d("AppNavigation", "HomeScreen created with userId: $homeUserId")

                HomeScreen(
                    userId = homeUserId,
                    navigateToDetail = { destinationId ->
                        val route = Screen.DetailScreen.createRoute(destinationId, homeUserId)
                        Log.d("AppNavigation", "Navigating to DetailScreen: $route")
                        navController.navigate(route)
                    }
                )
            }


            // DashboardScreen
            composable(
                Screen.DashboardScreen.ROUTE,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val dashboardUserId = backStackEntry.arguments?.getString("userId").orEmpty()
                Log.d("AppNavigation", "Dashboard created with userId: $dashboardUserId")

                DashboardScreen(
                    userId = dashboardUserId,
                    navigateToDetail = { destinationId ->
                        val route = Screen.DetailScreen.createRoute(destinationId, dashboardUserId)
                        Log.d("AppNavigation", "Navigating to DetailScreen: $route")
                        navController.navigate(route)
                    }
                )
            }
            // DetailScreen
            composable(
                route = Screen.DetailScreen.ROUTE,
                arguments = listOf(navArgument("destinationId") { type = NavType.StringType },
                    navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("destinationId") ?: ""
                DetailScreen(
                    destinationId = destinationId, userId = userId, innerPadding = innerPadding
                )
            }
            // FavoriteScreen
            composable(Screen.FavoriteScreen.route) {
                if (authState is UiState.Success) {
                    val user = (authState as UiState.Success).data
                    FavoriteScreen(userId = user.id, navigateToDetail = { destination ->
                        navController.navigate(
                            Screen.DetailScreen.createRoute(
                                destination.id, user.id
                            )
                        )
                    })
                }
            }


            // ProfileScreen
            composable(Screen.ProfileScreen.route) {
                ProfileScreen(
                    user = (authState as UiState.Success).data, onLogout = {
                        viewModel.logout()
                        navController.navigate(Screen.LoginScreen.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }, innerPadding = innerPadding
                )
            }

            composable(Screen.ForgotPasswordScreen.route) {
                ForgotPasswordScreen()
            }

            // Add Destination Screen
            composable(
                route = Screen.AddScreen.route
            ) {
                AddOrUpdateDestinationScreen(
                    isEditing = false,
//                    onSubmit = { navController.popBackStack() },
                    innerPadding = innerPadding
                )
            }

            // Update Destination Screen
            composable(
                route = Screen.UpdateScreen.ROUTE,
                arguments = listOf(navArgument("destinationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val destinationId = backStackEntry.arguments?.getString("destinationId") ?: ""

                // Pastikan hanya memanggil ini SEKALI dengan LaunchedEffect
                LaunchedEffect(destinationId) {
                    destinationViewModel.getDestinationById(destinationId, userId)
                }

                when (val selectedDestination =
                    destinationViewModel.selectedDestination.collectAsState().value) {
                    is UiState.Loading -> LoadingState()
                    is UiState.Success -> {
                        AddOrUpdateDestinationScreen(
                            initialDestination = selectedDestination.data.destination,
                            isEditing = true,
//                            onSubmit = { navController.popBackStack() },
                            innerPadding = innerPadding
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

