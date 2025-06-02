package com.banyumas.wisata.feature.bookmarks

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun FavoriteScreen(
    navigateToDetail: (String) -> Unit,
    viewmodel: DestinationViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val favoriteState by viewmodel.favoriteDestination.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? com.banyumas.wisata.core.common.UiState.Success)?.data

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewmodel.loadFavoriteDestinations(user.id)
        }
    }

    when (favoriteState) {
        is com.banyumas.wisata.core.common.UiState.Loading -> com.banyumas.wisata.core.designsystem.components.LoadingState()

        is com.banyumas.wisata.core.common.UiState.Success -> {
            val destinations = (favoriteState as com.banyumas.wisata.core.common.UiState.Success<List<com.banyumas.wisata.core.model.Destination>>).data
            if (destinations.isEmpty()) {
                com.banyumas.wisata.core.designsystem.components.EmptyState()
            } else {
                FavoriteContent(
                    destinations = destinations,
                    navigateToDetail = navigateToDetail,
                    onToggleFavorite = { data ->
                        currentUser?.let {
                            viewmodel.toggleFavorite(
                                userId = currentUser.id,
                                destinationId = data.id,
                                isFavorite = false
                            )
                        }
                    },
                )
            }
        }

        is com.banyumas.wisata.core.common.UiState.Error -> {
            com.banyumas.wisata.core.designsystem.components.ErrorState(
                message = (favoriteState as com.banyumas.wisata.core.common.UiState.Error).message,
            )
        }

        com.banyumas.wisata.core.common.UiState.Empty -> com.banyumas.wisata.core.designsystem.components.EmptyState()
    }
}


@Composable
fun FavoriteContent(
    destinations: List<com.banyumas.wisata.core.model.Destination>,
    navigateToDetail: (String) -> Unit,
    onToggleFavorite: (com.banyumas.wisata.core.model.Destination) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        items(destinations) { destination ->
            com.banyumas.wisata.core.designsystem.components.DestinationCard(
                destination = com.banyumas.wisata.core.model.UiDestination(
                    destination,
                    isFavorite = true
                ),
                onFavoriteClick = { onToggleFavorite(destination) },
                onClick = { navigateToDetail(destination.id) },
                onLongPress = {}
            )
        }
    }
}