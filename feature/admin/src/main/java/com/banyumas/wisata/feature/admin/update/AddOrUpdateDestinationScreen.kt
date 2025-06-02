package com.banyumas.wisata.feature.admin.update

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.R
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.designsystem.components.CustomButton
import com.banyumas.wisata.core.designsystem.components.CustomTextField
import com.banyumas.wisata.core.designsystem.components.DropDownMenu
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.wisata.banyumas.feature.admin.R

@Composable
fun AddOrUpdateDestinationScreen(
    initialDestination: com.banyumas.wisata.core.model.Destination? = null,
    isEditing: Boolean = false,
    viewModel: DestinationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var destination by remember { mutableStateOf(initialDestination ?: com.banyumas.wisata.core.model.Destination()) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchState by viewModel.uiDestinations.collectAsStateWithLifecycle()
    val selectedDestinationState by viewModel.selectedDestination.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is DestinationViewModel.DestinationEvent.Success -> {
                    destination = com.banyumas.wisata.core.model.Destination()
                    searchQuery = ""
                }

                is DestinationViewModel.DestinationEvent.ShowMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val isFormValid by remember {
        derivedStateOf {
            destination.name.isNotBlank() &&
                    destination.address.isNotBlank() &&
                    destination.latitude.toString().isNotBlank() &&
                    destination.longitude.toString().isNotBlank() &&
                    destination.category.isNotBlank()
        }
    }

    when (searchState) {
        UiState.Empty -> TODO()
        is UiState.Error -> TODO()
        UiState.Loading -> TODO()
        is UiState.Success<*> -> TODO()
    }
}


@Composable
fun AddOrUpdateDestinationContent(
    searchQuery: String,
    destination: com.banyumas.wisata.core.model.Destination,
    isEditing: Boolean = false,
    photoUrl: String,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (!isEditing) {
            com.banyumas.wisata.core.designsystem.components.Search(
                query = searchQuery,
                onQueryChange = {},
                placeholder = stringResource(R.string.placeholder_search),
                onSearch = {}
            )
        }
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
        DropDownMenu(
            selectedCategory = destination.category,
            onCategorySelected = {}
        )
        CustomButton(
            onClick = {},
            enabled = true,
            modifier = Modifier.fillMaxWidth(),
            text = if (isEditing) "Update Wisata" else "Tambah Wisata"
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun AddOrUpdateDestinationPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        AddOrUpdateDestinationContent(
            searchQuery = "",
            destination = Destination(
                name = "Amikom Purwokerto",
                longitude = 1234.32413,
                latitude = 1234.32413,
                address = "Banyumas",
            ),
            photoUrl = "",
        )
    }
}