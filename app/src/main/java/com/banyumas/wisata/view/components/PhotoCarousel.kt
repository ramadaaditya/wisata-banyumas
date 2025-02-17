package com.banyumas.wisata.view.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.utils.EmptyState

@Composable
fun PhotoCarouselViewer(
    photos: List<Any>,
    onAddPhoto: (Uri) -> Unit,
    onRemovePhoto: (Any) -> Unit,
    showRemoveIcon: Boolean = false
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { onAddPhoto(it) }
        }
    )

    Column {
        if (photos.isEmpty()) {
            EmptyState(message = "Foto belum tersedia")
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(photos) { photo ->
                    val imageUrl = when (photo) {
                        is Photo -> photo.photoUrl
                        is Uri -> photo.toString()
                        else -> ""
                    }

                    if (imageUrl.isNotBlank()) {
                        PhotoItem(
                            photoUrl = imageUrl,
                            showRemoveIcon = showRemoveIcon,
                            onRemove = { onRemovePhoto(photo) }
                        )
                    }
                }
            }
        }

        CustomButton(
            onClick = { imagePickerLauncher.launch("image/*") },
            text = "Tambah Foto",
        )
    }
}


@Composable
fun PhotoItem(
    photoUrl: String,
    showRemoveIcon: Boolean,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = photoUrl,
                placeholder = rememberAsyncImagePainter(R.drawable.image_placeholder), // Placeholder image
                error = rememberAsyncImagePainter(R.drawable.family)
            ),
            contentDescription = "Foto Destinasi",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        if (showRemoveIcon) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Hapus Foto",
                tint = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .size(30.dp)
                    .padding(4.dp)
                    .align(Alignment.TopEnd)
                    .clickable { onRemove() }
            )
        }
    }
}