package com.banyumas.wisata.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.Role
import com.banyumas.wisata.feature.bookmarks.BookmarksScreen
import com.banyumas.wisata.feature.dashboard.DashboardScreen
import com.banyumas.wisata.feature.auth.LoginScreen
import com.banyumas.wisata.feature.profile.ProfileScreen
import com.banyumas.wisata.feature.auth.RegisterScreen
import com.banyumas.wisata.feature.auth.ResetPasswordScreen
import com.banyumas.wisata.ui.DetailScreen
import com.banyumas.wisata.feature.auth.UserViewModel


const val AUTH_GRAPH_ROUTE = "auth_graph"
const val MAIN_GRAPH_ROUTE = "main_graph"

@Composable
fun AppNavigation(
    userViewModel: UserViewModel
) {
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val banyumasNavController = rememberWBNavController()

    WisataBanyumasTheme {
        when (val state = authState) {
            is UiState.Loading, is UiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is UiState.Error, is UiState.Success -> {
                val user = if (state is UiState.Success) state.data else null
                val startDestination = if (user != null) MAIN_GRAPH_ROUTE else AUTH_GRAPH_ROUTE
                NavHost(
                    navController = banyumasNavController.navController,
                    startDestination = startDestination
                ) {
                    authGraph(banyumasNavController)
                    mainGraph(banyumasNavController)
                }

            }
        }
    }
}

fun NavGraphBuilder.authGraph(navController: WBNavController) {
    navigation(
        startDestination = Screen.LoginScreen.route,
        route = AUTH_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.LoginScreen.route,
        ) {
            LoginScreen(
                navigateToHome = { user ->
                    navController.navigateToHome(user, AUTH_GRAPH_ROUTE)
                },
                onForgotPasswordClick = navController::navigateToResetPassword,
                onSignupClick = navController::navigateToRegister,
                viewModel = UserViewModel()
            )
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen(onSignInClick = { navController.navigateToLogin() })
        }
        composable(route = Screen.ForgotPasswordScreen.route) {
            ResetPasswordScreen(onSignInClick = { navController.navigateToLogin() })
        }
    }
}

fun NavGraphBuilder.mainGraph(navController: WBNavController) {
    navigation(
        route = "${MAIN_GRAPH_ROUTE}/{userId}/{role}",
        startDestination = Screen.Main.route
    ) {
        composable(route = Screen.Main.route) { backstackEntry ->
            val parentEntry = remember(backstackEntry) {
                navController.navController.getBackStackEntry("${MAIN_GRAPH_ROUTE}/{user}/{role}")
            }
            val userViewModel: UserViewModel = hiltViewModel(parentEntry)
            val userId = backstackEntry.arguments?.getString("userId") ?: ""
            val roleName = backstackEntry.arguments?.getString("role") ?: Role.USER.name
            val userRole = Role.valueOf(roleName)

            MainScreen(
                userId = userId,
                userRole = userRole,
                userViewModel = userViewModel,
                onNavigateToDetail = {}
            )
        }
        composable(
            route = Screen.DetailScreen.route,
            arguments = listOf(
                navArgument("destinationId") {
                    type = NavType.StringType
                }
            ),
        ) { backStackEntry ->
            val arguments = requireNotNull(backStackEntry.arguments)
            val destinationId = arguments.getString("destinationId")
            DetailScreen(
                destinationId ?: "",
                onBackClick = { navController.upPress() },
                onEditClick = {}
            )
        }
    }
}


@Composable
fun MainScreen(
    userId: String,
    userRole: Role,
    userViewModel: UserViewModel,
    onNavigateToDetail: (String) -> Unit
) {
    val nestedNavController = rememberNavController()
    val navBackStackEntry by nestedNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            WBBottomBar(
                destinations = TopLevelDestination.entries.toList(),
                onNavigateToDestination = { destination ->
                    nestedNavController.navigate(destination.route) {
                        popUpTo(nestedNavController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                currentDestination = currentDestination
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = nestedNavController,
            startDestination = TopLevelDestination.DASHBOARD.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(TopLevelDestination.DASHBOARD.route) {
                DashboardScreen(
                    userId = userId,
                    userRole = userRole,
                    navigateToDetail = onNavigateToDetail,
                    navigateToAddDestination = {},
                    userViewModel = userViewModel,
                )
            }
            composable(TopLevelDestination.BOOKMARKS.route) {
                BookmarksScreen(
                    navigateToDetail = onNavigateToDetail,
                )
            }
            composable(TopLevelDestination.PROFILE.route) {
                ProfileScreen(
                    onLogout = {},
                    onDelete = {}
                )
            }
        }
    }
}