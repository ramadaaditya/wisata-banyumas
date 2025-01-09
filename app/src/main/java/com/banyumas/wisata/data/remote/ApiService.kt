package com.banyumas.wisata.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("maps/api/place/details/json")
    suspend fun getDetailPlaces(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,formatted_address,geometry,photos,reviews",
        @Query("key") key: String
    ): DetailResponse
}