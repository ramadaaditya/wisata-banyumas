package com.banyumas.wisata.model.api

import com.google.gson.annotations.SerializedName

data class DetailResponse(

    @field:SerializedName("result")
    val result: Result? = null,

    @field:SerializedName("html_attributions")
    val htmlAttributions: List<Any?>? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class Open(

    @field:SerializedName("time")
    val time: String? = null,

    @field:SerializedName("day")
    val day: Int? = null
)

data class ReviewsItem(

    @field:SerializedName("author_name")
    val authorName: String? = null,

    @field:SerializedName("profile_photo_url")
    val profilePhotoUrl: String? = null,

    @field:SerializedName("original_language")
    val originalLanguage: String? = null,

    @field:SerializedName("author_url")
    val authorUrl: String? = null,

    @field:SerializedName("rating")
    val rating: Int? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("time")
    val time: Int? = null,

    @field:SerializedName("translated")
    val translated: Boolean? = null,

    @field:SerializedName("relative_time_description")
    val relativeTimeDescription: String? = null
)

data class Close(

    @field:SerializedName("time")
    val time: String? = null,

    @field:SerializedName("day")
    val day: Int? = null
)

data class OpeningHours(

    @field:SerializedName("open_now")
    val openNow: Boolean? = null,

    @field:SerializedName("periods")
    val periods: List<PeriodsItem?>? = null,

    @field:SerializedName("weekday_text")
    val weekdayText: List<String?>? = null
)

data class Geometry(

    @field:SerializedName("viewport")
    val viewport: Viewport? = null,

    @field:SerializedName("location")
    val location: Location? = null
)

data class Southwest(

    @field:SerializedName("lng")
    val lng: Any? = null,

    @field:SerializedName("lat")
    val lat: Any? = null
)

data class Result(

    @field:SerializedName("formatted_address")
    val formattedAddress: String? = null,

    @field:SerializedName("types")
    val types: List<String?>? = null,

    @field:SerializedName("user_ratings_total")
    val userRatingsTotal: Int? = null,

    @field:SerializedName("business_status")
    val businessStatus: String? = null,

    @field:SerializedName("reviews")
    val reviews: List<ReviewsItem?>? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("opening_hours")
    val openingHours: OpeningHours? = null,

    @field:SerializedName("rating")
    val rating: Any? = null,

    @field:SerializedName("geometry")
    val geometry: Geometry? = null,

    @field:SerializedName("photos")
    val photos: List<PhotosItem?>? = null
)

data class PeriodsItem(

    @field:SerializedName("close")
    val close: Close? = null,

    @field:SerializedName("open")
    val open: Open? = null
)

data class PhotosItem(

    @field:SerializedName("photo_reference")
    val photoReference: String? = null,

    @field:SerializedName("width")
    val width: Int? = null,

    @field:SerializedName("html_attributions")
    val htmlAttributions: List<String?>? = null,

    @field:SerializedName("height")
    val height: Int? = null
)

data class Viewport(

    @field:SerializedName("southwest")
    val southwest: Southwest? = null,

    @field:SerializedName("northeast")
    val northeast: Northeast? = null
)

data class Northeast(

    @field:SerializedName("lng")
    val lng: Any? = null,

    @field:SerializedName("lat")
    val lat: Any? = null
)

data class Location(

    @field:SerializedName("lng")
    val lng: Any? = null,

    @field:SerializedName("lat")
    val lat: Any? = null
)
