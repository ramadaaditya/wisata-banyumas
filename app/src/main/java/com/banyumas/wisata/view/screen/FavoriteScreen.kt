package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.view.components.DestinationItem
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun FavoriteScreen(
    destination: List<UiDestination> = emptyList(),
    navigateToDetail: (Destination) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(destination) { destination ->
            DestinationItem(
                destination = destination,
                onFavoriteClick = {},
                onClick = {
                    navigateToDetail(destination.destination)
                }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun FavoriteScreenPreview() {
    WisataBanyumasTheme {
        FavoriteScreen()
    }
}