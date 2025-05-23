package com.banyumas.wisata.view.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.view.theme.BanyumasTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@OptIn(ExperimentalFoundationApi::class)
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
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongPress
            )
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val imagePainter = if (destination.destination.photos.isNotEmpty()) {
                rememberAsyncImagePainter(destination.destination.photos.first().photoUrl)
            } else {
                painterResource(id = R.drawable.image_placeholder)
            }
            Image(
                painter = imagePainter,
                contentScale = ContentScale.Crop,
                contentDescription = "Destination Image",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            ) {
                Text(
                    text = destination.destination.name,
                    style = BanyumasTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    color = BanyumasTheme.colors.onBackground.copy(alpha = 0.7f),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = BanyumasTheme.colors.onBackground.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        fontSize = 12.sp,
                        text = destination.destination.address,
                        maxLines = 1,
                        color = BanyumasTheme.colors.onBackground.copy(alpha = 0.7f),
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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