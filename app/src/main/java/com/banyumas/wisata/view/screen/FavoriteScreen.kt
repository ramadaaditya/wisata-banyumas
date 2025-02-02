package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.viewmodel.FavoriteViewModel

@Composable
fun FavoriteScreen(
    userId: String,
    navigateToDetail: (Destination) -> Unit,
    viewmodel: FavoriteViewModel = hiltViewModel()
) {
    val favoriteState by viewmodel.favoriteDestination.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewmodel.loadFavoriteDestinations(userId)
    }

    when (favoriteState) {
        is UiState.Loading -> LoadingState()

        is UiState.Success -> {
            val destinations = (favoriteState as UiState.Success<List<Destination>>).data
            if (destinations.isEmpty()) {
                EmptyState()
            } else {
                FavoriteGrid(
                    destinations = destinations,
                    navigateToDetail = navigateToDetail,
                    onToggleFavorite = { data ->
                        viewmodel.toggleFavorite(
                            userId = userId,
                            destinationId = data.id,
                            isFavorite = false
                        )
                    },
                )
            }
        }

        is UiState.Error -> {
            ErrorState(
                message = (favoriteState as UiState.Error).message,
            )
        }

        UiState.Empty -> EmptyState()
    }
}

@Composable
fun FavoriteGrid(
    destinations: List<Destination>,
    navigateToDetail: (Destination) -> Unit,
    onToggleFavorite: (Destination) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(destinations) { destination ->
            DestinationCard(
                destination = UiDestination(destination, isFavorite = true),
                onFavoriteClick = { onToggleFavorite(destination) },
                onClick = { navigateToDetail(destination) }
            )
        }
    }
}
