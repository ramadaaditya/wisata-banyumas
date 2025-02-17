package com.banyumas.wisata.view.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.model.Role
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.view.components.BackIcon
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.EditIcon
import com.banyumas.wisata.view.components.FavoriteIcon
import com.banyumas.wisata.view.components.PhotoCarouselViewer
import com.banyumas.wisata.view.components.ReviewCard
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.openGoogleMaps
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun DetailScreen(
    destinationId: String?,
    viewModel: DestinationViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onEditClick: (Destination) -> Unit,
    onAddCommentClick: (Destination) -> Unit
) {
    val uiState by viewModel.selectedDestination.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val currentUser = (authState as? UiState.Success)?.data
    val isAdmin = remember { currentUser?.role == Role.ADMIN }

    LaunchedEffect(destinationId) {
        if (destinationId.isNullOrBlank()) {
            viewModel.logError("destinationId null atau kosong!")
        } else {
            viewModel.getDestinationById(destinationId, currentUser?.id.orEmpty())
        }
    }

    when (val state = uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Success -> {
            DetailContent(
                destination = state.data,
                onMapClick = { lat, long ->
                    openGoogleMaps(context, lat, long)
                },
                onFavoriteClick = { uiDestination ->
                    currentUser?.let {
                        viewModel.toggleFavorite(
                            userId = currentUser.id,
                            destinationId = uiDestination.destination.id,
                            isFavorite = !uiDestination.isFavorite
                        )
                    }
                },
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onAddCommentClick = onAddCommentClick,
                isAdmin = isAdmin
            )
        }

        is UiState.Error -> ErrorState(message = state.message)
        UiState.Empty -> EmptyState()
    }
}


@Composable
fun DetailContent(
    destination: UiDestination,
    onMapClick: (Double?, Double?) -> Unit,
    onFavoriteClick: (UiDestination) -> Unit,
    onBackClick: () -> Unit,
    onEditClick: (Destination) -> Unit,
    onAddCommentClick: (Destination) -> Unit,
    isAdmin: Boolean
) {
    Column {
        Box {
            val firstPhotoUrl = destination.destination.photos.firstOrNull()?.photoUrl
            DestinationImage(imageUrl = firstPhotoUrl)
            BackIcon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
                onClick = onBackClick
            )
            if (isAdmin) {
                EditIcon(
                    onClick = {
                        onEditClick(destination.destination)
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                )
            } else {
                FavoriteIcon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    onClick = { onFavoriteClick(destination) },
                    isFavorite = destination.isFavorite
                )
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            DetailSection(
                name = destination.destination.name,
                rating = destination.destination.rating,
                reviewCount = destination.destination.reviewsFromGoogle.size +
                        destination.destination.reviewsFromLocal.size,
                address = destination.destination.address,
            )
            if (destination.destination.photos.isNotEmpty()) {
                PhotoCarouselViewer(photos = destination.destination.photos)
            }
            Row (verticalAlignment = Alignment.Top){
                Text(
                    text = "Ulasan",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (!isAdmin) {
                    CustomButton(
                        onClick = {onAddCommentClick(destination.destination)},
                        icon = Icons.Default.Add,
                        text = "Add",
                        iconSize = Modifier.size(20.dp),
                        textStyle = MaterialTheme.typography.titleSmall,
                    )
                }
            }
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(destination.destination.reviewsFromGoogle + destination.destination.reviewsFromLocal) { review ->
                    ReviewCard(review)
                }
                item {
                    CustomButton(
                        onClick = {
                            onMapClick(
                                destination.destination.latitude,
                                destination.destination.longitude
                            )
                        },
                        text = "Navigasi ke Lokasi",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DestinationImage(imageUrl: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageRequest = remember(imageUrl) {
        ImageRequest.Builder(context)
            .data(imageUrl)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .crossfade(true)
            .build()
    }

    val painter = rememberAsyncImagePainter(model = imageRequest)

    Image(
        painter = painter,
        contentDescription = "Image of ${imageUrl.orEmpty()}",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}


@Composable
fun DetailSection(
    name: String,
    rating: Float,
    reviewCount: Int,
    address: String,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating Star",
                tint = Color.Yellow,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "$rating/5",
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "$reviewCount Review",
                style = MaterialTheme.typography.labelLarge
            )
        }
        Text(
            text = address,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun DetailContentPreview() {
    AppTheme {
        DetailContent(
            destination =
                UiDestination(
                    destination = Destination(
                        name = "Curug Baturraden",
                        address = "Jl. Baturraden, Purwokerto Utara",
                        reviewsFromGoogle = listOf(
                            Review(
                                authorName = "John Doe",
                                rating = 5,
                                text = "Great place to visit!",
                                source = "Google"
                            )
                        ),
                        reviewsFromLocal = listOf(
                            Review(
                                authorName = "Jane Doe",
                                rating = 4,
                                text = "Nice place!",
                                source = "local"
                            )
                        ),
                        rating = 4.5f,
                        photos = listOf(Photo("https://via.p0laceholder.com/600x400")), // Dummy photo
                    )
                ),
            onMapClick = { _, _ -> },
            onFavoriteClick = {},
            onBackClick = {},
            isAdmin = false,
            onEditClick = {},
            onAddCommentClick = {}
        )
    }
}