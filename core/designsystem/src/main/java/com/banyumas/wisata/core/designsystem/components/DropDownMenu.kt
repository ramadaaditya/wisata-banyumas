package com.banyumas.wisata.core.designsystem.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.banyumas.wisata.R
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    var isDropDownExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Alam", "Religi", "Kuliner", "Sejarah", "Keluarga")

    ExposedDropdownMenuBox(
        expanded = isDropDownExpanded,
        onExpandedChange = { isDropDownExpanded = it }
    ) {
        OutlinedTextField(
            value = selectedCategory.ifBlank { "Pilih Kategori" },
            onValueChange = {},
            readOnly = true, // ✅ Agar tidak bisa diketik langsung
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clickable { isDropDownExpanded = true }, // ✅ Klik untuk membuka dropdown
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_dropdown),
                    contentDescription = "Dropdown Icon"
                )
            }
        )

        DropdownMenu(
            expanded = isDropDownExpanded,
            onDismissRequest = { isDropDownExpanded = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(text = category) },
                    onClick = {
                        onCategorySelected(category)
                        isDropDownExpanded = false
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DropDownMenuPreview() {
    WisataBanyumasTheme {
        DropDownMenu(
            onCategorySelected = {},
            selectedCategory = "Alam"
        )
    }
}
