package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.banyumas.wisata.view.components.HomeSection
import com.banyumas.wisata.view.components.WBBottomBar
import com.banyumas.wisata.view.components.WisataBanyumasScaffold
import com.banyumas.wisata.view.components.addHomeGraph
import com.banyumas.wisata.view.detail.DetailScreen
import com.banyumas.wisata.view.initial.FetchDatabase
import com.banyumas.wisata.view.login.LoginScreen
import com.banyumas.wisata.view.login.RegisterScreen
import com.banyumas.wisata.view.login.ResetPasswordScreen
import com.banyumas.wisata.view.navigation.MainDestinations
import com.banyumas.wisata.view.navigation.rememberWBNavController
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Preview
@Composable
fun AppNavigation() {
    WisataBanyumasTheme {
        val banyumasNavController = rememberWBNavController()
        NavHost(
            navController = banyumasNavController.navController,
            startDestination = MainDestinations.SPLASH_ROUTE
        ) {
            composable(
                route = MainDestinations.SPLASH_ROUTE
            ) {
                SplashScreen(
                    navigateToLogin = banyumasNavController::navigateToFetchDB,
                    navigateToHome = banyumasNavController::navigateToHome
                )
            }
            composable(
                route = MainDestinations.LOGIN_ROUTE
            ) {
                LoginScreen(
                    navigateToHome = banyumasNavController::navigateToHome,
                    onForgotPasswordClick = banyumasNavController::navigateToResetPassword,
                    onSignupClick = banyumasNavController::navigateToRegister
                )
            }
            composable(
                route = MainDestinations.REGISTER_ROUTE
            ) {
                RegisterScreen(
                    onSignInClick = banyumasNavController::navigateToLogin
                )
            }
            composable(
                route = MainDestinations.FETCH_ROUTE
            ) {
                FetchDatabase()
            }
            composable(
                route = MainDestinations.RESET_ROUTE
            ) {
                ResetPasswordScreen(onSignInClick = banyumasNavController::navigateToLogin)
            }
            composable(
                route = MainDestinations.HOME_ROUTE
            ) {
                MainContainer(onDestinationSelected = banyumasNavController::navigateToDetail)
            }
            composable(
                "${MainDestinations.DETAIL_ROUTE}/" +
                        "{${MainDestinations.DESTINATION_ID_KEY}}",
                arguments = listOf(
                    navArgument(MainDestinations.DESTINATION_ID_KEY) {
                        type = NavType.LongType
                    }
                ),
            ) { backStackEntry ->
                val arguments = requireNotNull(backStackEntry.arguments)
                val destinationId = arguments.getString(MainDestinations.DESTINATION_ID_KEY)
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
                currentRoute = currentRoute ?: HomeSection.FEED.route,
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
                onDestinationSelected = onDestinationSelected,
                modifier = Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding)
            )
        }
    }
}