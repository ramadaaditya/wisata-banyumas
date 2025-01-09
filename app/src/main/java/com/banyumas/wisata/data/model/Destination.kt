package com.banyumas.wisata.data.model


data class Destination(
    val id: String = "",
    val googlePlaceId: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val rating: Float = 0.0f,
    val photos: List<Photo> = emptyList(),
    val reviews: List<Review> = emptyList(),
)


data class Review(
    val authorName: String = "",
    val rating: Int = 0,
    val text: String = ""
)


data class Photo(
    val photoUrl: String = ""
)


data class UiDestination(
    val destination: Destination,
    val isFavorite: Boolean = false
)