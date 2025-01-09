package com.banyumas.wisata.data.model

import com.banyumas.wisata.utils.Role

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val role: Role = Role.USER,
    val favoriteDestinations: List<String> = emptyList()
)
