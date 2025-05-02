@file:Suppress("UNCHECKED_CAST")

package com.banyumas.wisata.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.banyumas.wisata.BuildConfig
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.Facility
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.model.api.DetailResponse
import com.banyumas.wisata.model.api.SearchResponse
import org.json.JSONObject

fun getPlaceIdFromJson(context: Context): List<String> {
    val inputStream = context.resources.openRawResource(R.raw.place_ids)
    val json = inputStream.bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(json)
    val placeIds = mutableListOf<String>()
    val placeIdsArray = jsonObject.getJSONArray("place_ids")
    for (i in 0 until placeIdsArray.length()) {
        placeIds.add(placeIdsArray.getString(i))
    }
    return placeIds
}

//fun DetailResponse.toDestination(placeId: String): Destination {
//    val result = this.result ?: return Destination()
//    return Destination(
//        id = placeId,
//        name = result.name.orEmpty(),
//        address = result.formattedAddress.orEmpty(),
//        latitude = result.geometry?.location?.lat,
//        longitude = result.geometry?.location?.lng,
//        rating = result.rating?.toFloat() ?: 0.0f,
//        reviews = result.reviews?.map { review ->
//            Review(
//                authorName = review?.authorName.orEmpty(),
//                rating = review?.rating ?: 0,
//                text = review?.text.orEmpty(),
//            )
//        } ?: emptyList(),
//        photos = result.photos?.map { photo ->
//            Photo(
//                photoUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${photo?.photoReference}&key=${BuildConfig.ApiKey}"
//            )
//        } ?: emptyList(),
//    )
//}

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

fun generateNewId(): String {
    return System.currentTimeMillis().toString()
}

fun openGoogleMaps(context: Context, lat: Double?, long: Double?) {
    val uri = if (lat != null && long != null) {
        "geo:$lat,$long?q=$lat,$long".toUri()
    } else {
        "https://maps.google.com/".toUri()
    }
    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
    context.startActivity(mapIntent)
}

fun Destination.updateWithFields(fields: Map<String, Any>): Destination {
    return this.copy(
        name = fields["name"] as? String ?: name,
        address = fields["address"] as? String ?: address,
        category = fields["category"] as? String ?: category,
        latitude = (fields["latitude"] as? Double) ?: latitude,
        longitude = (fields["longitude"] as? Double) ?: longitude,
        rating = (fields["rating"] as? Number)?.toFloat() ?: rating,
        userRatingsTotal = (fields["userRatingsTotal"] as? Number)?.toInt() ?: userRatingsTotal,
        photos = fields["photos"] as? List<Photo> ?: photos,
        openingHours = fields["openingHours"] as? String ?: openingHours,
        phoneNumber = fields["phoneNumber"] as? String ?: phoneNumber,
        reviews = fields["reviews"] as? List<Review> ?: reviews,
        facilities = fields["facilities"] as? List<Facility> ?: facilities
    )
}


