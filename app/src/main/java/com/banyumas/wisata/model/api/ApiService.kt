package com.banyumas.wisata.model.api

import com.banyumas.wisata.BuildConfig
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("maps/api/place/details/json")
    suspend fun getDetailPlaces(
        @Query("place_id") placeId: String,
        @Query("fields") fields: String = "name,rating,formatted_address,geometry,photos,reviews,business_status,opening_hours,types,user_ratings_total",
        @Query("language") language: String = "id",
        @Query("key") key: String = BuildConfig.ApiKey
    ): DetailResponse

    @GET("maps/api/place/findplacefromtext/json")
    suspend fun getDestinationByName(
        @Query("input") query: String,
        @Query("inputtype") inputType: String = "textquery",
        @Query("language") language: String = "id",
        @Query("fields") fields: String = "place_id,name,formatted_address,business_status",
        @Query("key") key: String = BuildConfig.ApiKey
    ): SearchResponse
}


interface BackendService{
    @POST("login")
    suspend fun loginUser(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST
    suspend fun registerUser()

    @POST
    suspend fun registerAdmin()

    @POST
    suspend fun resetPassword()

    @GET
    suspend fun getAllDestination()

    @GET
    suspend fun getDetailDestination()
}