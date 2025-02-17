package com.banyumas.wisata.view.review

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.viewmodel.DestinationViewModel
import com.banyumas.wisata.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun AddReviewScreen(
    destinationId: String,
    viewModel: DestinationViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    var rating by remember { mutableIntStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val authState by userViewModel.authState.collectAsStateWithLifecycle()
    val currentUser = (authState as? UiState.Success)?.data
    val userId = currentUser?.id.orEmpty()

    AddReviewContent(
        rating = rating,
        reviewText = reviewText,
        onRatingChange = { rating = it },
        onReviewTextChange = { reviewText = it },
        onSubmit = {
            if (rating > 0 && reviewText.isNotBlank()) {
                coroutineScope.launch {
                    runCatching {
                        viewModel.addLocalReview(
                            userId = userId,
                            destinationId = destinationId,
                            review = Review(
                                authorName = currentUser?.name.orEmpty(),
                                rating = rating,
                                text = reviewText,
                                source = "Local"
                            )
                        )
                    }.onSuccess {
                        Toast.makeText(context, "Review berhasil ditambahkan!", Toast.LENGTH_SHORT)
                            .show()
                        rating = 0
                        reviewText = ""
                    }.onFailure {
                        Toast.makeText(
                            context,
                            "Gagal menambahkan review: ${it.message}",
                            Toast.LENGTH_SHORT

                        ).show()
                    }
                }
            } else {
                Toast.makeText(context, "Harap isi rating dan ulasan!", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

@Composable
fun AddReviewContent(
    rating: Int,
    reviewText: String,
    onSubmit: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onReviewTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Tambahkan Review", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            (1..5).forEach { star ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Star $star",
                    tint = if (star <= rating) Color.Yellow else Color.Gray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onRatingChange(star) }
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = reviewText,
            onValueChange = onReviewTextChange,
            label = { Text("Tulis ulasan kamu...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 4
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomButton(
            onClick = onSubmit,
            text = "Kirim Review"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddReviewPreview() {
    AppTheme {
        AddReviewContent(
            rating = 3,
            reviewText = "Tempatnya bagus banget!",
            onRatingChange = {},
            onReviewTextChange = {},
            onSubmit = {}
        )
    }
}