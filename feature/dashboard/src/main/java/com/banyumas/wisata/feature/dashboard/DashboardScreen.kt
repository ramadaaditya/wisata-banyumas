package com.banyumas.wisata.feature.dashboard

import android.content.ContentValues.TAG
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.core.designsystem.components.CategoryRow
import com.banyumas.wisata.core.designsystem.components.ConfirmationDialog
import com.banyumas.wisata.core.designsystem.components.DestinationCard
import com.banyumas.wisata.core.designsystem.components.EmptyState
import com.banyumas.wisata.core.designsystem.components.ErrorState
import com.banyumas.wisata.core.designsystem.components.FlexibleSearchBar
import com.banyumas.wisata.core.designsystem.components.LoadingState
import com.banyumas.wisata.core.designsystem.components.MapsIcon
import com.banyumas.wisata.core.designsystem.components.SimpleSearchBar
import com.banyumas.wisata.core.designsystem.theme.BanyumasTheme
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
    val uiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data

    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
    val searchTextFieldState = rememberTextFieldState()
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    var destinationToDeleteId by rememberSaveable { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    Timber.tag(TAG).d("Status user : $currentUser")
    Timber.tag(TAG).d("Cek data destinasi $uiState")

    LaunchedEffect(currentUser) {
        currentUser?.id?.let {
            dashboardViewModel.setUserId(it)

        }
    }

    LaunchedEffect(Unit) {
        dashboardViewModel.eventFlow.collect { event ->
            when (event) {
                is DashboardViewModel.DestinationEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(message = event.message.asString(context))
                }

                is DashboardViewModel.DestinationEvent.DeletionSuccess -> {
                    snackbarHostState.showSnackbar(message = "Destinasi berhasil dihapus.")
                }
            }
        }
    }


    DashboardContent(
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { focusManager.clearFocus() })
        },
        uiState = uiState,
        userRole = currentUser?.role ?: Role.USER,
        onSearch = { query ->
            dashboardViewModel.searchDestinations(query)
        },
        navigateToDetail = onDestinationClick,
        onLongPress = { destinationId ->
            if (currentUser?.role == Role.ADMIN) {
                destinationToDeleteId = destinationId
            }
        },
        onLogoutClick = { showLogoutDialog = true },
        onCategorySelected = { category ->
            selectedCategory = category
            dashboardViewModel.selectCategory(category)
        },
        onFavoriteClick = { destination ->
            dashboardViewModel.toggleFavorite(
                destination.destination.id,
                destination.isFavorite
            )
        },
        onAddClick = navigateToAddDestination,
        selectedCategory = selectedCategory,
        categories = Category.list,
        searchTextFieldState = searchTextFieldState,
        query = query,
        active = active
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
    searchTextFieldState: TextFieldState,
    onFavoriteClick: (UiDestination) -> Unit,
    navigateToDetail: (String) -> Unit,
    onLongPress: (String) -> Unit,
    onLogoutClick: () -> Unit,
    onAddClick: () -> Unit,
    query: String,
    active: Boolean,
    onCategorySelected: (String) -> Unit,
    onSearch: (String) -> Unit,
    selectedCategory: String,
    categories: List<String>,
) {
    val searchResults by remember(uiState) {
        derivedStateOf {
            val searchText = searchTextFieldState.text.toString()
            if (searchText.isBlank() || uiState !is UiState.Success) {
                emptyList()
            } else {
                uiState.data
                    .map { it.destination.name }
                    .filter { it.contains(searchText, ignoreCase = true) }
                    .distinct()
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            MapsIcon(onClick = {})
            Column {
                Text(text = "Location", style = BanyumasTheme.typography.titleLarge)
                Text(text = "Banyumas", style = BanyumasTheme.typography.titleSmall)
            }
        }


//        SimpleSearchBar(
//            textFieldState = searchTextFieldState,
//            searchResults = searchResults,
//            onSearch = onSearch,
//            // KOREKSI 5: Pindahkan ikon-ikon ke dalam SearchBar
////            trailingIcon = {
////                Row {
////                    if (userRole == Role.ADMIN) {
////                        AddIcon(onClick = onAddClick)
////                    }
////                    LogoutIcon(onClick = onLogoutClick)
////                }
////            }
//        )

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

@Preview(showBackground = true, device = Devices.PIXEL, name = "Admin Role", showSystemUi = true)
@Composable
fun DashboardContentPreview() {
    val searchTextFieldState = rememberTextFieldState()

    WisataBanyumasTheme(dynamicColor = false) {
        // Bungkus dengan Scaffold untuk mensimulasikan UI nyata
        Scaffold(
            bottomBar = { /* BottomBar tiruan untuk menghasilkan innerPadding */ }
        ) { innerPadding ->
            DashboardContent(
                modifier = Modifier.padding(innerPadding), // Terapkan innerPadding
                uiState = UiState.Success(generateDummyDestinations()),
                userRole = Role.ADMIN,
                searchTextFieldState = searchTextFieldState,
                onSearch = {},
                selectedCategory = "Semua",
                categories = Category.list,
                onCategorySelected = {},
                onFavoriteClick = {},
                onAddClick = {},
                onLogoutClick = {},
                onLongPress = {},
                navigateToDetail = {},
                query = "",
                active = false
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4, name = "User Role")
@Composable
private fun DashboardContentUserPreview() {
    var selectedCategory by remember { mutableStateOf("Kuliner") }
    val searchTextFieldState = rememberTextFieldState("Baturraden")

    WisataBanyumasTheme(dynamicColor = false) {
        Scaffold(
            bottomBar = { /* BottomBar tiruan */ }
        ) { innerPadding ->
            DashboardContent(
                modifier = Modifier.padding(innerPadding),
                uiState = UiState.Success(generateDummyDestinations(true)),
                userRole = Role.USER,
                searchTextFieldState = searchTextFieldState,
                onSearch = {},
                selectedCategory = selectedCategory,
                categories = Category.list,
                onCategorySelected = { newCategory ->
                    selectedCategory = newCategory
                },
                onFavoriteClick = {},
                onAddClick = {},
                onLogoutClick = {},
                onLongPress = {},
                navigateToDetail = {},
                query = "",
                active = false
            )
        }
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
private fun DashboardContentEmptyPreview() {
    val searchTextFieldState = rememberTextFieldState()

    WisataBanyumasTheme(dynamicColor = false) {
        Scaffold(
            bottomBar = { /* BottomBar tiruan */ }
        ) { innerPadding ->
            DashboardContent(
                modifier = Modifier.padding(innerPadding),
                uiState = UiState.Empty,
                userRole = Role.USER,
                searchTextFieldState = searchTextFieldState,
                onSearch = {},
                selectedCategory = "Semua",
                categories = Category.list,
                onCategorySelected = {},
                onFavoriteClick = {},
                onAddClick = {},
                onLogoutClick = {},
                onLongPress = {},
                navigateToDetail = {},
                query = "",
                active = false
            )
        }
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
private fun DashboardContentLoadingPreview() {
    val searchTextFieldState = rememberTextFieldState()

    WisataBanyumasTheme(dynamicColor = false) {
        Scaffold(
            bottomBar = { /* BottomBar tiruan */ }
        ) { innerPadding ->
            DashboardContent(
                modifier = Modifier.padding(innerPadding),
                uiState = UiState.Loading,
                userRole = Role.USER,
                searchTextFieldState = searchTextFieldState,
                onSearch = {},
                selectedCategory = "Semua",
                categories = Category.list,
                onCategorySelected = {},
                onFavoriteClick = {},
                onAddClick = {},
                onLogoutClick = {},
                onLongPress = {},
                navigateToDetail = {},
                query = "",
                active = false
            )
        }
    }
}

// ... fungsi generateDummyDestinations tidak berubah
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
