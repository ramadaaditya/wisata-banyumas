package com.banyumas.wisata.view.navigation


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

    data class DetailScreen(val destinationId: String) :
        Screen(DETAIL_ROUTE) {
        companion object {
            const val DETAIL_ROUTE = "detail/{destinationId}"
            fun createRoute(destinationId: String) =
                "detail/$destinationId"
        }
    }

    data class UpdateScreen(val destinationId: String) : Screen(UPDATE_ROUTE) {
        companion object {
            const val UPDATE_ROUTE = "update/{destinationId}"
            fun createRoute(destinationId: String) = "update/$destinationId"
        }
    }

    data class HomeScreen(val userId: String) : Screen(HOME_ROUTE) {
        companion object {
            const val HOME_ROUTE = "home/{userId}"
            fun createRoute(userId: String) = "home/$userId"
        }
    }

    data class DashboardScreen(val userId: String) : Screen(DASHBOARD_ROUTE) {
        companion object {
            const val DASHBOARD_ROUTE = "dashboard/{userId}"
            fun createRoute(userId: String) = "dashboard/$userId"
        }
    }
}
