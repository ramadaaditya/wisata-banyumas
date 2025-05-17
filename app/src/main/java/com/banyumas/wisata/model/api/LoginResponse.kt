package com.banyumas.wisata.model.api

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("token")
    val token: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)