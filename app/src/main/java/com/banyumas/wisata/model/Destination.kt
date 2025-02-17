package com.banyumas.wisata.model

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
    val photos: List<Photo> = emptyList(),
    val reviewsFromGoogle: List<Review> = emptyList(),
    val reviewsFromLocal: List<Review> = emptyList(),
    val reviewEmbedding: MutableMap<String, Double>? = null,
    val lastReviewTimestamp: Long = 0L,
    val lastUpdated: Long = 0L,
    val facilities: List<Facility> = emptyList()
)

data class Review(
    val authorName: String = "",
    val rating: Int = 0,
    val text: String = "",
    val source: String = "",
    val timestamp: Long = 0L
)

data class Photo(
    val photoUrl: String = ""
)

data class UiDestination(
    val destination: Destination = Destination(),
    val isFavorite: Boolean = false
)

