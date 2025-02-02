package com.banyumas.wisata.view.screen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.PhotoCarousel
import com.banyumas.wisata.viewmodel.DestinationViewModel

@Composable
fun AddOrUpdateDestinationScreen(
    initialDestination: Destination? = null,
    isEditing: Boolean = false,
    viewModel: DestinationViewModel = hiltViewModel(),
    onSubmit: () -> Unit,
    innerPadding: PaddingValues
) {
    var destination by remember { mutableStateOf(initialDestination ?: Destination()) }
    var selectedPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var deletedPhotos by rememberSaveable { mutableStateOf<List<Photo>>(emptyList()) }

    var latitudeInput by rememberSaveable { mutableStateOf(destination.latitude.toString()) }
    var longitudeInput by rememberSaveable { mutableStateOf(destination.longitude.toString()) }

    // 🔹 Pastikan state diperbarui saat `initialDestination` berubah
    LaunchedEffect(initialDestination) {
        if (initialDestination != null) {
            destination = initialDestination
            latitudeInput = initialDestination.latitude.toString()
            longitudeInput = initialDestination.longitude.toString()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect {
            onSubmit()
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            destination.name.isNotBlank() &&
                    destination.address.isNotBlank() &&
                    latitudeInput.toDoubleOrNull() != null &&
                    longitudeInput.toDoubleOrNull() != null
        }
    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Input Nama
        item {
            OutlinedTextField(
                value = destination.name,
                onValueChange = { destination = destination.copy(name = it) },
                label = { Text("Nama Wisata") },
                placeholder = { Text("Masukkan nama wisata") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Input Alamat
            OutlinedTextField(
                value = destination.address,
                onValueChange = { destination = destination.copy(address = it) },
                label = { Text("Alamat Wisata") },
                placeholder = { Text("Masukkan alamat wisata") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Input Latitude dengan Validasi
            OutlinedTextField(
                value = latitudeInput,
                onValueChange = { input ->
                    latitudeInput = input
                },
                label = { Text("Latitude") },
                placeholder = { Text("Masukkan latitude") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Input Longitude dengan Validasi
            OutlinedTextField(
                value = longitudeInput,
                onValueChange = { input ->
                    longitudeInput = input
                },
                label = { Text("Longitude") },
                placeholder = { Text("Masukkan longitude") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            // Foto Destinasi
            PhotoCarousel(
                photos = destination.photos + selectedPhotos,
                onAddPhoto = { uri -> selectedPhotos = selectedPhotos + uri },
                onRemovePhoto = { photo ->
                    when (photo) {
                        is Photo -> if (!photo.photoUrl.contains("googleapis.com")) {
                            deletedPhotos = deletedPhotos + photo
                        }

                        is Uri -> selectedPhotos = selectedPhotos.filterNot { it == photo }
                    }
                }
            )
        }

        item {
            // Tombol Submit
            CustomButton(
                onClick = {
                    val updatedDestination = destination.copy(
                        latitude = latitudeInput.toDoubleOrNull() ?: destination.latitude,
                        longitude = longitudeInput.toDoubleOrNull() ?: destination.longitude
                    )

                    if (isEditing) {
                        viewModel.updateDestination(
                            updatedDestination,
                            selectedPhotos,
                            deletedPhotos
                        )
                        Log.d("AddOrUpdate", "Update destinasi: $updatedDestination")
                    } else {
                        viewModel.addDestination(updatedDestination, selectedPhotos)
                        Log.d("AddOrUpdate", "Tambah destinasi: $updatedDestination")
                    }
                    onSubmit()
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth(),
                text = if (isEditing) "Update Wisata" else "Tambah Wisata"
            )
        }

        item {
            if (!isFormValid) {
                Text(
                    text = "Semua data harus diisi dengan benar.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
