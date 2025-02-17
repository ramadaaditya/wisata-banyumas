package com.banyumas.wisata.view.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.model.User
import com.banyumas.wisata.view.components.CategoryRow
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel
import de.drick.compose.edgetoedgepreviewlib.CameraCutoutMode
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import de.drick.compose.edgetoedgepreviewlib.NavigationMode

@Composable
fun rememberHomeScreenState(
    viewModel: DestinationViewModel,
    userViewModel: UserViewModel
): HomeScreenState {
    val uiState by viewModel.uiDestinations.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = remember { listOf("All", "Alam", "Religi", "Sejarah", "Kuliner", "Keluarga") }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            viewModel.loadDestinations(user.id)
        }
    }

    return HomeScreenState(
        uiState = uiState,
        query = query,
        selectedCategory = selectedCategory,
        onQueryChange = {
            query = it
            viewModel.searchDestinations(it, selectedCategory)
        },
        onCategorySelected = { category ->
            selectedCategory = category
            viewModel.filterDestinations(category)
        },
        toggleFavorite = { uiDestination ->
            currentUser?.let {
                viewModel.toggleFavorite(
                    userId = currentUser.id,
                    destinationId = uiDestination.destination.id,
                    isFavorite = !uiDestination.isFavorite
                )
            }
        },
        categories = categories,
        currentUser = currentUser
    )
}

data class HomeScreenState(
    val uiState: UiState<List<UiDestination>>,
    val query: String,
    val onQueryChange: (String) -> Unit,
    val toggleFavorite: (UiDestination) -> Unit,
    val selectedCategory: String?,
    val onCategorySelected: (String) -> Unit,
    val categories: List<String>,
    val currentUser: User?
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToDetail: (String) -> Unit,
    userViewModel: UserViewModel,
    destinationViewModel: DestinationViewModel,
) {
    val state =
        rememberHomeScreenState(viewModel = destinationViewModel, userViewModel = userViewModel)

    HomeScreenContent(
        modifier = modifier,
        query = state.query,
        onQueryChange = state.onQueryChange,
        uiState = state.uiState,
        navigateToDetail = navigateToDetail,
        onFavoriteClick = state.toggleFavorite,
        onCategorySelected = state.onCategorySelected,
        selectedCategory = state.selectedCategory,
        categories = state.categories,
        currentUser = state.currentUser
    )
}

@Composable
fun HomeScreenContent(
    categories: List<String>,
    modifier: Modifier = Modifier,
    query: String,
    selectedCategory: String?,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    uiState: UiState<List<UiDestination>>,
    navigateToDetail: (String) -> Unit,
    onFavoriteClick: (UiDestination) -> Unit,
    currentUser: User?
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Hai, ${currentUser?.name ?: "Pengguna"}!",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = "Temukan Keindahan Alam Banyumas!",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge
        )
        Search(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = {},
            modifier = Modifier
                .fillMaxWidth()
        )
        CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory ?: "All",
            onCategorySelected = onCategorySelected,
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (uiState) {
                is UiState.Loading -> item { LoadingState() }

                is UiState.Success -> {
                    val destinations = uiState.data
                    if (destinations.isNotEmpty()) {
                        items(destinations) { destination ->
                            Column {
                                DestinationCard(
                                    destination = destination,
                                    onFavoriteClick = { onFavoriteClick(destination) },
                                    onClick = { navigateToDetail(destination.destination.id) },
                                    onLongPress = {}
                                )
                            }
                        }
                    } else {
                        item {
                            Text(
                                text = "Tidak ada destinasi dalam kategori \"$selectedCategory\".",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        val errorMessage = uiState.message
                        Log.e("HomeScreen", "Error loading destinations: $errorMessage")
                        ErrorState(message = errorMessage)
                    }
                }

                UiState.Empty -> {
                    item { EmptyState() }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    AppTheme {
        val sampleDestinations = List(5) { index ->
            UiDestination(
                destination = Destination(
                    id = "id_$index",
                    name = "Pantai Parangtritis $index",
                    address = "Jl. Parangtritis, Yogyakarta",
                ),
                isFavorite = index % 2 == 0
            )
        }
        val uiState = UiState.Success(sampleDestinations)
        EdgeToEdgeTemplate(
            navMode = NavigationMode.ThreeButton,
            cameraCutoutMode = CameraCutoutMode.Middle,
            showInsetsBorder = true,
            isStatusBarVisible = true,
            isNavigationBarVisible = true,
            isInvertedOrientation = false
        )
        {
            HomeScreenContent(
                modifier = Modifier.fillMaxSize(),
                query = "",
                selectedCategory = null,
                onQueryChange = {},
                onCategorySelected = {},
                uiState = uiState,
                navigateToDetail = {},
                onFavoriteClick = {},
                currentUser = User(
                    name = "Ramados"
                ),
                categories = listOf("All", "Alam", "Religi", "Sejarah", "Kuliner", "Keluarga")
            )

        }
    }
}
