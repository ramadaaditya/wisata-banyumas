package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel

@Composable
fun HomeScreen(
    userId: String,
    modifier: Modifier = Modifier,
    navigateToDetail: (String) -> Unit,
    viewModel: DestinationViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiDestinations.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        Log.d("HomeScreen", "HomeScreen created with userId: $userId")
        if (userId.isNotBlank()) {
            Log.d("HomeScreen", "Fetching destinations for userId: $userId")
            viewModel.loadDestinations(userId)
        }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        Search(
            query = query,
            onQueryChange = {
                query = it
                viewModel.filterDestinations(it)
            },
            onSearch = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (uiState) {
            is UiState.Loading -> {
                Log.d("HomeScreen", "Loading destinations...")
                LoadingState()
            }

            is UiState.Success -> {
                val destinations = (uiState as UiState.Success<List<UiDestination>>).data

                if (destinations.isNotEmpty()) {
                    Log.d("HomeScreen", "Displaying ${destinations.size} destinations")
                    DestinationContent(
                        destinations = destinations,
                        navigateToDetail = navigateToDetail,
                        onFavoriteClick = { destination ->
                            viewModel.toggleFavorite(
                                userId,
                                destination.destination.id,
                                !destination.isFavorite
                            )
                        }
                    )
                } else {
                    Log.d("HomeScreen", "No destinations found")
                    Text(
                        text = "Tidak ada destinasi yang cocok dengan pencarian Anda.",
                        style = AppTheme.typography.body,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is UiState.Error -> {
                val errorMessage = (uiState as UiState.Error).message
                Log.e("HomeScreen", "Error loading destinations: $errorMessage")
                ErrorState(message = errorMessage)
            }

            UiState.Empty -> {
                Log.d("HomeScreen", "Destinations list is empty")
                EmptyState()
            }
        }
    }
}

@Composable
fun DestinationContent(
    destinations: List<UiDestination>,
    navigateToDetail: (String) -> Unit,
    onFavoriteClick: (UiDestination) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        item {
            Text(
                text = "Rekomendasi Wisata",
                style = AppTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        // List destinasi
        items(destinations) { destination ->
            DestinationCard(
                destination = destination,
                onFavoriteClick = { onFavoriteClick(destination) },
                onClick = { navigateToDetail(destination.destination.id) }
            )
        }
    }
}