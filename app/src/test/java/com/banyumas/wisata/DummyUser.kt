//package com.banyumas.wisata
//
//import com.banyumas.wisata.model.Role
//import com.banyumas.wisata.model.User
//
//object DummyUser {
//    fun generateUser():User {
//        return User(
//            id = "user",
//            name = "Wisatawan Bule",
//            favoriteDestinations = java.util.List("Destinasi1", "Destinasi2"),
//            role = Role.USER,
//            hashedPassword = "ASodqioqwiu",
//            email = "Bule@gmail.com",
//
//        )
//    }
//
//    fun generateAdmin() : User {
//        return User(
//            id = "iniadmin",
//            name = ,
//            favoriteDestinations = ,
//            role = ,
//            hashedPassword = ,
//            email = ,
//        )
//    }
//}

package com.banyumas.wisata

import com.banyumas.wisata.core.model.Role
import com.banyumas.wisata.core.model.User

object DummyUser {
    fun generateUser(): com.banyumas.wisata.core.model.User {
        return com.banyumas.wisata.core.model.User(
            id = "user",
            name = "Wisatawan Bule",
            favoriteDestinations = listOf("Destinasi1", "Destinasi2"),
            role = com.banyumas.wisata.core.model.Role.USER,
            hashedPassword = "ASodqioqwiu",
            email = "Bule@gmail.com"
        )
    }

    fun generateAdmin(): com.banyumas.wisata.core.model.User {
        return com.banyumas.wisata.core.model.User(
            id = "iniadmin",
            name = "Admin Lokal",
            favoriteDestinations = emptyList(),
            role = com.banyumas.wisata.core.model.Role.ADMIN,
            hashedPassword = "admin123hashed",
            email = "admin@banyumas.go.id"
        )
    }
}
