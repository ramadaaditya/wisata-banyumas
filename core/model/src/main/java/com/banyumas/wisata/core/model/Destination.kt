package com.banyumas.wisata.core.model

enum class Facility {
    BATHROOM,
    PARKING,
    RESTAURANT,
    MOSQUE,
    REST_AREA,
    TICKET_COUNTER,
    GOOD_ACCESS
}

data class Destination(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val category: String = "",
    val latitude: Double? = 0.0,
    val longitude: Double? = 0.0,
    val rating: Float = 0.0f,
    val userRatingsTotal: Int = 0,
    val photos: List<Photo> = emptyList(),
    val openingHours: String? = null,
    val phoneNumber: String? = null,
    val reviews: List<Review> = emptyList(),
    val facilities: List<Facility> = emptyList()
)


data class Review(
    val authorName: String = "",
    val rating: Int = 0,
    val text: String = "",
    val timestamp: Long = 0L
)

data class Photo(
    val photoUrl: String = ""
)

data class UiDestination(
    val destination: Destination = Destination(),
    val isFavorite: Boolean = false
)

