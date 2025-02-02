package com.banyumas.wisata.view.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.R
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

@Composable
fun DetailScreen(
    userId: String,
    destinationId: String?,
    viewModel: DestinationViewModel = hiltViewModel(),
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val uiState by viewModel.selectedDestination.collectAsStateWithLifecycle()

    LaunchedEffect(destinationId) {
        destinationId?.let { viewModel.getDestinationById(destinationId, userId) }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            destinationId?.let { viewModel.getDestinationById(it, userId) }
        }
    }
    when (uiState) {
        is UiState.Loading -> LoadingState()
        is UiState.Success -> {
            val destination = (uiState as UiState.Success<UiDestination>).data
            DetailContent(
                destination = destination,
                onMapClick = { lat, long ->
                    handleMapClick(context, lat, long)
                },
                innerPadding = innerPadding
            )
        }

        is UiState.Error -> ErrorState(message = (uiState as UiState.Error).message)
        UiState.Empty -> EmptyState()
    }
}

private fun handleMapClick(context: Context, lat: Double?, long: Double?) {
    if (lat != null && long != null) {
        val gmmIntentUri = Uri.parse("https://maps.google.com/maps?daddr=$lat,$long")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }

        try {
            context.startActivity(mapIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Google Maps tidak tersedia di perangkat ini.",
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
        Toast.makeText(context, "Koordinat lokasi tidak tersedia.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun DetailContent(
    destination: UiDestination,
    onMapClick: (Double?, Double?) -> Unit,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DestinationImage(imageUrl = destination.destination.photos.firstOrNull()?.photoUrl)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item {
                DetailSection(
                    name = destination.destination.name,
                    rating = destination.destination.rating,
                    reviewCount = destination.destination.reviewsFromGoogle.size + destination.destination.reviewsFromLocal.size,
                    address = destination.destination.address
                )
            }
            item {
                PhotoCarousel(photos = destination.destination.photos)
            }
            item {
                ReviewsSection(reviews = destination.destination.reviewsFromGoogle + destination.destination.reviewsFromLocal)
            }
            item {
                CustomButton(
                    onClick = {
                        onMapClick(
                            destination.destination.latitude,
                            destination.destination.longitude
                        )
                    },
                    text = "Navigasi Ke Lokasi"
                )
            }
        }
    }
}

@Composable
fun DestinationImage(
    imageUrl: String?,
) {
    val imagePainter = rememberAsyncImagePainter(imageUrl ?: R.drawable.image_placeholder)

    Image(
        painter = imagePainter,
        contentDescription = "Destination Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
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
        modifier = Modifier
            .fillMaxWidth()
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