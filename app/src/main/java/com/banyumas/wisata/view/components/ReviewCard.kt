package com.banyumas.wisata.view.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun ReviewCard(review: Review) {
    Card(
        border = BorderStroke(
            1.dp, MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(CornerSize(6.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = review.authorName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Rating(rating = review.rating)
            }
            Text(
                text = review.text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Composable
fun Rating(rating: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Rating",
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = rating.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview
@Composable
private fun ReviewCardPreview() {
    WisataBanyumasTheme(dynamicColor = false) {
        ReviewCard(
            review = Review(
                text = "Gelo king keren banget wisatanya",
                rating = 5,
                timestamp = 123123,
                authorName = "Bagas"
            )
        )
    }
}