package com.banyumas.wisata.view.screen

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun AddDestinationScreen(
    onAddDestination: (Destination) -> Unit,
    onUploadPhoto: (Uri) -> Unit
) {
    var newDestination by remember {
        mutableStateOf(Destination())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Tambah Wisata Baru",
                style = MaterialTheme.typography.titleMedium
            )
        }

        item {
            OutlinedTextField(
                value = newDestination.name,
                onValueChange = { newDestination = newDestination.copy(name = it) },
                label = { Text("Name") },
                placeholder = { Text("Masukkan nama wisata") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = newDestination.address,
                onValueChange = { newDestination = newDestination.copy(address = it) },
                label = { Text("Address") },
                placeholder = { Text("Masukkan alamat wisata") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = if (newDestination.latitude == 0.0) "" else newDestination.latitude.toString(),
                onValueChange = {
                    newDestination = newDestination.copy(latitude = it.toDoubleOrNull() ?: 0.0)
                },
                label = { Text("Latitude") },
                placeholder = { Text("Masukkan latitude") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = if (newDestination.longitude == 0.0) "" else newDestination.longitude.toString(),
                onValueChange = {
                    newDestination = newDestination.copy(longitude = it.toDoubleOrNull() ?: 0.0)
                },
                label = { Text("Longitude") },
                placeholder = { Text("Masukkan longitude") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = if (newDestination.rating == 0.0f) "" else newDestination.rating.toString(),
                onValueChange = {
                    newDestination = newDestination.copy(rating = it.toFloatOrNull() ?: 0.0f)
                },
                label = { Text("Rating") },
                placeholder = { Text("Masukkan rating (0.0 - 5.0)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            PhotoCarousel(
                photos = newDestination.photos,
                onAddPhoto = {
                    newDestination = newDestination.copy(
                        photos = newDestination.photos + Photo(photoUrl = it.toString())
                    )
                    onUploadPhoto(it)
                }
            )
        }

        item {
            Button(onClick = {
                if (newDestination.name.isNotBlank() && newDestination.address.isNotBlank() &&
                    newDestination.latitude != 0.0 && newDestination.longitude != 0.0 &&
                    newDestination.rating != 0.0f
                ) {
                    val destinationToAdd = newDestination.copy(
                        id = System.currentTimeMillis().toString()
                    )
                    onAddDestination(destinationToAdd)
                } else {
                    println("Semua data harus diisi!")
                }
            }) {
                Text("Tambah Wisata")
            }
        }
    }
}

@Composable
fun PhotoCarousel(
    photos: List<Photo>,
    onAddPhoto: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        if (photos.isEmpty()) {
            Text(
                text = "Belum ada foto. Tambahkan foto wisata.",
                style = MaterialTheme.typography.bodySmall
            )
        } else {
            LazyRow(
                modifier = modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(photos) { photo ->
                    Image(
                        painter = rememberAsyncImagePainter(photo.photoUrl),
                        contentDescription = "Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            }
        }

        Button(onClick = {
            // Simulasikan memilih gambar dari galeri
            val dummyUri = Uri.parse("https://via.placeholder.com/150")
            onAddPhoto(dummyUri)
        }) {
            Text("Tambah Foto")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddDestinationPreview() {
    AddDestinationScreen(
        onAddDestination = { destination ->
            println("Destination added: $destination")
        },
        onUploadPhoto = { uri ->
            println("Photo uploaded: $uri")
        }
    )
}
