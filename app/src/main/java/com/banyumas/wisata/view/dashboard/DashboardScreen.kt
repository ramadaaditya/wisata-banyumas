package com.banyumas.wisata.view.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.banyumas.wisata.model.Category
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.AddIcon
import com.banyumas.wisata.view.components.CategoryRow
import com.banyumas.wisata.view.components.ConfirmationDialog
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.EmptyState
import com.banyumas.wisata.view.components.ErrorState
import com.banyumas.wisata.view.components.LoadingState
import com.banyumas.wisata.view.components.LogoutIcon
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.WisataBanyumasTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    userId: String,
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    navigateToDetail: (String) -> Unit,
    viewModel: DestinationViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiDestinations.collectAsState()
    val authState by userViewModel.authState.collectAsState()
    var selectedDestination by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getAllDestinations(userId)
    }

    when (val state = uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Success -> {
            val destinations = state.data
            if (destinations.isNotEmpty()) {
                DashboardContent(
                    destinations = destinations,
                    navigateToDetail = navigateToDetail,
                    onLongPress = {
                        selectedDestination = it
                        showDeleteDialog = true
                    },
                    onLogoutClick = { showLogoutDialog = true },
                    onSearchQueryChange = { query ->
                        viewModel.searchDestinations(
                            query,
                            null
                        )
                    },
                    onCategorySelected = { category -> viewModel.filterDestinations(category) },
                    onAddClick = onAddClick
                )
            } else {
                EmptyState(message = "Tidak Ada Destinasi yang cocok dengan pencarian")
            }
        }

        is UiState.Error -> ErrorState(message = state.message)
        is UiState.Empty -> {
            DashboardContent(
                destinations = emptyList(),
                navigateToDetail = navigateToDetail,
                onLongPress = {
                    selectedDestination = it
                    showDeleteDialog = true
                },
                onLogoutClick = { showLogoutDialog = true },
                onSearchQueryChange = { query ->
                    viewModel.searchDestinations(
                        query,
                        null
                    )
                },
                onCategorySelected = { category -> viewModel.filterDestinations(category) },
                onAddClick = onAddClick
            )
        }
    }


    if (showLogoutDialog) {
        ConfirmationDialog(
            title = "Konfirmasi Logout",
            message = "Apakah Anda yakin ingin keluar ?",
            onConfirm = {
                userViewModel.logout()
                showLogoutDialog = false
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showDeleteDialog && selectedDestination != null) {
        ConfirmationDialog(
            title = "Konfirmasi Hapus",
            message = "Apakah Anda yakin ingin menghapus destinasi ini ?",
            onConfirm = {
                selectedDestination?.let { viewModel.deleteDestinationById(it) }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}


@Composable
fun DashboardContent(
    destinations: List<UiDestination>,
    navigateToDetail: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onAddClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    val categories = remember { Category.list }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Search(
                query = query,
                onQueryChange = {
                    query = it
                    onSearchQueryChange(it)
                },
                onSearch = {},
                modifier = Modifier.weight(1f)
            )
            AddIcon(onClick = onAddClick)
            LogoutIcon(onClick = onLogoutClick)
        }

        CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory ?: "All",
            onCategorySelected = {
                selectedCategory = it
                onCategorySelected(it)
            }
        )

        if (destinations.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn {
                items(destinations) { destination ->
                    DestinationCard(
                        destination = destination,
                        onFavoriteClick = {},
                        onClick = {
                            navigateToDetail(destination.destination.id)
                        },
                        showFavoriteIcon = false,
                        onLongPress = { onLongPress(destination.destination.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun DashboardContentPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        val sampleDestination = List(5) { index ->
            UiDestination(
                destination = Destination(
                    id = "id_$index",
                    name = "Curug Baturraden $index",
                    address = "Jl. Baturraden, Purwokerto Utara"
                )
            )
        }
        DashboardContent(
            destinations = sampleDestination,
            navigateToDetail = {},
            onLongPress = {},
            onSearchQueryChange = {},
            onCategorySelected = {},
            onLogoutClick = {},
            onAddClick = {}
        )
    }
}