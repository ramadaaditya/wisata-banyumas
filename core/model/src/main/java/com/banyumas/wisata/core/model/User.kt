package com.banyumas.wisata.core.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val hashedPassword: String = "",
    val role: Role = Role.USER,
    val favoriteDestinations: List<String> = emptyList()
)

enum class Role {
    USER,
    ADMIN
}



