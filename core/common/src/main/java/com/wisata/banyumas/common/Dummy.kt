package com.wisata.banyumas.common

import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.Facility
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.model.UiDestination

val dummyDestination = Destination(
    id = "dest001",
    name = "Curug Cipendok",
    address = "Kecamatan Cilongok, Banyumas",
    category = "Alam",
    latitude = -7.450000,
    longitude = 109.150000,
    rating = 4.5f,
    userRatingsTotal = 1234,
    photos = listOf(
        Photo(photoUrl = "https://example.com/photo1.jpg"),
        Photo(photoUrl = "https://example.com/photo2.jpg")
    ),
    openingHours = "08:00 - 17:00",
    phoneNumber = "+622812345678",
    reviews = listOf(
        Review(
            authorName = "Andi",
            rating = 5,
            text = "Tempatnya sejuk dan indah!",
            timestamp = 1672531200L
        ),
        Review(
            authorName = "Budi",
            rating = 4,
            text = "Cukup ramai saat libur tapi tetap nyaman.",
            timestamp = 1672617600L
        )
    ),
    facilities = listOf(
        Facility.BATHROOM,
        Facility.PARKING,
        Facility.RESTAURANT,
        Facility.REST_AREA
    )
)


val dummyDestinations = listOf(
    dummyDestination,
    Destination(
        id = "dest002",
        name = "Taman Balai Kemambang",
        address = "Purwokerto Timur, Banyumas",
        category = "Taman Kota",
        latitude = -7.425000,
        longitude = 109.240000,
        rating = 4.3f,
        userRatingsTotal = 856,
        photos = listOf(Photo(photoUrl = "https://example.com/photo3.jpg")),
        openingHours = "06:00 - 18:00",
        phoneNumber = "+622834567890",
        reviews = listOf(
            Review(
                authorName = "Citra",
                rating = 4,
                text = "Cocok untuk piknik keluarga.",
                timestamp = 1672704000L
            )
        ),
        facilities = listOf(
            Facility.PARKING,
            Facility.GOOD_ACCESS,
            Facility.MOSQUE
        )
    )
)


val dummySearchResult = SearchResultItem(
    placeId = "oaiswfrouiwofi",
    name = "Curug Kapuas",
    address = "Baturraden"
)

val dummySearchResultItem = listOf(
    dummySearchResult,
    SearchResultItem(
        placeId = "dest002",
        name = "Taman Balai Kemambang",
        address = "Purwokerto Timur, Banyumas",
    )
)

val dummyUiDestination = UiDestination(
    dummyDestination,
    false
)

val listDummyDestination = dummyDestinations.map { destination ->
    UiDestination(
        destination = destination,
        isFavorite = destination.id == "dest001"
    )
}


