package com.banyumas.wisata.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Facility

@Composable
fun FacilityItem(drawableRes: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Card(
            modifier = Modifier.padding(4.dp),
            shape = RoundedCornerShape(CornerSize(8.dp)),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = "Facility Icon",
                modifier = Modifier.padding(16.dp)
            )
        }
        Text(text = label)
    }
}


@Composable
fun Facilities(
    modifier: Modifier = Modifier,
    availableFacility: List<Facility>
) {
    LazyRow(modifier = modifier) {
        availableFacility.forEach { facility ->
            item {
                when (facility) {
                    Facility.BATHROOM -> {
                        FacilityItem(drawableRes = R.drawable.bathroom, label = "Mandi")
                    }

                    Facility.PARKING -> {
                        FacilityItem(drawableRes = R.drawable.parking, label = "Parkir")
                    }

                    Facility.MOSQUE -> {
                        FacilityItem(drawableRes = R.drawable.mosque, label = "Musholla")
                    }

                    Facility.REST_AREA -> {
                        FacilityItem(drawableRes = R.drawable.rest_area, label = "Istirahat")
                    }

                    Facility.TICKET_COUNTER -> {
                        FacilityItem(drawableRes = R.drawable.ticket_counter, label = "Loket")
                    }

                    Facility.GOOD_ACCESS -> {
                        FacilityItem(drawableRes = R.drawable.good_access, label = "Akses Jalan")
                    }

                    Facility.RESTAURANT -> {
                        FacilityItem(drawableRes = R.drawable.restaurant, label = "Restaurant")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFacilities() {
    val availableFacility = listOf(
        Facility.MOSQUE,
        Facility.PARKING,
        Facility.RESTAURANT
    )
    Facilities(availableFacility = availableFacility)
}