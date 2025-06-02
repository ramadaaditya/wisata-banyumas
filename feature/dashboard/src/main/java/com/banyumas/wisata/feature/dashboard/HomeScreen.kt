package com.banyumas.wisata.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.listDummyDestination
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    userId: String,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    destinationViewModel: DestinationViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit,
    onFavoriteClick: (com.banyumas.wisata.core.model.UiDestination) -> Unit
) {
    val uiState by destinationViewModel.uiDestinations.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = remember { com.banyumas.wisata.core.model.Category.list }
    var query by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        destinationViewModel.getAllDestinations(userId)
    }

    HomeScreenContent(
        querySearch = query,
        onQueryChange = { query = it },
        categories = categories,
        onCategorySelected = { selectedCategory = it },
        uiState = uiState,
        navigateToDetail = navigateToDetail,
        onFavoriteClick = onFavoriteClick,
        selectedCategory = selectedCategory,
        modifier = modifier
    )
}

@Composable
internal fun HomeScreenContent(
    modifier: Modifier = Modifier,
    navigateToDetail: (String) -> Unit,
    onFavoriteClick: (com.banyumas.wisata.core.model.UiDestination) -> Unit,
    querySearch: String = "",
    onQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    uiState: com.banyumas.wisata.core.common.UiState<List<com.banyumas.wisata.core.model.UiDestination>>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        com.banyumas.wisata.core.designsystem.components.Search(
            query = querySearch,
            onQueryChange = onQueryChange,
            onSearch = {},
        )
        com.banyumas.wisata.core.designsystem.components.CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )
        when (uiState) {
            is com.banyumas.wisata.core.common.UiState.Loading -> com.banyumas.wisata.core.designsystem.components.LoadingState()
            is com.banyumas.wisata.core.common.UiState.Success -> {
                val destinations = uiState.data.filter {
                    selectedCategory == "All" || it.destination.category == selectedCategory
                }.filter {
                    it.destination.name.contains(querySearch, ignoreCase = true)
                }

                if (destinations.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(destinations) { destination ->
                            com.banyumas.wisata.core.designsystem.components.DestinationCard(
                                destination = destination,
                                onFavoriteClick = { onFavoriteClick(destination) },
                                onClick = { navigateToDetail(destination.destination.id) },
                                onLongPress = {}
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Tidak ada destinasi dalam kategori \"$selectedCategory\".",
                        style = com.banyumas.wisata.core.designsystem.theme.BanyumasTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is com.banyumas.wisata.core.common.UiState.Error -> {
                com.banyumas.wisata.core.designsystem.components.ErrorState(message = uiState.message)
            }

            com.banyumas.wisata.core.common.UiState.Empty -> com.banyumas.wisata.core.designsystem.components.EmptyState()
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun HomeScreenContentPreview() {
    com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme {
        val categories = com.banyumas.wisata.core.model.Category.list
        HomeScreenContent(
            onFavoriteClick = {},
            onQueryChange = {},
            categories = categories,
            selectedCategory = "All",
            onCategorySelected = {},
            querySearch = "",
            navigateToDetail = {},
            uiState = UiState.Success(listDummyDestination)
        )
    }
}
