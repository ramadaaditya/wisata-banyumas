package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banyumas.wisata.BuildConfig
import com.banyumas.wisata.view.theme.WisataBanyumasTheme
import com.banyumas.wisata.viewmodel.FetchViewModel

@Composable
fun FetchScreen(viewModel: FetchViewModel = viewModel()) {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                viewModel.uploadData(
                    context = context,
                    apiKey = BuildConfig.ApiKey,
                )
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Upload Data")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FetchDataPreview() {
    WisataBanyumasTheme {
        FetchScreen()
    }
}
