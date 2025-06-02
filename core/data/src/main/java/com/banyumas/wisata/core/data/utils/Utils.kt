package com.banyumas.wisata.core.data.utils

//fun buildPhotoUrl(photoReference: String): String {
//    return "https://maps.googleapis.com/maps/api/place/photo" +
//            "?maxwidth=400" +
//            "&photoreference=$photoReference" +
//            "&key=${BuildConfig.ApiKey}"
//}

//fun Destination.updateWithFields(fields: Map<String, Any>): Destination {
//    return this.copy(
//        name = fields["name"] as? String ?: name,
//        address = fields["address"] as? String ?: address,
//        category = fields["category"] as? String ?: category,
//        latitude = (fields["latitude"] as? Double) ?: latitude,
//        longitude = (fields["longitude"] as? Double) ?: longitude,
//        rating = (fields["rating"] as? Number)?.toFloat() ?: rating,
//        userRatingsTotal = (fields["userRatingsTotal"] as? Number)?.toInt() ?: userRatingsTotal,
//        photos = fields["photos"] as? List<Photo> ?: photos,
//        openingHours = fields["openingHours"] as? String ?: openingHours,
//        phoneNumber = fields["phoneNumber"] as? String ?: phoneNumber,
//        reviews = fields["reviews"] as? List<Review> ?: reviews,
//        facilities = fields["facilities"] as? List<Facility> ?: facilities
//    )
//}
//
//fun parsePlaceIdsFromJson(json: String): List<String> {
//    val jsonArray = JSONObject(json)
//    val placeIds = mutableListOf<String>()
//    val placeIdArray = jsonArray.getJSONArray("place_ids")
//    for (i in 0 until placeIdArray.length()) {
//        placeIds.add(placeIdArray.getString(i))
//    }
//    return placeIds
//}