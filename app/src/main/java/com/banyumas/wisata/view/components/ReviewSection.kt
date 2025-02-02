package com.banyumas.wisata.view.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.data.model.Review
import com.banyumas.wisata.view.theme.AppTheme

@Composable
fun ReviewsSection(reviews: List<Review>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = "Ulasan",
            style = AppTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        reviews.forEach { review ->
            HorizontalDivider()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = review.authorName,
                        style = AppTheme.typography.body,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Yellow
                    )
                    Text(
                        text = "Rating: ${review.rating}",
                        style = AppTheme.typography.labelNormal
                    )
                }
                Text(
                    text = review.text,
                    style = AppTheme.typography.labelNormal,
                    color = AppTheme.colorScheme.secondary
                )
                if (review.source.isNotEmpty()) {
                    Text(
                        text = "Sumber: ${review.source}",
                        style = AppTheme.typography.labelSmall,
                        color = AppTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}
