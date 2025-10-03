package com.banyumas.wisata.core.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.core.designsystem.R
import com.banyumas.wisata.core.designsystem.theme.BanyumasTheme
import com.banyumas.wisata.core.model.Photo

@Composable
fun PhotoCarouselViewer(
    photos: List<Photo>,
    onRemovePhoto: (Photo) -> Unit,
    showRemoveIcon: Boolean = false,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (photos.isNotEmpty()) {
            PhotoItem(
                photos = photos,
                showRemoveIcon = showRemoveIcon,
                onRemovePhoto = onRemovePhoto,
            )
        } else {
            Image(
                painterResource(R.drawable.empty_image),
                contentDescription = "Empty image",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                alignment = Alignment.Center
            )
        }
    }
}


@Composable
fun PhotoItem(
    photos: List<Photo>,
    showRemoveIcon: Boolean,
    onRemovePhoto: (Photo) -> Unit,
) {
    var selectedPhoto by remember { mutableStateOf<String?>(null) }
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(photos) { photo ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = photo.photoUrl,
                        placeholder = painterResource(R.drawable.image_placeholder),
                        error = painterResource(R.drawable.error_image)
                    ),
                    contentDescription = "Foto Destinasi",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { selectedPhoto = photo.photoUrl }
                )

                if (showRemoveIcon) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hapus Foto",
                        tint = BanyumasTheme.colors.background,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(4.dp)
                            .align(Alignment.TopEnd)
                            .clickable { onRemovePhoto(photo) }
                    )
                }
            }
        }
    }
    selectedPhoto?.let { photoUrl ->
        Dialog(onDismissRequest = { selectedPhoto = null }) {
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Hapus Foto",
                        tint = BanyumasTheme.colors.background,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clickable { selectedPhoto = null }
                    )
                    Image(
                        painter = rememberAsyncImagePainter(photoUrl),
                        contentDescription = "Enlarged Image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}