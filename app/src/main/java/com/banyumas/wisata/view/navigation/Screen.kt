package com.banyumas.wisata.view.navigation

sealed class Screen(val route: String) {
    data object SplashScreen : Screen("splash")
    data object LoginScreen : Screen("login")
    data object RegisterScreen : Screen("register")
    data object ForgotPasswordScreen : Screen("forgot-password")
    data object AddScreen : Screen("add")

    data class Home(val userId: String) : Screen("home/{userId}") {
        companion object {
            const val ROUTE = "home/{userId}"
            fun createRoute(userId: String) = "home/$userId"
        }
    }

    data class DashboardScreen(val userId: String) : Screen("dashboard/{userId") {
        companion object {
            const val ROUTE = "dashboard/{userId}"
            fun createRoute(userId: String) = "dashboard/$userId"
        }
    }

    data object FavoriteScreen : Screen("favorite")
    data object ProfileScreen : Screen("profile")

    data class DetailScreen(val destinationId: String, val userId: String) :
        Screen("detail/{destinationId}/{userId}") {
        companion object {
            const val ROUTE = "detail/{destinationId}/{userId}"
            fun createRoute(destinationId: String, userId: String) =
                "detail/$destinationId/$userId"
        }
    }

    data class UpdateScreen(val destinationId: String) : Screen("update/{destinationId}") {
        companion object {
            const val ROUTE = "update/{destinationId}"
            fun createRoute(destinationId: String) = "update/$destinationId"
        }
    }
}
