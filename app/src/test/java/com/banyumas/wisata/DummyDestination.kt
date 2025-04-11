package com.banyumas.wisata

import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.Facility
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.model.UiDestination

object DummyDestination {
    fun generateDummyDestination(): Destination {
        return Destination(
            id = "destinasi1",
            name = "Curug",
            address = "Jl. Wisata No. 123, Banyumas",
            category = "Alam",
            latitude = -7.4212,
            longitude = 109.2341,
            rating = 4.3f,
            userRatingsTotal = 124,
            photos = listOf(
                Photo("https://example.com/photo1.jpg"),
                Photo("https://example.com/photo2.jpg")
            ),
            openingHours = "08:00 - 17:00",
            phoneNumber = "08123456789",
            tags = listOf("air terjun", "alam", "sejuk"),
            reviews = listOf(
                Review("Alice", 5, "Tempatnya sangat indah!", 1678900000L),
                Review("Bob", 4, "Recommended untuk liburan", 1678910000L)
            ),
            facilities = listOf(
                Facility.BATHROOM,
                Facility.PARKING,
                Facility.REST_AREA,
                Facility.GOOD_ACCESS
            )
        )
    }

    fun generateDummyUiDestination(): UiDestination {
        return UiDestination(
            destination = generateDummyDestination(),
            isFavorite = true
        )
    }

    fun generateListDummyDestination(): List<Destination> {
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

    fun generateListDummyUiDestination(): List<UiDestination> {
        return listOf(
            UiDestination(
                destination = generateDummyDestination().copy(
                    id = "dest_1",
                    name = "Curug Cipendok",
                    category = "Alam"
                ),
                isFavorite = true
            ),
            UiDestination(
                destination = generateDummyDestination().copy(
                    id = "dest_2",
                    name = "Curug Gomblang",
                    category = "Alam"
                ),
                isFavorite = false
            ),
            UiDestination(
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