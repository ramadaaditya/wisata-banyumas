package com.banyumas.wisata.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument


sealed class Screen(
    val route: String,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    data object AuthGraph : Screen("auth_graph")

    data object LoginScreen : Screen("login")
    data object RegisterScreen : Screen("register")
    data object ForgotPasswordScreen : Screen("forgot_password")

    data object FavoriteScreen : Screen("favorite")
    data object ProfileScreen : Screen("profile")
    data object FetchScreen : Screen("fetch")
    data object SplashScreen : Screen("splash")

    data class AddReviewScreen(val destinationId: String) : Screen(ROUTE) {
        companion object {
            const val ROUTE = "add_review/{destinationId}"
            fun createRoute(destinationId: String) = "add_review/$destinationId"
        }
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

    data object MainGraph : Screen(
        "main_screen/{userId}/{role}",
        arguments = listOf(
            navArgument("userId") { type = NavType.StringType },
            navArgument("role") { type = NavType.StringType }
        )
    ) {
        fun createRoute(userId: String, role: String) = "main_screen/$userId/$role"
    }

    data object Main : Screen("main_screen")

    // Layar untuk menampilkan detail satu destinasi.
    data object DetailScreen : Screen(
        route = "detail/{destinationId}",
        arguments = listOf(
            navArgument("destinationId") { type = NavType.StringType; nullable = false }
        )
    ) {
        fun createRoute(destinationId: String) = "detail/$destinationId"
    }

    // Layar untuk menambah atau mengedit destinasi.
    // BEST PRACTICE: Menggunakan satu layar untuk dua fungsi (Tambah/Edit)
    // dengan argumen opsional.
    data object ManageDestination : Screen(
        route = "manage_destination?destinationId={destinationId}",
        arguments = listOf(
            navArgument("destinationId") {
                type = NavType.StringType
                nullable = true // destinationId boleh null (untuk mode "Tambah")
                defaultValue = null
            }
        )
    ) {
        // Fungsi untuk navigasi ke mode "Tambah" (tanpa argumen)
        fun createRouteForAdd(): String = "manage_destination"

        // Fungsi untuk navigasi ke mode "Edit" (dengan argumen)
        fun createRouteForEdit(destinationId: String): String =
            "manage_destination?destinationId=$destinationId"
    }
}

