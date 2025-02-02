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
import androidx.compose.runtime.rememberUpdatedState
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
import kotlinx.coroutines.delay


@Composable
fun AppNavigation(
    viewModel: AppViewModel = hiltViewModel(),
    destinationViewModel: DestinationViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val userRole by rememberUpdatedState(viewModel.userRole)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val destinationState by destinationViewModel.selectedDestination.collectAsStateWithLifecycle()
    val userId = (authState as? UiState.Success)?.data?.id.orEmpty()

    var isSplashScreenVisible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        isSplashScreenVisible = false
    }

    // 🔥 Navigasi hanya dilakukan setelah Splash selesai
    LaunchedEffect(authState, isSplashScreenVisible) {
        if (!isSplashScreenVisible) { // ✅ Cek Splash sudah selesai
            when (authState) {
                is UiState.Success -> {
                    val user = (authState as UiState.Success).data
                    if (user.id.isNotBlank()) {
                        val targetScreen = if (userRole == Role.ADMIN) {
                            Screen.DashboardScreen.route
                        } else {
                            Screen.Home.createRoute(user.id) // ✅ Pastikan userId diteruskan
                        }

                        Log.d(
                            "AppNavigation",
                            "Navigating to $targetScreen with userId: ${user.id}"
                        )
                        navController.navigate(targetScreen) {
                            popUpTo(Screen.SplashScreen.route) { inclusive = true }
                        }
                    }
                }

                is UiState.Error, UiState.Empty -> {
                    Log.e("AppNavigation", "AuthState is error or empty, navigating to LoginScreen")
                    navController.navigate(Screen.LoginScreen.route) {
                        popUpTo(Screen.SplashScreen.route) { inclusive = true }
                    }
                }

                else -> {} // Tunggu sampai authState stabil
            }
        }
    }


    Scaffold(topBar = {
        val currentRoute = navBackStackEntry?.destination?.route
        when (currentRoute) {
            Screen.DetailScreen.ROUTE -> {
                val destinationId = navBackStackEntry?.arguments?.getString("destinationId") ?: ""
                val isAdmin = userRole == Role.ADMIN

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
                                        userId, destinationId, !isFavorite
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
                onBackClick = { navController.popBackStack() })

            Screen.UpdateScreen.ROUTE -> CustomTopBar(
                title = "Update Destinasi",
                onBackClick = { navController.popBackStack() })
        }
    }, floatingActionButton = {
        val currentRoute = navBackStackEntry?.destination?.route
        when {
            currentRoute == Screen.DashboardScreen.route && userRole == Role.ADMIN -> {
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

            currentRoute == Screen.DetailScreen.ROUTE && userRole == Role.ADMIN -> {
                val destinationId =
                    navBackStackEntry?.arguments?.getString("destinationId") ?: ""
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.UpdateScreen.createRoute(destinationId))
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
        }
    }, floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            val currentRoute = navBackStackEntry?.destination?.route
            if (currentRoute?.startsWith(Screen.Home.ROUTE.split("/{")[0]) == true ||
                currentRoute == Screen.ProfileScreen.route ||
                currentRoute == Screen.FavoriteScreen.route
            ) {
                BottomNavigation(
                    navController = navController,
                    currentUserId = userId
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = if (isSplashScreenVisible) Screen.SplashScreen.route else Screen.LoginScreen.route,
            modifier = Modifier.fillMaxSize()
        ) {

            composable(Screen.SplashScreen.route) {
                SplashScreen {
                    Log.d("SplashScreen", "Splash Finished")
                }
            }

            // LoginScreen
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

            // DashboardScreen
            composable(Screen.DashboardScreen.route) {
                DashboardScreen(
                    userId = userId,
                    navigateToDetail = { destinationId ->
                        navController.navigate(
                            Screen.DetailScreen.createRoute(
                                destinationId, userId
                            )
                        )
                    },
                )
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
                    onSubmit = { navController.popBackStack() },
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
                            onSubmit = { navController.popBackStack() },
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

