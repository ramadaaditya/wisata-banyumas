package com.banyumas.wisata.view.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.view.theme.AppTheme

@Composable
fun ManageDestinationScreen(
    onBack: () -> Unit,
    onSave: (Destination) -> Unit,
    onImportJsonClick: () -> Unit,
    importedDestinations: List<Destination>,
    onBulkInsertClick: () -> Unit,
    selectedDestination: Destination? = null
) {
    var name by rememberSaveable { mutableStateOf(selectedDestination?.name ?: "") }
    var address by rememberSaveable { mutableStateOf(selectedDestination?.address ?: "") }
    var category by rememberSaveable { mutableStateOf(selectedDestination?.category ?: "") }
    var website by rememberSaveable { mutableStateOf(selectedDestination?.website ?: "") }
    var rating by rememberSaveable { mutableStateOf(selectedDestination?.rating?.toString() ?: "") }
    var tags by rememberSaveable {
        mutableStateOf(
            selectedDestination?.tags?.joinToString(", ") ?: ""
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (selectedDestination != null) "Update Wisata" else "Tambah Wisata",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Wisata") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Alamat") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Kategori") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Rating (0.0 - 5.0)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = website,
            onValueChange = { website = it },
            label = { Text("Website") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = tags,
            onValueChange = { tags = it },
            label = { Text("Tags (pisahkan dengan koma)") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                onSave(
                    Destination(
                        id = selectedDestination?.id ?: "",
                        name = name,
                        address = address,
                        category = category,
                        rating = rating.toFloatOrNull() ?: 0f,
                        website = website,
                        tags = tags.split(",").map { it.trim() }
                    )
                )
            }) {
                Text(text = if (selectedDestination != null) "Update" else "Simpan")
            }
            OutlinedButton(onClick = onImportJsonClick) {
                Text("Import JSON")
            }
        }

        if (importedDestinations.isNotEmpty()) {
            Text("Preview Wisata dari JSON", style = MaterialTheme.typography.titleSmall)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(importedDestinations) { destination ->
                    Column {
                        Text("- ${destination.name} (${destination.category})")
                        destination.photos.firstOrNull()?.photoUrl?.let { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(100.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Button(
                onClick = onBulkInsertClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Simpan Semua")
            }
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ManageDestinationScreenPreview_Add() {
    val dummyImported = listOf(
        Destination(id = "1", name = "Curug Cipendok", address = "Baturaden", category = "Alam"),
        Destination(
            id = "2",
            name = "Telaga Sunyi",
            address = "Karanglewas",
            category = "Air Terjun"
        )
    )

    AppTheme {
        ManageDestinationScreen(
            onBack = {},
            onSave = {},
            onImportJsonClick = {},
            importedDestinations = dummyImported,
            onBulkInsertClick = {},
            selectedDestination = null // Mode Tambah
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ManageDestinationScreenPreview_Update() {
    val dummyImported = emptyList<Destination>()

    AppTheme (dynamicColor = false){
        ManageDestinationScreen(
            onBack = {},
            onSave = {},
            onImportJsonClick = {},
            importedDestinations = dummyImported,
            onBulkInsertClick = {},
            selectedDestination = Destination(
                id = "123",
                name = "Taman Andhang Pangrenan",
                address = "Purwokerto Selatan",
                category = "Taman Kota"
            ) // Mode Update
        )
    }
}
