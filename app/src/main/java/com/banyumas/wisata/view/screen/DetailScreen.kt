package com.banyumas.wisata.view.screen

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.R
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.PhotoCarousel
import com.banyumas.wisata.view.components.ReviewsSection
import com.banyumas.wisata.viewmodel.DetailViewModel
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun DetailScreen(
    destinationId: String?,
    onBackClick: () -> Unit = {},
    viewModel: DetailViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val uiState by viewModel.destination.collectAsState()
    LaunchedEffect(destinationId) {
        destinationId?.let {
            viewModel.fetchDestinationById(destinationId)
        }
    }

    when (uiState) {
        is UiState.Loading -> {
            LoadingState(modifier = modifier.fillMaxSize())
        }

        is UiState.Success -> {
            val destination = (uiState as UiState.Success<UiDestination>).data
            DetailContent(
                destination = destination,
                onFavoriteClick = {},
                onMapClick = { lat, long ->
                    if (lat != null && long != null) {
                        val gmmIntentUri = Uri.parse("geo:$lat,$long")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            Toast.makeText(context, "Google Maps app not found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Log.e("DetailScreen", "Latitude or Longitude is null")
                    }
                },
                onBackClick = onBackClick,
                modifier = Modifier.padding(16.dp)
            )
        }

        is UiState.Error -> {
            ErrorState(
                message = (uiState as UiState.Error).message,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


@Composable
fun DetailContent(
    destination: UiDestination,
    onFavoriteClick: (Boolean) -> Unit = {},
    onMapClick: (Double?, Double?) -> Unit = { _, _ -> },
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            val imagePainter = if (destination.destination.photos.isNotEmpty()) {
                rememberAsyncImagePainter(destination.destination.photos.first().photoUrl)
            } else {
                painterResource(id = R.drawable.image10)
            }
            Image(
                painter = imagePainter,
                contentDescription = "Back",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Detail",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                IconButton(
                    onClick = { onFavoriteClick(true) }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.FavoriteBorder,
                        contentDescription = "Toggle Favorite",
                        tint = Color.White
                    )
                }
            }
        }

        // Details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = destination.destination.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.Yellow
                )
                Text(
                    text = "${destination.destination.rating} (${destination.destination.reviews.size})",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = destination.destination.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        PhotoCarousel(photos = destination.destination.photos)

        ReviewsSection(reviews = destination.destination.reviews)

        // Button
        Button(
            onClick = {
                onMapClick(
                    destination.destination.latitude,
                    destination.destination.longitude
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            Text(text = "See on map")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    WisataBanyumasTheme {
        DetailContent(
            destination = UiDestination(
                Destination(
                    id = "1",
                    name = "Lorem Ipsum",
                    address = "Jl. Lorem Ipsum",
                    rating = 4.5f,
                    reviews = emptyList(),
                    photos = emptyList(),
                    latitude = -7.3323,
                    longitude = 109.1323
                )
            )
        )
    }
}