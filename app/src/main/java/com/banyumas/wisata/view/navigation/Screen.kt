package com.banyumas.wisata.view.navigation


sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data class DetailScreen(val destinationId: String) : Screen("detail/{destinationId}") {
        companion object {
            const val ROUTE = "detail/{destinationId}"
            fun createRoute(destinationId: String) = "detail/$destinationId"
        }
    }

    data object Search : Screen("search")
    data object FavoriteScreen : Screen("favorite")
    data object ProfileScreen : Screen("profile")
    data class UpdateScreen(val destinationId: String) : Screen("update/{destinationId}") {
        companion object {
            const val ROUTE = "update/{destinationId}"
            fun createRoute(destinationId: String) = "update/$destinationId"
        }
    }

    data object AddScreen : Screen("add")
    data object SplashScreen : Screen("splash")
    data object LoginScreen : Screen("login")
    data object RegisterScreen : Screen("register")
    data object ForgotPasswordScreen : Screen("forgot-password")
    data object DashboardScreen : Screen("dashboard")
}