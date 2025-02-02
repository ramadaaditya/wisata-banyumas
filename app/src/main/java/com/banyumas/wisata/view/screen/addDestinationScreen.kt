//package com.banyumas.wisata.view.screen
//
//import android.net.Uri
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.Button
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.banyumas.wisata.data.model.Destination
//import com.banyumas.wisata.data.model.Photo
//import com.banyumas.wisata.view.components.PhotoCarousel
//
//@Composable
//fun AddDestinationScreen(
//    onAddDestination: (Destination) -> Unit,
//    onUploadPhoto: (Uri) -> Unit,
//    onFetchDestinationById: (String) -> Destination?, // Callback untuk fetch destinasi
//    onSearchDestinationByName: (String) -> List<Destination> // Callback untuk mencari destinasi berdasarkan nama
//) {
//    var newDestination by remember { mutableStateOf(Destination()) }
//    var isFormValid by remember { mutableStateOf(true) }
//    var destinationId by remember { mutableStateOf("") }
//    var fetchedDestination by remember { mutableStateOf<Destination?>(null) }
//    var searchQuery by remember { mutableStateOf("") } // Query pencarian nama
//    var searchResults by remember { mutableStateOf(emptyList<Destination>()) } // Hasil pencarian
//
//    LazyColumn(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        // Header
//        item {
//            Text(
//                text = "Tambah Wisata Baru",
//                style = MaterialTheme.typography.titleLarge,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )
//            Text(
//                text = "Cari Destination ID atau lengkapi informasi wisata secara manual.",
//                style = MaterialTheme.typography.bodyMedium,
//                color = MaterialTheme.colorScheme.onBackground
//            )
//        }
//
//        // Pencarian Nama Wisata
//        item {
//            OutlinedTextField(
//                value = searchQuery,
//                onValueChange = { searchQuery = it },
//                label = { Text("Cari Nama Wisata") },
//                placeholder = { Text("Masukkan nama wisata") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Button(
//                onClick = {
//                    searchResults = onSearchDestinationByName(searchQuery) // Cari berdasarkan nama
//                },
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("Cari")
//            }
//        }
//
//        // Hasil Pencarian
//        if (searchResults.isNotEmpty()) {
//            item {
//                Text(
//                    text = "Hasil Pencarian:",
//                    style = MaterialTheme.typography.titleMedium,
//                    modifier = Modifier.padding(vertical = 8.dp)
//                )
//            }
//            items(searchResults) { destination ->
//                DestinationSearchResultCard(
//                    destination = destination,
//                    onCopyId = { copiedId -> destinationId = copiedId }
//                )
//            }
//        }
//
//        // Input Destination ID
//        item {
//            OutlinedTextField(
//                value = destinationId,
//                onValueChange = { destinationId = it },
//                label = { Text("Destination ID") },
//                placeholder = { Text("Masukkan Destination ID") },
//                modifier = Modifier.fillMaxWidth()
//            )
//            Button(
//                onClick = {
//                    fetchedDestination = onFetchDestinationById(destinationId)
//                    fetchedDestination?.let { newDestination = it }
//                },
//                modifier = Modifier.padding(top = 8.dp)
//            ) {
//                Text("Muat Data dari ID")
//            }
//
//            fetchedDestination?.let {
//                Text(
//                    text = "Data destinasi berhasil dimuat.",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.primary,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            } ?: if (destinationId.isNotBlank()) {
//                Text(
//                    text = "Destinasi tidak ditemukan.",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            } else {
//
//            }
//        }
//
//        // Form Input Manual
//        item {
//            OutlinedTextField(
//                value = newDestination.name,
//                onValueChange = { newDestination = newDestination.copy(name = it) },
//                label = { Text("Nama Wisata") },
//                placeholder = { Text("Masukkan nama wisata") },
//                isError = !isFormValid && newDestination.name.isBlank(),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = newDestination.address,
//                onValueChange = { newDestination = newDestination.copy(address = it) },
//                label = { Text("Alamat") },
//                placeholder = { Text("Masukkan alamat wisata") },
//                isError = !isFormValid && newDestination.address.isBlank(),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = if (newDestination.latitude == 0.0) "" else newDestination.latitude.toString(),
//                onValueChange = {
//                    newDestination = newDestination.copy(latitude = it.toDoubleOrNull() ?: 0.0)
//                },
//                label = { Text("Latitude") },
//                placeholder = { Text("Masukkan koordinat latitude") },
//                isError = !isFormValid && newDestination.latitude == 0.0,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = if (newDestination.longitude == 0.0) "" else newDestination.longitude.toString(),
//                onValueChange = {
//                    newDestination = newDestination.copy(longitude = it.toDoubleOrNull() ?: 0.0)
//                },
//                label = { Text("Longitude") },
//                placeholder = { Text("Masukkan koordinat longitude") },
//                isError = !isFormValid && newDestination.longitude == 0.0,
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        item {
//            OutlinedTextField(
//                value = if (newDestination.rating == 0.0f) "" else newDestination.rating.toString(),
//                onValueChange = {
//                    newDestination = newDestination.copy(rating = it.toFloatOrNull() ?: 0.0f)
//                },
//                label = { Text("Rating") },
//                placeholder = { Text("Masukkan rating (0.0 - 5.0)") },
//                isError = !isFormValid && (newDestination.rating <= 0.0f || newDestination.rating > 5.0f),
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        // Photo Carousel
//        item {
//            PhotoCarousel(
//                photos = newDestination.photos,
//                onAddPhoto = {
//                    newDestination = newDestination.copy(
//                        photos = newDestination.photos + Photo(photoUrl = it.toString())
//                    )
//                    onUploadPhoto(it)
//                }
//            )
//        }
//
//        // Add Button
//        item {
//            Button(
//                onClick = {
//                    isFormValid = newDestination.name.isNotBlank() &&
//                            newDestination.address.isNotBlank() &&
//                            newDestination.latitude != 0.0 &&
//                            newDestination.longitude != 0.0 &&
//                            newDestination.rating > 0.0f
//
//                    if (isFormValid) {
//                        val destinationToAdd = newDestination.copy(
//                            id = System.currentTimeMillis().toString(),
//                            lastUpdated = System.currentTimeMillis()
//                        )
//                        onAddDestination(destinationToAdd)
//                    }
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Tambah Wisata")
//            }
//
//            if (!isFormValid) {
//                Text(
//                    text = "Harap lengkapi semua data dengan benar!",
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.error,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun DestinationSearchResultCard(destination: Destination, onCopyId: (String) -> Unit) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//    ) {
//        Text(text = "Nama: ${destination.name}", style = MaterialTheme.typography.bodyLarge)
//        Text(text = "ID: ${destination.id}", style = MaterialTheme.typography.bodyMedium)
//        Button(onClick = { onCopyId(destination.id) }) {
//            Text("Salin ID")
//        }
//    }
//}
//
//
//@Preview(showBackground = true)
//@Composable
//private fun AddDestinationPreview() {
//    AddDestinationScreen(
//        onAddDestination = {},
//        onUploadPhoto = {},
//        onFetchDestinationById = { null },
//        onSearchDestinationByName = { emptyList() }
//    )
//}
