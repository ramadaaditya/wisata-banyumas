package com.banyumas.wisata.core.data.utils

import com.banyumas.wisata.core.data.retrofit.DetailResponse
import com.banyumas.wisata.core.data.retrofit.SearchResponse
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.Photo
import com.banyumas.wisata.core.model.Review
import com.banyumas.wisata.core.model.SearchResultItem

fun SearchResponse.toSearchResult(): List<SearchResultItem> {
    return this.candidates.orEmpty().mapNotNull { item ->
        item?.let {
            SearchResultItem(
                placeId = item.placeId.orEmpty(),
                name = item.name.orEmpty(),
                address = item.formattedAddress.orEmpty()
            )
        }
    }
}

fun DetailResponse.toDestination(placeId: String): Destination {
    val result = this.result ?: return Destination()
    return Destination(
        id = placeId,
        name = result.name.orEmpty(),
        address = result.formattedAddress.orEmpty(),
        category = result.types?.joinToString(", ") ?: "",
        latitude = result.geometry?.location?.lat,
        longitude = result.geometry?.location?.lng,
        rating = result.rating ?: 0.0f,
        userRatingsTotal = result.userRatingsTotal ?: 0,
        openingHours = result.openingHours?.weekdayText?.joinToString("\n"),
        reviews = result.reviews?.map { review ->
            Review(
                authorName = review?.authorName.orEmpty(),
                rating = review?.rating ?: 0,
                text = review?.text.orEmpty(),
            )
        } ?: emptyList(),
        photos = result.photos?.map { photo ->
            Photo(
                photoUrl = "${photo?.photoReference}"
            )
        } ?: emptyList(),
        facilities = emptyList()
    )
}