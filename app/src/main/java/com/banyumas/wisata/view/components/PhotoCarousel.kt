package com.banyumas.wisata.view.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.banyumas.wisata.R
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.utils.EmptyState

@Composable
fun PhotoCarousel(
    photos: List<Any>,
    onAddPhoto: (Uri) -> Unit, // Callback untuk menambah foto
    onRemovePhoto: (Any) -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            uri?.let { onAddPhoto(it) }
        }
    )
    Column {
        Text(
            text = "Foto Destinasi",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (photos.isEmpty()) {
            EmptyState(
                message = "Foto belum tersedia"
            )
        } else {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(photos) { photo ->
                    when (photo) {
                        is Photo -> PhotoItem(
                            photoUrl = photo.photoUrl,
                            onClick = { onRemovePhoto(photo) })

                        is Uri -> PhotoItem(
                            photoUrl = photo.toString(),
                            onClick = { onRemovePhoto(photo) })

                    }
                }
            }
        }

        CustomButton(
            onClick = {
                imagePickerLauncher.launch("image/*")
            },
            text = "Tambah Foto",
        )
    }
}

@Composable
fun PhotoItem(photoUrl: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                model = photoUrl,
                placeholder = rememberAsyncImagePainter(R.drawable.image_placeholder), // Placeholder image
                error = rememberAsyncImagePainter(R.drawable.family) // Error image
            ),
            contentDescription = "Foto Destinasi",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}