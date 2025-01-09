package com.banyumas.wisata.view.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo

@Composable
fun UpdateDestinationScreen(
    destination: Destination,
    onUpdateDestination: (Destination) -> Unit,
    onUploadPhoto: (String, Uri) -> Unit,
) {
    var updatedDestination by remember { mutableStateOf(destination) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text(
                text = "Update Data for: ${destination.name}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            OutlinedTextField(
                value = updatedDestination.name,
                onValueChange = { updatedDestination = updatedDestination.copy(name = it) },
                label = { Text("Name") },
                placeholder = { Text(destination.name) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = updatedDestination.address,
                onValueChange = { updatedDestination = updatedDestination.copy(address = it) },
                label = { Text("Address") },
                placeholder = { Text(destination.address) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = updatedDestination.latitude.toString(),
                onValueChange = {
                    updatedDestination = updatedDestination.copy(
                        latitude = it.toDoubleOrNull() ?: destination.latitude
                    )
                },
                label = { Text("Latitude") },
                placeholder = { Text(destination.latitude.toString()) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = updatedDestination.longitude.toString(),
                onValueChange = {
                    updatedDestination = updatedDestination.copy(
                        longitude = it.toDoubleOrNull() ?: destination.longitude
                    )
                },
                label = { Text("Longitude") },
                placeholder = { Text(destination.longitude.toString()) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = updatedDestination.rating.toString(),
                onValueChange = {
                    updatedDestination =
                        updatedDestination.copy(rating = it.toFloatOrNull() ?: destination.rating)
                },
                label = { Text("Rating") },
                placeholder = { Text(destination.rating.toString()) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            PhotoCarousel(photos = destination.photos)
        }

        item {
            Button(onClick = { onUpdateDestination(updatedDestination) }) {
                Text("Perbarui Wisata")
            }
        }
    }
}

@Composable
fun PhotoCarousel(photos: List<Photo>, modifier: Modifier = Modifier) {
    if (photos.isEmpty()) {
        Text(text = "No photos available", style = MaterialTheme.typography.bodySmall)
    } else {
        LazyRow(
            modifier = modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(photos) { photo ->
                Image(
                    painter = rememberAsyncImagePainter(photo.photoUrl),
                    contentDescription = "Photo of ${photo.photoUrl}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun UpdateDataPreview() {
    UpdateDestinationScreen(
        destination = Destination(
            id = "1",
            name = "Destination 1",
            address = "Address 1",
            latitude = -7.0,
            longitude = 110.0,
            rating = 4.5f,
            photos = listOf(Photo(photoUrl = "https://via.placeholder.com/"))
        ),
        onUpdateDestination = { destination ->
            println("Updating destination: $destination")
        },
        onUploadPhoto = { destinationId, uri ->
            println("Uploading photo for destination $destinationId with URI $uri")
        }
    )
}