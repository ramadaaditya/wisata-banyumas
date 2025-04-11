package com.banyumas.wisata.view.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun FavoriteScreen(
    navigateToDetail: (Destination) -> Unit,
    viewmodel: DestinationViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val favoriteState by viewmodel.favoriteDestination.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewmodel.loadFavoriteDestinations(user.id)
        }
    }

    when (favoriteState) {
        is UiState.Loading -> LoadingState()

        is UiState.Success -> {
            val destinations = (favoriteState as UiState.Success<List<Destination>>).data
            if (destinations.isEmpty()) {
                EmptyState()
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

        is UiState.Error -> {
            ErrorState(
                message = (favoriteState as UiState.Error).message,
            )
        }

        UiState.Empty -> EmptyState()
    }
}


@Composable
fun FavoriteContent(
    destinations: List<Destination>,
    navigateToDetail: (Destination) -> Unit,
    onToggleFavorite: (Destination) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        items(destinations) { destination ->
            DestinationCard(
                destination = UiDestination(destination, isFavorite = true),
                onFavoriteClick = { onToggleFavorite(destination) },
                onClick = { navigateToDetail(destination) },
                onLongPress = {}
            )
        }
    }
}