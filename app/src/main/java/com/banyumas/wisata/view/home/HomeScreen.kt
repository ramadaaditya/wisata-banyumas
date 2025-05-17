package com.banyumas.wisata.view.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.listDummyDestination
import com.banyumas.wisata.view.components.CategoryRow
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.BanyumasTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun HomeScreen(
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

    HomeScreenContent(
        querySearch = query,
        onQueryChange = { query = it },
        categories = categories,
        onCategorySelected = { selectedCategory = it },
        uiState = uiState,
        navigateToDetail = navigateToDetail,
        onFavoriteClick = onFavoriteClick,
        selectedCategory = selectedCategory
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
    uiState: UiState<List<UiDestination>>
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
            is UiState.Loading -> LoadingState()
            is UiState.Success -> {
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

            is UiState.Error -> {
                Log.e("HomeScreen", "Error loading destinations: ${uiState.message}")
                ErrorState(message = uiState.message)
            }

            UiState.Empty -> EmptyState()
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
            uiState = UiState.Success(listDummyDestination)
        )
    }
}
