package com.banyumas.wisata.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.R
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = query,
        colors = SearchBarDefaults.colors(
            containerColor = AppTheme.colorScheme.background,
        ),
        shadowElevation = 2.dp,
        onQueryChange = onQueryChange,
        onSearch = { onSearch(query) },
        active = false,
        onActiveChange = { },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.placeholder_search),
                style = AppTheme.typography.labelNormal
            )
        },
        shape = MaterialTheme.shapes.large,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {}
}

@Preview(showBackground = true)
@Composable
fun SearchPreview() {
    WisataBanyumasTheme {
        Search(
            query = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}