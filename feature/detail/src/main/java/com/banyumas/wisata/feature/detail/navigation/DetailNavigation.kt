package com.banyumas.wisata.feature.detail.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.banyumas.wisata.feature.detail.DetailRoute
import kotlinx.serialization.Serializable

// 1. Rute Navigasi dengan argumen
@Serializable
data class DetailDestination(val destinationId: String)

// 2. Fungsi pembantu untuk navigasi yang mudah
fun NavController.navigateToDetail(destinationId: String, navOptions: NavOptions? = null) {
    this.navigate(DetailDestination(destinationId = destinationId), navOptions)
}

// 3. Pintu masuk untuk NavHost (Sang Arsitek akan memanggil ini)
fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit, // Kirim ID saja, bukan seluruh objek
) {
    composable<DetailDestination> {
        // Composable ini akan mengelola state (Stateful)
        DetailRoute(
            onBackClick = onBackClick,
            onEditClick = onEditClick,
        )
    }
}