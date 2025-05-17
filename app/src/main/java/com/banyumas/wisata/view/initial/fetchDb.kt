package com.banyumas.wisata.view.initial

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.view.components.ErrorState
import com.banyumas.wisata.view.components.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.dummySearchResultItem
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.BanyumasTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun FetchDatabase(
    viewModel: FetchViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val destinationState by viewModel.destination.collectAsStateWithLifecycle()
    val listDestinationState by viewModel.detailDestinations.collectAsStateWithLifecycle()
    var query by remember { mutableStateOf("") }
    val importedIds by viewModel.importedIds.collectAsStateWithLifecycle()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val json = inputStream?.bufferedReader()?.use { reader -> reader.readText() }
                if (json != null) {
                    viewModel.importJsonFromUri(json)
                }
            }
        }
    )
    val onImportClicked = {
        filePickerLauncher.launch(arrayOf("application/json"))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Search(
            query = query,
            onQueryChange = { query = it },
            onSearch = { viewModel.searchDestination(query) }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(text = "Import", onClick = onImportClicked)
            CustomButton(
                text = "Save All",
                onClick = { viewModel.fetchAndSaveAllDestination(importedIds) })
        }

        Spacer(Modifier.height(4.dp))

        // Prioritaskan tampilan loading/error detailDestinations jika ada
        when (val state = listDestinationState) {
            UiState.Loading -> {
                LoadingState()
            }

            is UiState.Error -> {
                ErrorState(state.message)
            }

            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(WindowInsets.navigationBars.asPaddingValues()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.data) { destination ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(destination.name)
                            Text(destination.address)
                            HorizontalDivider()
                        }
                    }
                }
            }

            UiState.Empty -> {
                // Tampilkan hasil pencarian jika tidak sedang fetch & save all
                when (val searchState = destinationState) {
                    UiState.Loading -> LoadingState()
                    is UiState.Error -> ErrorState(searchState.message)
                    is UiState.Success -> {
                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .padding(WindowInsets.navigationBars.asPaddingValues()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(searchState.data) { destination ->
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(destination.name)
                                    Text(destination.address)
                                    HorizontalDivider()
                                }
                            }
                        }
                    }

                    else -> {
                        // kosong, tidak tampilkan list
                    }
                }
            }
        }

        if (importedIds.isNotEmpty()) {
            Text("Hasil Impor JSON :", Modifier.padding(top = 4.dp))
            importedIds.forEach { id ->
                Text(id, style = BanyumasTheme.typography.labelMedium)
            }
        }
    }
}


@Composable
fun FetchDatabaseContent(
    searchQuery: String,
    modifier: Modifier = Modifier,
    destinations: List<SearchResultItem>,
    onImportClicked: () -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onSearchAllClicked: () -> Unit,
    importedIds: List<String>
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Search(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearchClicked
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            CustomButton(text = "Import", onClick = onImportClicked)
            CustomButton(text = "Save All", onClick = onSearchAllClicked)
        }
        Spacer(Modifier.height(4.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(WindowInsets.navigationBars.asPaddingValues()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(destinations) { destination ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(destination.name)
                    Text(destination.address)
                    HorizontalDivider()
                }
            }
            if (importedIds.isNotEmpty()) {
                item {
                    Text("Hasil Impor JSON :", Modifier.padding(top = 4.dp))
                }
                items(importedIds) { placeId ->
                    Text(placeId, style = BanyumasTheme.typography.labelMedium)
                }
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun FetchDatabasePreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        FetchDatabaseContent(
            destinations = dummySearchResultItem,
            onImportClicked = {},
            onQueryChange = {},
            onSearchClicked = {},
            onSearchAllClicked = {},
            searchQuery = "",
            importedIds = listOf("")
        )
    }
}