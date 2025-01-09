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
        id = "",
        name = result.name.orEmpty(),
        address = result.formattedAddress.orEmpty(),
        latitude = result.geometry?.location?.lat,
        longitude = result.geometry?.location?.lng,
        rating = (result.rating as? Double)?.toFloat() ?: 0.0f,
        reviews = result.reviews?.map {
            Review(
                authorName = it?.authorName.orEmpty(),
                rating = it?.rating ?: 0,
                text = it?.text.orEmpty()
            )
        } ?: emptyList(),
        photos = result.photos?.map {
            Photo(
                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${it?.photoReference}&key=$apiKey"
            )
        } ?: emptyList(),
        googlePlaceId = placeId
    )
}


fun mapToUiDestinations(
    userFavorites: List<String>,
    destinations: List<Destination>
): List<UiDestination> {
    return destinations.map { destination ->
        UiDestination(
            destination = destination,
            isFavorite = destination.id in userFavorites
        )
    }
}
