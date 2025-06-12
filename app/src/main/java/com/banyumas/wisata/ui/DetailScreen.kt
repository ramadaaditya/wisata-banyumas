@file:OptIn(ExperimentalFoundationApi::class)

package com.banyumas.wisata.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.designsystem.components.EditIcon
import com.banyumas.wisata.core.designsystem.components.EmptyState
import com.banyumas.wisata.core.designsystem.components.ErrorState
import com.banyumas.wisata.core.designsystem.components.FavoriteIcon
import com.banyumas.wisata.core.designsystem.components.LoadingState
import com.banyumas.wisata.core.designsystem.components.PhotoCarouselViewer
import com.banyumas.wisata.core.designsystem.components.ReviewCard
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.Photo
import com.banyumas.wisata.core.model.Review
import com.banyumas.wisata.core.model.Role
import com.banyumas.wisata.core.model.UiDestination
import com.banyumas.wisata.feature.auth.UserViewModel
import com.banyumas.wisata.utils.openGoogleMaps
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(
    viewModel: DetailViewModel = hiltViewModel(),
    userViewModel: UserViewModel,
    onBackClick: () -> Unit,
    onEditClick: (Destination) -> Unit,
) {
    val uiState by viewModel.destinationState.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        currentUser?.id?.let { viewModel.loadDestinationDetail(it) }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DetailViewModel.DetailScreenEvent.ShowMessage -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(event.message.asString(context))
                    }
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (val state = uiState) {
                    is UiState.Loading -> LoadingState()
                    is UiState.Success -> {
                        val destination = state.data
                        DetailContent(
                            destination = destination,
                            isAdmin = currentUser?.role == Role.ADMIN,
                            onMapClick = { lat, long -> openGoogleMaps(context, lat, long) },
                            onFavoriteClick = { currentUser?.id?.let { viewModel.toggleFavorite(it) } },
                            onEditClick = { onEditClick(destination.destination) }
                        )
                    }

                    is UiState.Error -> ErrorState(message = state.message)
                    is UiState.Empty -> EmptyState(message = "Destinasi tidak ditemukan.")
                }
                // Tombol back selalu ada di atas konten
                IconButton(onClick = onBackClick, modifier = Modifier.padding(8.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

@Composable
fun DetailContent(
    destination: UiDestination,
    isAdmin: Boolean,
    onMapClick: (Double, Double) -> Unit,
    onFavoriteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = destination.destination.photos.firstOrNull()?.photoUrl,
                    contentDescription = "Image of ${destination.destination.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    if (isAdmin) {
                        EditIcon(onClick = onEditClick)
                    } else {
                        FavoriteIcon(onClick = onFavoriteClick, isFavorite = destination.isFavorite)
                    }
                }
            }
        }

        // --- Bagian 2: Info Utama (Nama, Rating, Alamat) ---
        item {
            DetailInfoSection(
                name = destination.destination.name,
                rating = destination.destination.rating,
                reviewCount = destination.destination.reviews.size,
                address = destination.destination.address,
                modifier = Modifier.padding(16.dp)
            )
        }

        // --- Bagian 3: Carousel Foto ---
        if (destination.destination.photos.isNotEmpty()) {
            item {
                PhotoCarouselViewer(
                    photos = destination.destination.photos,
                    onRemovePhoto = {},
                    showRemoveIcon = false
                )
            }
        }

        // --- Bagian 4: Header "Ulasan" yang menempel (sticky) ---
        stickyHeader {
            Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 2.dp) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Ulasan",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    if (!isAdmin) {
                        TextButton(onClick = { /* TODO: Buka layar tambah ulasan */ }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Tambah Ulasan",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Tambah")
                        }
                    }
                }
            }
        }

        // --- Bagian 5: Daftar Ulasan ---
        items(destination.destination.reviews) { review ->
            ReviewCard(
                review = review,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // --- Bagian 6: Tombol Navigasi Peta (jika ada koordinat) ---
        if (destination.destination.latitude != null && destination.destination.longitude != null) {
            item {
                ExtendedFloatingActionButton(
                    text = { Text("Navigasi ke Lokasi") },
                    icon = { Icon(Icons.Default.Map, contentDescription = null) },
                    onClick = {
                        onMapClick(
                            destination.destination.latitude!!,
                            destination.destination.longitude!!
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}


@Composable
fun DetailInfoSection(
    name: String,
    rating: Float,
    reviewCount: Int,
    address: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Default.Star,
                contentDescription = "Rating",
                tint = Color.Yellow,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(4.dp))
            Text("$rating ($reviewCount ulasan)", style = MaterialTheme.typography.bodyMedium)
        }
        Text(
            text = address,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4)
@Composable
private fun DetailContentPreview() {
    WisataBanyumasTheme {
        DetailContent(
            destination = UiDestination(
                destination = Destination(
                    name = "Curug Baturraden",
                    address = "Jl. Baturraden, Purwokerto Utara, Kabupaten Banyumas",
                    reviews = List(3) {
                        Review(
                            authorName = "Pengunjung ${it + 1}",
                            rating = (5 - it),
                            text = "Tempatnya bagus dan sejuk, cocok untuk liburan keluarga."
                        )
                    },
                    rating = 4.5f,
                    photos = List(5) { Photo("https://placehold.co/600x400?text=Foto+${it + 1}") },
                    latitude = -7.318,
                    longitude = 109.226
                ),
                isFavorite = true
            ),
            isAdmin = false,
            onMapClick = { _, _ -> },
            onFavoriteClick = {},
            onEditClick = {}
        )
    }
}