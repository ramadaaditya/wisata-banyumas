package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "List Wisata", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(16.dp))
//
//        LazyVerticalGrid(
//            modifier = Modifier.fillMaxSize(),
//            columns = GridCells.Fixed(2),
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        ) {
//            items(destinations) { destination ->
//                DestinationItem(
//                    destination = destination,
//                    onClick = { navigateToUpdate(destination.destination) },
//                    onFavoriteClick = {}
//                )
//            }
//        }
    }
}