package com.banyumas.wisata

import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.Facility
import com.banyumas.wisata.core.model.Photo
import com.banyumas.wisata.core.model.Review
import com.banyumas.wisata.core.model.SearchResultItem
import com.banyumas.wisata.core.model.UiDestination

val dummyDestination = com.banyumas.wisata.core.model.Destination(
    id = "dest001",
    name = "Curug Cipendok",
    address = "Kecamatan Cilongok, Banyumas",
    category = "Alam",
    latitude = -7.450000,
    longitude = 109.150000,
    rating = 4.5f,
    userRatingsTotal = 1234,
    photos = listOf(
        com.banyumas.wisata.core.model.Photo(photoUrl = "https://example.com/photo1.jpg"),
        com.banyumas.wisata.core.model.Photo(photoUrl = "https://example.com/photo2.jpg")
    ),
    openingHours = "08:00 - 17:00",
    phoneNumber = "+622812345678",
    reviews = listOf(
        com.banyumas.wisata.core.model.Review(
            authorName = "Andi",
            rating = 5,
            text = "Tempatnya sejuk dan indah!",
            timestamp = 1672531200L
        ),
        com.banyumas.wisata.core.model.Review(
            authorName = "Budi",
            rating = 4,
            text = "Cukup ramai saat libur tapi tetap nyaman.",
            timestamp = 1672617600L
        )
    ),
    facilities = listOf(
        com.banyumas.wisata.core.model.Facility.BATHROOM,
        com.banyumas.wisata.core.model.Facility.PARKING,
        com.banyumas.wisata.core.model.Facility.RESTAURANT,
        com.banyumas.wisata.core.model.Facility.REST_AREA
    )
)


val dummyDestinations = listOf(
    dummyDestination,
    com.banyumas.wisata.core.model.Destination(
        id = "dest002",
        name = "Taman Balai Kemambang",
        address = "Purwokerto Timur, Banyumas",
        category = "Taman Kota",
        latitude = -7.425000,
        longitude = 109.240000,
        rating = 4.3f,
        userRatingsTotal = 856,
        photos = listOf(com.banyumas.wisata.core.model.Photo(photoUrl = "https://example.com/photo3.jpg")),
        openingHours = "06:00 - 18:00",
        phoneNumber = "+622834567890",
        reviews = listOf(
            com.banyumas.wisata.core.model.Review(
                authorName = "Citra",
                rating = 4,
                text = "Cocok untuk piknik keluarga.",
                timestamp = 1672704000L
            )
        ),
        facilities = listOf(
            com.banyumas.wisata.core.model.Facility.PARKING,
            com.banyumas.wisata.core.model.Facility.GOOD_ACCESS,
            com.banyumas.wisata.core.model.Facility.MOSQUE
        )
    )
)


val dummySearchResult = com.banyumas.wisata.core.model.SearchResultItem(
    placeId = "oaiswfrouiwofi",
    name = "Curug Kapuas",
    address = "Baturraden"
)

val dummySearchResultItem = listOf(
    dummySearchResult,
    com.banyumas.wisata.core.model.SearchResultItem(
        placeId = "dest002",
        name = "Taman Balai Kemambang",
        address = "Purwokerto Timur, Banyumas",
    )
)

val dummyUiDestination = com.banyumas.wisata.core.model.UiDestination(
    dummyDestination,
    false
)

val listDummyDestination = dummyDestinations.map { destination ->
    com.banyumas.wisata.core.model.UiDestination(
        destination = destination,
        isFavorite = destination.id == "dest001"
    )
}


