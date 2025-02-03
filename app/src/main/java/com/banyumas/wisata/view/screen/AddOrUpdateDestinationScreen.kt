package com.banyumas.wisata.view.screen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.CustomTextField
import com.banyumas.wisata.view.components.PhotoCarousel
import com.banyumas.wisata.viewmodel.DestinationViewModel

@Composable
fun AddOrUpdateDestinationScreen(
    initialDestination: Destination? = null,
    isEditing: Boolean = false,
    viewModel: DestinationViewModel = hiltViewModel(),
    innerPadding: PaddingValues,
) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf(initialDestination ?: Destination()) }
    var selectedPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var deletedPhotos by rememberSaveable { mutableStateOf<List<Photo>>(emptyList()) }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }

    val searchState by viewModel.searchResults.collectAsState()
    val selectedDestinationState by viewModel.selectedDestination.collectAsState()

    val latitudeInput = rememberSaveable { mutableStateOf(destination.latitude?.toString() ?: "") }
    val longitudeInput =
        rememberSaveable { mutableStateOf(destination.longitude?.toString() ?: "") }

    val isFormValid by remember {
        derivedStateOf {
            destination.name.isNotBlank() &&
                    destination.address.isNotBlank() &&
                    latitudeInput.value.toDoubleOrNull() != null &&
                    longitudeInput.value.toDoubleOrNull() != null
        }
    }

    LaunchedEffect(selectedDestinationState) {
        if (selectedDestinationState is UiState.Success) {
            val selectedDestination =
                (selectedDestinationState as UiState.Success<UiDestination>).data.destination
            destination = selectedDestination
            latitudeInput.value = selectedDestination.latitude.toString()
            longitudeInput.value = selectedDestination.longitude.toString()
        }
    }

    if (isLoading) {
        LoadingState()
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isEditing) {
                item {
                    CustomTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = "Nama Wisata",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    )
                }
                item {
                    CustomButton(
                        onClick = { viewModel.searchDestinationsByName(searchQuery) },
                        enabled = searchQuery.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        text = "Cari Wisata",
                        icon = Icons.Default.Search
                    )
                }
            }

            when (searchState) {
                is UiState.Loading -> item {
                    Text(
                        "Mencari wisata...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                is UiState.Success -> {
                    val destinations = (searchState as UiState.Success<List<UiDestination>>).data
                    items(destinations) { fetchedDestination ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    viewModel.fetchPlaceDetailsFromGoogle(fetchedDestination.destination.id)
                                }
                        ) {
                            Text(
                                text = fetchedDestination.destination.name,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "ID: ${fetchedDestination.destination.id}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                is UiState.Error -> item {
                    Text(
                        "Gagal mencari wisata: ${(searchState as UiState.Error).message}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is UiState.Empty -> item {
                    Text(
                        "Tidak ada wisata ditemukan",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            item {
                CustomTextField(
                    value = destination.name,
                    onValueChange = { destination = destination.copy(name = it) },
                    label = "Nama Wisata",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Place"
                        )
                    } // Tambah ikon GPS

                )
            }
            item {
                CustomTextField(
                    value = destination.address,
                    onValueChange = { destination = destination.copy(address = it) },
                    label = "Alamat Wisata",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Address"
                        )
                    } // Tambah ikon GPS
                )
            }
            item {
                CustomTextField(
                    value = latitudeInput.value,
                    onValueChange = { latitudeInput.value = it },
                    label = "Latitude",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "GPS"
                        )
                    } // Tambah ikon GPS
                )
            }
            item {
                CustomTextField(
                    value = longitudeInput.value,
                    onValueChange = { longitudeInput.value = it },
                    label = "Longitude",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "GPS"
                        )
                    } // Tambah ikon GPS
                )
            }

            item {
                PhotoCarousel(
                    photos = destination.photos + selectedPhotos,
                    onAddPhoto = { uri -> selectedPhotos = selectedPhotos + uri },
                    onRemovePhoto = { photo ->
                        when (photo) {
                            is Photo -> {
                                deletedPhotos = deletedPhotos + photo // 🔥 Tandai untuk dihapus dari Firestore & Storage
                                destination = destination.copy(photos = destination.photos - photo) // 🔥 Hapus dari UI
                            }
                            is Uri -> {
                                selectedPhotos = selectedPhotos.filterNot { it == photo } // 🔥 Hapus langsung dari daftar lokal
                            }
                        }
                    },
                )
            }

            item {
                CustomButton(
                    onClick = {
//                        viewModel.saveDestination(destination, selectedPhotos, deletedPhotos, isEditing)
//                        isLoading = true
//                        viewModel.uploadNewPhotos(selectedPhotos, destination.id)
//                        val updatedDestination =
//                            destination.copy(lastUpdated = System.currentTimeMillis())
//                        try {
//                            if (isEditing) {
//                                viewModel.updateDestination(
//                                    updatedDestination,
//                                    selectedPhotos,
//                                    deletedPhotos
//                                )
//                            } else {
//                                viewModel.addDestination(updatedDestination, selectedPhotos)
//                            }
//                            isLoading = false
//                            Toast.makeText(context, "Wisata berhasil disimpan", Toast.LENGTH_SHORT)
//                                .show()
//                            isLoading = false
//                        } catch (e: Exception) {
//                            Toast.makeText(
//                                context,
//                                "Gagal menyimpan wisata",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
                    },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    text = if (isEditing) "Update Wisata" else "Tambah Wisata"
                )
            }
        }
    }
}