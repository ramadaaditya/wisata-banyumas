package com.banyumas.wisata.view.update

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.CustomTextField
import com.banyumas.wisata.view.components.DestinationCard
import com.banyumas.wisata.view.components.DropDownMenu
import com.banyumas.wisata.view.components.PhotoCarouselViewer
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.DestinationViewModel

@Composable
fun AddOrUpdateDestinationScreen(
    initialDestination: Destination? = null,
    isEditing: Boolean = false,
    viewModel: DestinationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf(initialDestination ?: Destination()) }
    val latitudeInput = rememberSaveable { mutableStateOf(destination.latitude?.toString() ?: "") }
    val longitudeInput =
        rememberSaveable { mutableStateOf(destination.longitude?.toString() ?: "") }
    var selectedPhotos by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var deletedPhotos by rememberSaveable { mutableStateOf<List<Photo>>(emptyList()) }
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DestinationViewModel.DestinationEvent.Success -> {
                    destination = Destination()
                    latitudeInput.value = ""
                    longitudeInput.value = ""
                    selectedPhotos = emptyList()
                    deletedPhotos = emptyList()
                    searchQuery = ""
                }

                is DestinationViewModel.DestinationEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val searchState by viewModel.destinations.collectAsState()
    val selectedDestinationState by viewModel.selectedDestination.collectAsState()
    LaunchedEffect(selectedDestinationState) {
        if (!isEditing && selectedDestinationState is UiState.Success) {
            val fetchedUiDestination =
                (selectedDestinationState as UiState.Success<UiDestination>).data
            val fetchedDestination = fetchedUiDestination.destination
            destination = fetchedDestination
            latitudeInput.value = fetchedDestination.latitude.toString()
            longitudeInput.value = fetchedDestination.longitude.toString()
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            destination.name.isNotBlank() &&
                    destination.address.isNotBlank() &&
                    latitudeInput.value.toDoubleOrNull() != null &&
                    longitudeInput.value.toDoubleOrNull() != null &&
                    destination.category.isNotBlank()
        }
    }

//    fun onSaveDestination() {
//        val latVal = latitudeInput.value.toDoubleOrNull()
//        val longVal = longitudeInput.value.toDoubleOrNull()
//
//        if (latVal == null || longVal == null) {
//            Toast.makeText(context, "Latitude / Longitude tidak valid!", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val finalDestination = destination.copy(
//            latitude = latVal,
//            longitude = longVal
//        )
//
//        if (isEditing) {
//            if (finalDestination.id.isBlank()) {
//                Toast.makeText(
//                    context,
//                    "ID destinasi kosong, tidak bisa update!",
//                    Toast.LENGTH_SHORT
//                ).show()
//                return
//            }
//            viewModel.updateDestination(
//                destination = finalDestination,
//                newImageUris = selectedPhotos,
//                deletedPhotos = deletedPhotos
//            )
//        } else {
//            val newDestination =
//                finalDestination.copy(id = finalDestination.id.ifBlank { generateNewId() })
//
//            viewModel.saveNewDestination(
//                destination = newDestination,
//                imageUris = selectedPhotos
//            )
//        }
//    }

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
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
                    onClick = {
//                        viewModel.searchDestinationsByName(searchQuery)
                    },
                    enabled = searchQuery.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    text = "Cari Wisata",
                )
            }

            when (searchState) {
                is UiState.Loading -> {}

                is UiState.Success -> {
                    val destinations = (searchState as UiState.Success<List<UiDestination>>).data
                    if (destinations.isEmpty()) {
                        item {
                            Text(
                                "Tidak ada destinasi yang cocok.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    } else {
                        items(destinations) { fetchedDestination ->
                            DestinationCard(
                                destination = fetchedDestination,
                                onFavoriteClick = {},
                                onClick = {
//                                    viewModel.fetchPlaceDetailsFromGoogle(fetchedDestination.destination.id)
                                },
                                onLongPress = {},
                                showFavoriteIcon = false
                            )
                        }
                    }
                }

                is UiState.Error -> {
                    item {
                        Text(
                            "Gagal mencari wisata: ${(searchState as UiState.Error).message}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                is UiState.Empty -> {
                    item {
                        Text(
                            "Tidak ada wisata ditemukan",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
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
                }
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
                }
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
                }
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
                }
            )
        }

        item {
            DropDownMenu(
                selectedCategory = destination.category,
                onCategorySelected = { category ->
                    destination = destination.copy(category = category)
                }
            )
        }

        item {
            PhotoCarouselViewer(
                photos = destination.photos + selectedPhotos,
                onAddPhoto = { uri -> selectedPhotos = selectedPhotos + uri },
                onRemovePhoto = { photo ->
                    when (photo) {
                        is Photo -> {
                            deletedPhotos = deletedPhotos + photo
                            destination = destination.copy(photos = destination.photos - photo)
                        }

                        is Uri -> {
                            selectedPhotos = selectedPhotos.filterNot { it == photo }
                        }
                    }
                },
                showRemoveIcon = true
            )
        }

        item {
            CustomButton(
                onClick = {
//                    onSaveDestination()
                },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth(),
                text = if (isEditing) "Update Wisata" else "Tambah Wisata"
            )
        }
    }
}


@Composable
fun AddOrUpdateDestinationContent(
    modifier: Modifier = Modifier,
    searchQuery: String,
    destination: Destination,
) {
    var firebasePhoto by rememberSaveable { mutableStateOf<List<Uri>>(emptyList()) }
    var localPhoto by rememberSaveable { mutableStateOf<List<Photo>>(emptyList()) }
    val isEditing = true

    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isEditing) {
            item {
                CustomTextField(
                    value = searchQuery,
                    onValueChange = {},
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
                    onClick = {},
                    enabled = searchQuery.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    text = "Cari Wisata",
                )
            }
        }

        item {
            CustomTextField(
                value = destination.name,
                onValueChange = {},
                label = "Nama Wisata",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Place"
                    )
                }
            )
        }
        item {
            CustomTextField(
                value = destination.address,
                onValueChange = {},
                label = "Alamat Wisata",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Address"
                    )
                }
            )
        }
        item {
            CustomTextField(
                value = destination.latitude.toString(),
                onValueChange = {},
                label = "Latitude",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "GPS"
                    )
                }
            )
        }
        item {
            CustomTextField(
                value = destination.longitude.toString(),
                onValueChange = {},
                label = "Longitude",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "GPS"
                    )
                }
            )
        }

        item {
            DropDownMenu(
                selectedCategory = destination.category,
                onCategorySelected = {}
            )
        }

        item {
            PhotoCarouselViewer(
                photos = destination.photos + firebasePhoto,
                onAddPhoto = { uri -> firebasePhoto = firebasePhoto + uri },
                onRemovePhoto = { photo ->
                    when (photo) {
                        is Photo -> {}

                        is Uri -> {
                            firebasePhoto = firebasePhoto.filterNot { it == photo }
                        }
                    }
                },
                showRemoveIcon = true
            )
        }

        item {
            CustomButton(
                onClick = {},
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                text = if (isEditing) "Update Wisata" else "Tambah Wisata"
            )
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun AddOrUpdateDestinationPreview() {
    AppTheme {
        AddOrUpdateDestinationContent(
            searchQuery = "Rambutan",
            destination = Destination(
                name = "Amikom Purwokerto",
                longitude = 1234.32413,
                latitude = 1234.32413,
                address = "Banyumas",
            )
        )
    }
}