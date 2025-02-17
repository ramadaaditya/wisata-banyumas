package com.banyumas.wisata.view.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.banyumas.wisata.R
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.PhotoCarousel
import com.banyumas.wisata.view.components.ReviewsSection
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun DetailScreen(
    destinationId: String?,
    viewModel: DestinationViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val uiState by viewModel.selectedDestination.collectAsStateWithLifecycle()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()

    val currentUser = (authState as? UiState.Success)?.data
    val isAdmin = currentUser?.role == Role.ADMIN
    Log.d("DetailScreen", "User role: ${currentUser?.role}, isAdmin : $isAdmin")

    LaunchedEffect(destinationId) {
        if (!destinationId.isNullOrBlank()) {
            Log.d("DetailScreen", "Fetching destination data for ID: $destinationId")
            viewModel.getDestinationById(destinationId, currentUser?.id.orEmpty())
        } else {
            Log.e("DetailScreen", "destinationId null atau kosong!")
        }
    }

    // Observasi status selectedDestination
    when (uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Success -> {
            val destination = (uiState as UiState.Success<UiDestination>).data
            DetailContent(
                destination = destination,
                onMapClick = { lat, long ->
                    openGoogleMaps(context, lat, long)
                },
                innerPadding = innerPadding
            )
        }

        is UiState.Error -> ErrorState(message = (uiState as UiState.Error).message)
        UiState.Empty -> EmptyState()
    }
}

@Composable
fun DetailContent(
    destination: UiDestination,
    onMapClick: (Double?, Double?) -> Unit,
    innerPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            item {
                val firstPhotoUrl = destination.destination.photos.firstOrNull()?.photoUrl
                DestinationImage(imageUrl = firstPhotoUrl)
            }
            item {
                DetailSection(
                    name = destination.destination.name,
                    rating = destination.destination.rating,
                    reviewCount = destination.destination.reviewsFromGoogle.size +
                            destination.destination.reviewsFromLocal.size,
                    address = destination.destination.address
                )
            }
            item {
                if (destination.destination.photos.isNotEmpty()) {
                    PhotoCarousel(photos = destination.destination.photos)
                }
            }
            item {
                ReviewsSection(
                    reviews = destination.destination.reviewsFromGoogle +
                            destination.destination.reviewsFromLocal
                )
            }
            item {
                CustomButton(
                    onClick = {
                        onMapClick(
                            destination.destination.latitude,
                            destination.destination.longitude
                        )
                    },
                    text = "Navigasi ke Lokasi"
                )
            }
        }
    }
}

@Composable
fun DestinationImage(imageUrl: String?) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_placeholder)
            .crossfade(true)
            .build()
    )

    Image(
        painter = painter,
        contentDescription = "Destination Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    )
}


@Composable
fun DetailSection(
    name: String,
    rating: Float,
    reviewCount: Int,
    address: String
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
                style = AppTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color.Yellow
            )
            Text(
                text = "$rating ($reviewCount)",
                style = AppTheme.typography.body
            )
        }
        Text(
            text = address,
            style = AppTheme.typography.body,
            color = AppTheme.colorScheme.secondary
        )
    }
}

private fun openGoogleMaps(context: Context, lat: Double?, long: Double?) {
    val uri = if (lat != null && long != null) {
        Uri.parse("geo:$lat,$long?q=$lat,$long")
    } else {
        Uri.parse("https://maps.google.com/")
    }
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(mapIntent)
}
