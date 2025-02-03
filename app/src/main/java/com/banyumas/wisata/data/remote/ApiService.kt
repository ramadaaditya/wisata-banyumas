package com.banyumas.wisata.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("maps/api/place/details/json")
    suspend fun getDetailPlaces(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,formatted_address,geometry,photos,reviews",
        @Query("language") language: String = "id",
        @Query("key") key: String
    ): DetailResponse

    @GET("maps/api/place/findplacefromtext/json")
    suspend fun searchPlacesByName(
        @Query("input") query: String, // Nama tempat yang dicari
        @Query("inputtype") inputType: String = "textquery",
        @Query("language") language: String = "id",
        @Query("fields") fields: String = "place_id",
        @Query("key") key: String
    ): SearchResponse
}