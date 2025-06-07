package com.banyumas.wisata.navigation


sealed class Screen(val route: String) {
    data object RegisterScreen : Screen("register")
    data object ForgotPasswordScreen : Screen("forgot_password")
    data object AddScreen : Screen("add")
    data object FavoriteScreen : Screen("favorite")
    data object ProfileScreen : Screen("profile")
    data object FetchScreen : Screen("fetch")
    data object SplashScreen : Screen("splash")
    data object LoginScreen : Screen("login")

    data class AddReviewScreen(val destinationId: String) : Screen(ROUTE) {
        companion object {
            const val ROUTE = "add_review/{destinationId}"
            fun createRoute(destinationId: String) = "add_review/$destinationId"
        }
    }

    data object DetailScreen : Screen("detail/{destinationId}") {
        fun createRoute(destinationId: String) =
            "detail/$destinationId"
    }

    data class UpdateScreen(val destinationId: String) : Screen(UPDATE_ROUTE) {
        companion object {
            const val UPDATE_ROUTE = "update/{destinationId}"
            fun createRoute(destinationId: String) = "update/$destinationId"
        }
    }

    data object DashboardScreen : Screen("dashboard/{userId}/{role}") {
        fun createRoute(userId: String, role: String) = "dashboard/$userId/$role"
    }

    data object Main : Screen("main_screen")
}

