package com.banyumas.wisata.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.banyumas.wisata.core.designsystem.components.HomeSection
import com.banyumas.wisata.core.designsystem.components.WBBottomBar
import com.banyumas.wisata.core.designsystem.components.WisataBanyumasScaffold
import com.banyumas.wisata.core.designsystem.components.addHomeGraph
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.feature.dashboard.DashboardScreen
import com.banyumas.wisata.feature.login.LoginScreen
import com.banyumas.wisata.feature.register.RegisterScreen
import com.banyumas.wisata.feature.resetpassword.ResetPasswordScreen
import com.banyumas.wisata.ui.DetailScreen
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun AppNavigation(
    userViewModel: UserViewModel
) {
    WisataBanyumasTheme {
        val destinationViewModel: DestinationViewModel = hiltViewModel()
        val banyumasNavController = rememberWBNavController()
        val navController = banyumasNavController.navController
        NavHost(
            navController = navController,
            startDestination = Screen.SplashScreen.route
        ) {
            composable(
                route = Screen.LoginScreen.route,
            ) {
                LoginScreen(
                    navigateToHome = banyumasNavController::navigateToHome,
                    onForgotPasswordClick = banyumasNavController::navigateToResetPassword,
                    onSignupClick = banyumasNavController::navigateToRegister
                )
            }
            composable(
                route = Screen.RegisterScreen.route
            ) {
                RegisterScreen(
                    onSignInClick = banyumasNavController::navigateToLogin
                )
            }
            composable(
                route = Screen.ForgotPasswordScreen.route
            ) {
                ResetPasswordScreen(onSignInClick = banyumasNavController::navigateToLogin)
            }
            composable(
                route = Screen.DashboardScreen.DASHBOARD_ROUTE,
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                )
            ) { backstackEntry ->
                val userId = backstackEntry.arguments?.getString("userId") ?: ""
                DashboardScreen(
                    userId = userId,
                    navigateToDetail = { destinationId ->
                        banyumasNavController.navigateToDetail(destinationId, backstackEntry)
                    },
                    onLogout = {},
                    onAddClick = {})
            }
            composable(
                route = Screen.HomeScreen.HOME_ROUTE,
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: ""
                MainContainer(
                    userId = userId,
                    onDestinationSelected = banyumasNavController::navigateToDetail,
                    userViewModel = userViewModel,
                    destinationViewModel = destinationViewModel,
                )
            }
            composable(
                route = Screen.DetailScreen.DETAIL_ROUTE,
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
                    onBackClick = banyumasNavController::upPress,
                    onEditClick = {}
                )
            }
        }
    }
}

@Composable
fun MainContainer(
    userId: String,
    userViewModel: UserViewModel,
    destinationViewModel: DestinationViewModel,
    modifier: Modifier = Modifier,
    onDestinationSelected: (placeId: String, from: NavBackStackEntry) -> Unit
) {
    val nestedNavController = rememberWBNavController()
    val navBackStackEntry by nestedNavController.navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    WisataBanyumasScaffold(
        bottomBar = {
            WBBottomBar(
                tabs = HomeSection.entries.toTypedArray(),
                currentRoute = currentRoute
                    ?: HomeSection.FEED.route,
                navigateToRoute = nestedNavController::navigateToBottomBarRoute,
            )
        },
        modifier = modifier
    ) { padding ->
        NavHost(
            navController = nestedNavController.navController,
            startDestination = HomeSection.FEED.route
        ) {
            addHomeGraph(
                userViewModel = userViewModel,
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
                userId = userId,
                destinationViewModel = destinationViewModel
            )
        }
    }
}