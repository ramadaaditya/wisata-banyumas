package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banyumas.wisata.BuildConfig
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Review
import com.banyumas.wisata.utils.EmptyState
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.viewmodel.FetchViewModel
@Composable
fun FetchScreen(viewModel: FetchViewModel = viewModel()) {
    val context = LocalContext.current
    val uiState by viewModel.destinations.collectAsStateWithLifecycle()
    var isButtonClicked by remember { mutableStateOf(false) }
    var currentPlaceId by remember { mutableStateOf("") } // Untuk menyimpan ID tempat
    var reviewText by remember { mutableStateOf("") }     // Untuk input teks review
    var reviewRating by remember { mutableFloatStateOf(3f) }   // Untuk input rating

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isButtonClicked) {
            Button(
                onClick = {
                    isButtonClicked = true
                    viewModel.uploadData(
                        context = context,
                        apiKey = BuildConfig.ApiKey,
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Upload Data")
            }
        } else {
            when (uiState) {
                is UiState.Loading -> LoadingState()

                is UiState.Success -> {
                    val destinations = (uiState as UiState.Success<List<Destination>>).data
                    if (destinations.isNotEmpty()) {
                        destinations.forEach { destination ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(text = "Name: ${destination.name}")
                                Button(
                                    onClick = { currentPlaceId = destination.id },
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text(text = "Add Review")
                                }
                            }
                        }
                    } else {
                        EmptyState()
                    }
                }

                is UiState.Error -> ErrorState(
                    message = (uiState as UiState.Error).message,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )

                UiState.Empty -> EmptyState()
            }

            // Jika tempat dipilih, tampilkan form review
            if (currentPlaceId.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text(text = "Add Review for Place: $currentPlaceId")

                    // Input teks review
                    TextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Review Text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )

                    // Input rating
                    Text(text = "Rating: ${reviewRating.toInt()}")
                    Slider(
                        value = reviewRating,
                        onValueChange = { reviewRating = it },
                        valueRange = 1f..5f,
                        steps = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )

                    // Tombol untuk menyimpan review
                    Button(
                        onClick = {
                            val review = Review(
                                authorName = "Joko", // Ganti dengan nama pengguna jika ada autentikasi
                                rating = reviewRating.toInt(),
                                text = reviewText
                            )
                            currentPlaceId = ""
                            reviewText = ""
                            reviewRating = 3f
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Submit Review")
                    }
                }
            }
        }
    }
}
