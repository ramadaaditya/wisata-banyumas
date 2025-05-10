package com.banyumas.wisata.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun IconAction(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    iconSize: Int = 24,
    backgroundColor: Color = MaterialTheme.colorScheme.surface.copy(
        0.7f
    ),
    iconTint: Color = MaterialTheme.colorScheme.secondary,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .size(36.dp)
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = iconTint,
            modifier = Modifier.size(iconSize.dp)
        )
    }
}

@Composable
fun FavoriteIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isFavorite: Boolean = false
) {
    IconAction(
        modifier = modifier,
        onClick = onClick,
        contentDescription = "Toggle Favorite",
        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    )
}


@Composable
fun EditIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconAction(
        modifier = modifier,
        onClick = onClick,
        contentDescription = "Edit Icon",
        imageVector = Icons.Default.Edit
    )
}

@Composable
fun AddIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconAction(
        modifier = modifier,
        onClick = onClick,
        contentDescription = "Add Icon",
        imageVector = Icons.Default.Add

    )
}

@Composable
fun BackIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconAction(
        modifier = modifier,
        onClick = onClick,
        contentDescription = "Back Icon",
        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
    )
}

@Composable
fun LogoutIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IconAction(
        modifier = modifier,
        onClick = onClick,
        contentDescription = "Logout Icon",
        imageVector = Icons.AutoMirrored.Filled.Logout,
    )
}

@Preview
@Composable
private fun BackIconPreview() {
    WisataBanyumasTheme {
        BackIcon(onClick = {})
    }
}

@Preview
@Composable
private fun FavoriteIconPreview() {
    WisataBanyumasTheme {
        FavoriteIcon(onClick = {})
    }
}

@Preview
@Composable
private fun AddIconPreview() {
    WisataBanyumasTheme {
        AddIcon(onClick = {})
    }
}

@Preview
@Composable
private fun EditIconPreview() {
    WisataBanyumasTheme {
        EditIcon(onClick = {})
    }
}