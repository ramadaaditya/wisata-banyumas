package com.banyumas.wisata.view.home

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
import com.banyumas.wisata.model.Category
import com.banyumas.wisata.model.UiDestination
import com.wisata.banyumas.common.UiState
import com.wisata.banyumas.common.listDummyDestination
import com.banyumas.wisata.view.components.CategoryRow
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.EmptyState
import com.banyumas.wisata.view.components.ErrorState
import com.banyumas.wisata.view.components.LoadingState
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.BanyumasTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun HomeScreen(
    userId: String,
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel = hiltViewModel(),
    destinationViewModel: DestinationViewModel = hiltViewModel(),
    navigateToDetail: (String) -> Unit,
    onFavoriteClick: (UiDestination) -> Unit
) {
    val uiState by destinationViewModel.uiDestinations.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = remember { Category.list }
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
    onFavoriteClick: (UiDestination) -> Unit,
    querySearch: String = "",
    onQueryChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    uiState: com.wisata.banyumas.common.UiState<List<UiDestination>>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Search(
            query = querySearch,
            onQueryChange = onQueryChange,
            onSearch = {},
        )
        CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )
        when (uiState) {
            is com.wisata.banyumas.common.UiState.Loading -> LoadingState()
            is com.wisata.banyumas.common.UiState.Success -> {
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
                            DestinationCard(
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
                        style = BanyumasTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is com.wisata.banyumas.common.UiState.Error -> {
                ErrorState(message = uiState.message)
            }

            com.wisata.banyumas.common.UiState.Empty -> EmptyState()
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun HomeScreenContentPreview() {
    WisataBanyumasTheme {
        val categories = Category.list
        HomeScreenContent(
            onFavoriteClick = {},
            onQueryChange = {},
            categories = categories,
            selectedCategory = "All",
            onCategorySelected = {},
            querySearch = "",
            navigateToDetail = {},
            uiState = com.wisata.banyumas.common.UiState.Success(com.wisata.banyumas.common.listDummyDestination)
        )
    }
}
