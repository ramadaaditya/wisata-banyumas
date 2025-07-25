package com.banyumas.wisata.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme

@Composable
fun CategoryRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier : Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            CategoryChip(
                label = category,
                isSelected = isSelected,
                onClick = { onCategorySelected(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val textColor =
        if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(
            alpha = 0.8f
        )
    Box(
        modifier = Modifier
            .shadow(
                elevation = if (isSelected) 2.dp else 1.dp,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable (onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .wrapContentSize(Alignment.Center),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(name = "Category Row Light", showBackground = true)
@Composable
private fun CategoryRowPreviewLight() {
    // Gunakan tema aplikasi Anda
    WisataBanyumasTheme(darkTheme = false, dynamicColor = false) {
        var selectedCategory by remember { mutableStateOf("Alam") }
        val categories = listOf("Semua", "Alam", "Kuliner", "Sejarah", "Edukasi", "Belanja")

        CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { newCategory ->
                selectedCategory = newCategory
            }
        )
    }
}

@Preview(name = "Category Row Dark", showBackground = true)
@Composable
private fun CategoryRowPreviewDark() {
    WisataBanyumasTheme(darkTheme = true) {
        var selectedCategory by remember { mutableStateOf("Kuliner") }
        val categories = listOf("Semua", "Alam", "Kuliner", "Sejarah", "Edukasi", "Belanja")

        CategoryRow(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { newCategory ->
                selectedCategory = newCategory
            }
        )
    }
}