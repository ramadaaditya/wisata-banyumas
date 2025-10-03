package com.banyumas.wisata.feature.detail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.data.viewModel.UserViewModel
import com.banyumas.wisata.core.designsystem.R
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
import com.banyumas.wisata.feature.detail.utils.openGoogleMaps
import timber.log.Timber

@Composable
fun DetailRouteScreen(
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    detailViewModel: DetailViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    val detailUiState by detailViewModel.destinationState.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data
    val isAdmin = currentUser?.role == Role.ADMIN
    val currentUserId = currentUser?.id

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(currentUser?.id) {
        if (currentUser?.id != null) {
            detailViewModel.loadDestinationDetail(currentUser.id)
        } else {
            Timber.d("DetailRoute: Menunggu user ID tersedia...")
        }
    }

//    LaunchedEffect(detailViewModel.eventFlow) {
//        detailViewModel.eventFlow.collectLatest { event ->
//            when (event) {
//                is DetailViewModel.DetailScreenEvent.ShowMessage -> {
//                    scope.launch {
//                        snackbarHostState.showSnackbar(event.message.asString(context))
//                    }
//                }
//            }
//        }
//    }

    DetailContent(
        uiState = detailUiState,
        isAdmin = isAdmin,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onFavoriteClick = { currentUserId?.let { detailViewModel.toggleFavorite(it) } },
        onMapClick = { lat, long -> openGoogleMaps(context, lat, long) },
        modifier = modifier
    )
}

@Composable
private fun DetailContent(
    uiState: UiState<UiDestination>,
    isAdmin: Boolean,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onFavoriteClick: () -> Unit,
    onMapClick: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            when (uiState) {
                is UiState.Loading -> LoadingState()
                is UiState.Success -> {
                    val destination = uiState.data
                    HeaderImage(
                        photoUrl = destination.destination.photos.firstOrNull()?.photoUrl,
                        destinationName = destination.destination.name,
                        isFavorite = destination.isFavorite,
                        isAdmin = isAdmin,
                        onFavoriteClick = onFavoriteClick,
                        onEditClick = { onEditClick(destination.destination.id) },
                        onBackClick = onBackClick
                    )

                    DetailInfoSection(
                        name = destination.destination.name,
                        rating = destination.destination.rating,
                        reviewCount = destination.destination.reviews.size,
                        address = destination.destination.address,
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (destination.destination.photos.isNotEmpty()) {
                            item {
                                PhotoCarouselViewer(
                                    photos = destination.destination.photos,
                                    onRemovePhoto = {})
                            }
                        }
                        item { ReviewsHeader(isAdmin) }
                        items(destination.destination.reviews) { review ->
                            ReviewCard(review)
                        }

                        if (destination.destination.latitude != null && destination.destination.longitude != null) {
                            item {
                                MapButton(onClick = {
                                    onMapClick(
                                        destination.destination.latitude!!,
                                        destination.destination.longitude!!
                                    )
                                }
                                )
                            }
                        }
                    }
                }

                is UiState.Error -> ErrorState(message = uiState.message)
                is UiState.Empty -> EmptyState(message = "Destinasi tidak ditemukan.")
            }
        }
    }
}


@Composable
private fun HeaderImage(
    photoUrl: String?,
    destinationName: String,
    isFavorite: Boolean,
    isAdmin: Boolean,
    onFavoriteClick: () -> Unit,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    ) {
        AsyncImage(
            model = photoUrl,
            contentDescription = "Image of $destinationName",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            error = painterResource(R.drawable.waterfall),
            placeholder = painterResource(R.drawable.error_image)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            if (isAdmin) EditIcon(onClick = onEditClick)
            else FavoriteIcon(onClick = onFavoriteClick, isFavorite = isFavorite)
        }


        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .size(32.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Kembali", tint = Color.White)
        }
    }
}

@Composable
private fun DetailInfoSection(
    name: String,
    rating: Float,
    reviewCount: Int,
    address: String,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.Star, "Rating", tint = Color.Yellow, modifier = Modifier.size(18.dp))
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

@Composable
private fun ReviewsHeader(isAdmin: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text("Ulasan", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        if (!isAdmin) {
            TextButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Add, "Tambah Ulasan", modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Tambah")
            }
        }
    }
}

@Composable
private fun MapButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        text = { Text("Navigasi ke Lokasi") },
        icon = { Icon(Icons.Default.Map, null) },
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    val destination = UiDestination(
        destination = Destination(
            name = "Curug Baturraden",
            address = "Jl. Baturraden, Purwokerto Utara",
            reviews = listOf(Review(authorName = "Pengunjung", rating = 5, text = "Bagus!")),
            rating = 4.5f,
            photos = listOf(Photo("")),
            latitude = -7.318,
            longitude = 109.226
        ),
        isFavorite = true
    )
    WisataBanyumasTheme(dynamicColor = false) {
        DetailContent(
            uiState = UiState.Success(destination),
            isAdmin = false,
            onBackClick = {},
            onEditClick = {},
            onFavoriteClick = {},
            onMapClick = { _, _ -> },
        )
    }
}