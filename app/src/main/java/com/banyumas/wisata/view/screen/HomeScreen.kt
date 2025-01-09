package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.DestinationItem
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.viewmodel.HomeViewModel
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToDetail: (String) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.destinations.collectAsState()
    var query by remember { mutableStateOf("") }
    val nearbyState by viewModel.nearbyDestinations.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Search(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        )
        {
            item {
                Text(
                    text = "Rekomendasi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .align(Alignment.Start)
                )
            }

            when (uiState) {
                is UiState.Loading -> {
                    item {
                        LoadingState(modifier = Modifier.fillMaxSize())
                    }
                }

                is UiState.Success -> {
                    val destinations = (uiState as UiState.Success<List<UiDestination>>).data
                    if (destinations.isNotEmpty()) {
                        item {
                            DestinationRow(
                                destination = destinations,
                                navigateToDetail = { destination ->
                                    navigateToDetail(destination)
                                },
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No destinations found",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        ErrorState(
                            message = (uiState as UiState.Error).message,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Destinasi Terdekat",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.Start)
                )
            }


            when (uiState) {
                is UiState.Loading -> {
                    item {
                        LoadingState(modifier = Modifier.fillMaxSize())
                    }
                }

                is UiState.Success -> {
                    val destinations = (uiState as UiState.Success<List<UiDestination>>).data
                    if (destinations.isNotEmpty()) {
                        item {
                            DestinationRow(
                                destination = destinations,
                                navigateToDetail = { destination ->
                                    navigateToDetail(destination)
                                },
                            )
                        }
                    } else {
                        item {
                            Text(
                                text = "No destinations found",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        ErrorState(
                            message = (uiState as UiState.Error).message,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DestinationRow(
    destination: List<UiDestination>,
    navigateToDetail: (String) -> Unit,
) {
    LazyRow {
        items(destination) { destination ->
            DestinationItem(
                destination = destination,
                onFavoriteClick = {},
                onClick = {
                    navigateToDetail(destination.destination.id)
                },
            )
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    WisataBanyumasTheme {
        HomeScreen(
            navigateToDetail = {},
        )
    }
}