package com.banyumas.wisata.view.initial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.dummySearchResultItem
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.Search
import com.banyumas.wisata.view.theme.AppTheme

@Composable
fun FetchDatabase(
    viewModel: FetchViewModel = hiltViewModel()
) {
    val destinationState by viewModel.destination.collectAsStateWithLifecycle()

    when (val state = destinationState) {
        UiState.Empty -> {
            FetchDatabaseContent(
                destinations = emptyList(),
                searchAllByJson = { name -> viewModel.searchDestination(name) }
            )
        }

        is UiState.Error -> {
            ErrorState(state.message)
        }

        UiState.Loading -> {
            LoadingState()
        }

        is UiState.Success -> {
            FetchDatabaseContent(
                destinations = state.data,
                searchAllByJson = { name -> viewModel.searchDestination(name) }
            )
        }
    }
}


@Composable
fun FetchDatabaseContent(
    modifier: Modifier = Modifier,
    destinations: List<SearchResultItem>,
    searchAllByJson: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Search(
                query = query,
                onQueryChange = { query = it },
                onSearch = { searchAllByJson(query) }
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(destinations) { destination ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(destination.name)
                        Text(destination.address)
                    }
                    HorizontalDivider()
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomButton(text = "Save All", onClick = {})
            CustomButton(text = "Import", onClick = {})
            CustomButton(text = "Search", onClick = { searchAllByJson(query) })
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
private fun FetchDatabasePreview() {
    AppTheme {
        FetchDatabaseContent(
            destinations = dummySearchResultItem,
            searchAllByJson = {},
        )
    }
}