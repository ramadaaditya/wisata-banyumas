package com.banyumas.wisata.feature.dashboard

import android.content.ContentValues.TAG
import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.core.designsystem.components.AddIcon
import com.banyumas.wisata.core.designsystem.components.AppSearchBar
import com.banyumas.wisata.core.designsystem.components.CategoryRow
import com.banyumas.wisata.core.designsystem.components.ConfirmationDialog
import com.banyumas.wisata.core.designsystem.components.DestinationCard
import com.banyumas.wisata.core.designsystem.components.EmptyState
import com.banyumas.wisata.core.designsystem.components.ErrorState
import com.banyumas.wisata.core.designsystem.components.LoadingState
import com.banyumas.wisata.core.designsystem.components.LogoutIcon
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.Category
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.Role
import com.banyumas.wisata.core.model.UiDestination
import timber.log.Timber

@Composable
fun DashboardScreen(
    navigateToAddDestination: () -> Unit,
    onDestinationClick: (String) -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiDestinations.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data
    val userRole = currentUser?.role ?: Role.USER
    val userId = currentUser?.id
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var destinationToDeleteId by rememberSaveable { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current

    Log.d(TAG, "DashboardScreen: $currentUser")

    DashboardContent(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        uiState = uiState,
        userRole = userRole,
        query = query,
        navigateToDetail = onDestinationClick,
        onLongPress = { destinationId ->
            if (userRole == Role.ADMIN) {
                destinationToDeleteId = destinationId
            }
        },
        onLogoutClick = { showLogoutDialog = true },
        onSearchQueryChange = {
            query = it
            dashboardViewModel.searchDestinations(it, selectedCategory)
        },
        onCategorySelected = { category ->
            selectedCategory = category
            dashboardViewModel.searchDestinations(query, category)
        },
        onFavoriteClick = { destination ->
            userId?.let {
                dashboardViewModel.toggleFavorite(
                    userId = it,
                    destination.destination.id,
                    destination.isFavorite
                )
            }
        },
        onAddClick = navigateToAddDestination,
        selectedCategory = selectedCategory,
        categories = Category.list
    )


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

    if (destinationToDeleteId != null) {
        ConfirmationDialog(
            title = "Konfirmasi Hapus",
            message = "Apakah Anda yakin ingin menghapus destinasi ini ?",
            onConfirm = {
                destinationToDeleteId?.let { dashboardViewModel.deleteDestinationById(it) }
                destinationToDeleteId = null
            },
            onDismiss = { destinationToDeleteId = null }
        )
    }
}


@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    uiState: UiState<List<UiDestination>>,
    userRole: Role,
    onFavoriteClick: (UiDestination) -> Unit,
    navigateToDetail: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onAddClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    query: String,
    selectedCategory: String,
    categories: List<String>,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AppSearchBar(
                query = query,
                onQueryChange = onSearchQueryChange,
                onSearch = {},
                modifier = Modifier.weight(1f)
            )
            if (userRole == Role.ADMIN) {
                AddIcon(onClick = onAddClick)
            }
            LogoutIcon(onClick = onLogoutClick)
        }

        CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
        )

        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(uiState.data, key = { it.destination.id }) { destination ->
                        DestinationCard(
                            destination = destination,
                            onFavoriteClick = { onFavoriteClick(destination) },
                            onClick = { navigateToDetail(destination.destination.id) },
                            showFavoriteIcon = userRole == Role.USER,
                            onLongPress = { onLongPress(destination.destination.id) }
                        )
                    }
                }
            }

            is UiState.Error -> ErrorState(message = uiState.message)
            is UiState.Empty -> EmptyState(message = "Tidak ada destinasi yang ditemukan")
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL, name = "Admin Role")
@Composable
fun DashboardContentPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        DashboardContent(
            uiState = UiState.Success(generateDummyDestinations()),
            userRole = Role.ADMIN, // <-- Lihat sebagai Admin
            query = "",
            selectedCategory = "Semua",
            categories = Category.list,
            onSearchQueryChange = {},
            onCategorySelected = {},
            onFavoriteClick = {},
            onAddClick = {},
            onLogoutClick = {},
            onLongPress = {},
            navigateToDetail = {}
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4, name = "User Role")
@Composable
private fun DashboardContentUserPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        DashboardContent(
            uiState = UiState.Success(generateDummyDestinations(true)), // <-- User punya favorit
            userRole = Role.USER, // <-- Lihat sebagai User
            query = "Baturraden",
            selectedCategory = "Alam",
            categories = Category.list,
            onSearchQueryChange = {},
            onCategorySelected = {},
            onFavoriteClick = {},
            onAddClick = {},
            onLogoutClick = {},
            onLongPress = {},
            navigateToDetail = {}
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun DashboardContentEmptyPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        DashboardContent(
            uiState = UiState.Empty, // <-- Kondisi kosong
            userRole = Role.USER,
            query = "",
            selectedCategory = "Semua",
            categories = Category.list,
            onSearchQueryChange = {},
            onCategorySelected = {},
            onFavoriteClick = {},
            onAddClick = {},
            onLogoutClick = {},
            onLongPress = {},
            navigateToDetail = {}
        )
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun DashboardContentLoadingPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        DashboardContent(
            uiState = UiState.Loading,
            userRole = Role.USER,
            query = "",
            selectedCategory = "Semua",
            categories = Category.list,
            onSearchQueryChange = {},
            onCategorySelected = {},
            onFavoriteClick = {},
            onAddClick = {},
            onLogoutClick = {},
            onLongPress = {},
            navigateToDetail = {}
        )
    }
}

private fun generateDummyDestinations(isUser: Boolean = false): List<UiDestination> {
    return List(5) { index ->
        UiDestination(
            destination = Destination(
                id = "id_$index",
                name = "Curug Baturraden $index",
                address = "Jl. Baturraden, Purwokerto Utara",
                category = if (index % 2 == 0) "Alam" else "Kuliner"
            ),
            isFavorite = isUser && (index % 2 == 0)
        )
    }
}
