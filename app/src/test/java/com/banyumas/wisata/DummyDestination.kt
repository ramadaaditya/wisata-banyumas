package com.banyumas.wisata

import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.Facility
import com.banyumas.wisata.core.model.Photo
import com.banyumas.wisata.core.model.Review
import com.banyumas.wisata.core.model.UiDestination

object DummyDestination {
    fun generateDummyDestination(): com.banyumas.wisata.core.model.Destination {
        return com.banyumas.wisata.core.model.Destination(
            id = "destinasi1",
            name = "Curug",
            address = "Jl. Wisata No. 123, Banyumas",
            category = "Alam",
            latitude = -7.4212,
            longitude = 109.2341,
            rating = 4.3f,
            userRatingsTotal = 124,
            photos = listOf(
                com.banyumas.wisata.core.model.Photo("https://example.com/photo1.jpg"),
                com.banyumas.wisata.core.model.Photo("https://example.com/photo2.jpg")
            ),
            openingHours = "08:00 - 17:00",
            phoneNumber = "08123456789",
            tags = listOf("air terjun", "alam", "sejuk"),
            reviews = listOf(
                com.banyumas.wisata.core.model.Review(
                    "Alice",
                    5,
                    "Tempatnya sangat indah!",
                    1678900000L
                ),
                com.banyumas.wisata.core.model.Review(
                    "Bob",
                    4,
                    "Recommended untuk liburan",
                    1678910000L
                )
            ),
            facilities = listOf(
                com.banyumas.wisata.core.model.Facility.BATHROOM,
                com.banyumas.wisata.core.model.Facility.PARKING,
                com.banyumas.wisata.core.model.Facility.REST_AREA,
                com.banyumas.wisata.core.model.Facility.GOOD_ACCESS
            )
        )
    }

    fun generateDummyUiDestination(): com.banyumas.wisata.core.model.UiDestination {
        return com.banyumas.wisata.core.model.UiDestination(
            destination = generateDummyDestination(),
            isFavorite = true
        )
    }

    fun generateListDummyDestination(): List<com.banyumas.wisata.core.model.Destination> {
        return List(5) { index ->
            generateDummyDestination().copy(
                id = "dest_$index",
                name = "Destinasi $index"
            )
        }
    }

//    fun generateListDummyUiDestination(): List<UiDestination> {
//        return List(5) { index ->
//            UiDestination(
//                destination = generateDummyDestination().copy(
//                    id = "dest_ui_$index",
//                    name = "Curug $index"
//                ),
//                isFavorite = index % 2 == 0 // Even indexes = favorite
//            )
//        }
//    }

    fun generateListDummyUiDestination(): List<com.banyumas.wisata.core.model.UiDestination> {
        return listOf(
            com.banyumas.wisata.core.model.UiDestination(
                destination = generateDummyDestination().copy(
                    id = "dest_1",
                    name = "Curug Cipendok",
                    category = "Alam"
                ),
                isFavorite = true
            ),
            com.banyumas.wisata.core.model.UiDestination(
                destination = generateDummyDestination().copy(
                    id = "dest_2",
                    name = "Curug Gomblang",
                    category = "Alam"
                ),
                isFavorite = false
            ),
            com.banyumas.wisata.core.model.UiDestination(
                destination = generateDummyDestination().copy(
                    id = "dest_3",
                    name = "Taman Rekreasi",
                    category = "Buatan"
                ),
                isFavorite = true
            )
        )
    }

}