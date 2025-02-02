package com.banyumas.wisata.utils

import android.content.Context
import com.banyumas.wisata.R
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.data.model.Review
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.data.remote.DetailResponse
import org.json.JSONObject

fun getPlaceIdFromJson(context: Context): List<String> {
    // Baca File Json dari res/raw
    val inputStream = context.resources.openRawResource(R.raw.place_ids)
    val json = inputStream.bufferedReader().use { it.readText() }

    //Parsing JSON
    val jsonObject = JSONObject(json)
    val placeIds = mutableListOf<String>()
    val placeIdsArray = jsonObject.getJSONArray("place_ids")
    for (i in 0 until placeIdsArray.length()) {
        placeIds.add(placeIdsArray.getString(i))
    }
    return placeIds
}

fun DetailResponse.toDestination(placeId: String, apiKey: String): Destination {
    val result = this.result ?: return Destination()

    return Destination(
        id = placeId,
        name = result.name.orEmpty(),
        address = result.formattedAddress.orEmpty(),
        latitude = result.geometry?.location?.lat,
        longitude = result.geometry?.location?.lng,
        rating = result.rating?.toFloat() ?: 0.0f,
        reviewsFromGoogle = result.reviews?.map { review ->
            Review(
                authorName = review?.authorName.orEmpty(),
                rating = review?.rating ?: 0,
                text = review?.text.orEmpty(),
                source = "google" // Tambahkan sumber review
            )
        } ?: emptyList(),
        reviewsFromLocal = emptyList(), // Kosongkan karena review lokal hanya berasal dari aplikasi
        photos = result.photos?.map { photo ->
            Photo(
                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo?.photoReference}&key=$apiKey"
            )
        } ?: emptyList(),
        lastUpdated = System.currentTimeMillis() // Menandai waktu pembaruan
    )
}