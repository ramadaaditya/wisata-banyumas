package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.data.model.User
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun DashboardScreen(
    onLogout: () -> Unit,
    navigateToDetail: (String) -> Unit,
    viewModel: DestinationViewModel = hiltViewModel(),
    innerPadding: PaddingValues,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiDestinations.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var selectedDestinationId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        Log.d("DashboardScreen", "AuthState berubah: $authState")

        when (authState) {
            is UiState.Success -> {
                val currentUser = (authState as UiState.Success<User>).data
                Log.d("DashboardScreen", "User ditemukan dengan user id : ${currentUser.id}")
                viewModel.loadDestinations(currentUser.id)
            }
            is UiState.Error -> {
                Log.e("DashboardScreen", "Gagal mengambil user: ${(authState as UiState.Error).message}")
            }
            is UiState.Empty -> {
                Log.w("DashboardScreen", "AuthState kosong, menunggu update...")
                onLogout()
            }
            else -> { }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            Search(
                query = query,
                onQueryChange = {
                    query = it
                    viewModel.filterDestinations(it)
                },
                onSearch = {},
                modifier = Modifier
                    .weight(1f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            IconButton(onClick = { showLogoutDialog = true }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = AppTheme.colorScheme.primary
                )
            }
        }

        when (uiState) {
            is UiState.Loading -> LoadingState()
            is UiState.Success -> {
                val destinations = (uiState as UiState.Success<List<UiDestination>>).data
                if (destinations.isNotEmpty()) {
                    DashboardContent(
                        destinations = destinations,
                        navigateToDetail = navigateToDetail,
                        onLongPress = { destinationId ->
                            selectedDestinationId = destinationId
                            showDeleteDialog = true
                        }
                    )
                } else {
                    Text(
                        text = "Tidak ada destinasi yang cocok dengan pencarian Anda.",
                        style = AppTheme.typography.body,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            is UiState.Error -> ErrorState(message = (uiState as UiState.Error).message)
            UiState.Empty -> EmptyState()
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(onClick = {
                    userViewModel.logout()
                    showLogoutDialog = false
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showDeleteDialog && selectedDestinationId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Konfirmasi Hapus") },
            text = { Text("Apakah Anda yakin ingin menghapus destinasi ini?") },
            confirmButton = {
                TextButton(onClick = {
                    selectedDestinationId?.let { viewModel.deleteDestination(it) }
                    showDeleteDialog = false
                }) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}


@Composable
fun DashboardContent(
    destinations: List<UiDestination>,
    navigateToDetail: (String) -> Unit,
    onLongPress: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "List Wisata",
                style = AppTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

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