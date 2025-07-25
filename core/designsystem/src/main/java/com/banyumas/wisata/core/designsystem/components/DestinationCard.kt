package com.banyumas.wisata.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.core.designsystem.R
import com.banyumas.wisata.core.designsystem.theme.BanyumasTheme
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.UiDestination

@Composable
fun DestinationCard(
    destination: UiDestination,
    onFavoriteClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showFavoriteIcon: Boolean = true,
    onLongPress: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            Color.White
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = modifier
            .width(400.dp)
            .height(300.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .padding(8.dp)
    ) {

        Column {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
            ) {
                val imagePainter = if (destination.destination.photos.isNotEmpty()) {
                    rememberAsyncImagePainter(destination.destination.photos.first().photoUrl)
                } else {
                    painterResource(id = R.drawable.waterfall)
                }
                Image(
                    painter = imagePainter,
                    contentScale = ContentScale.Crop,
                    contentDescription = "Destination Image",
                    modifier = Modifier
                        .fillMaxSize()
                )
                if (showFavoriteIcon) {
                    FavoriteIcon(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp),
                        onClick = { onFavoriteClick(!destination.isFavorite) },
                        isFavorite = destination.isFavorite
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = destination.destination.name,
                    style = BanyumasTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Text(
                    text = destination.destination.address,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    style = BanyumasTheme.typography.bodySmall
                )
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewDestinationCard() {
    WisataBanyumasTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            val sampleDestination = UiDestination(
                destination = Destination(
                    name = "Pantai Parangtritis",
                    address = "Jl. Parangtritis, Yogyakarta",
                ),
                isFavorite = false
            )

            DestinationCard(
                destination = sampleDestination,
                onFavoriteClick = {},
                onClick = {},
                onLongPress = {}
            )
        }
    }
}