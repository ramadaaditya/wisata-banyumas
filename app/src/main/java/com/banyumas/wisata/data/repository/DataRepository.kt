package com.banyumas.wisata.data.repository

import android.content.Context
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.data.model.Review
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.data.model.User
import com.banyumas.wisata.data.remote.ApiService
import com.banyumas.wisata.utils.getPlaceIdFromJson
import com.banyumas.wisata.utils.toDestination
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DataRepository @Inject constructor(
    private val apiService: ApiService,
) {
    private val database: DatabaseReference =
        FirebaseDatabase.getInstance("https://admob-mobile-12fa9-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("destinations")
    private val dummyDestination = getDummyDestinations()
    private var dummyUsers = getDummyUsers()
    private val userFavorites = mutableMapOf<String, MutableList<String>>()

    fun readPlaceFromJson(context: Context) = getPlaceIdFromJson(context)

    private fun getDummyUsers(): List<User> {
        return listOf(
            User(
                id = "1",
                name = "budi",
                email = "budi@gmail.com",
                password = "budi123",
                favoriteDestinations = listOf("1", "2", "3")
            ),
            User(
                id = "2",
                name = "joko",
                email = "joko@gmail.com",
                password = "joko123",
                favoriteDestinations = listOf("4", "5", "6")
            )
        )
    }

    private fun getDummyDestinations(): List<Destination> {
        return listOf(
            Destination(
                id = "1",
                name = "Pantai Menganti",
                address = "Desa Menganti, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            ),
            Destination(
                id = "2",
                name = "Pantai Ayah",
                address = "Desa Ayah, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            ),
            Destination(
                id = "3",
                name = "Pantai Karangbolong",
                address = "Desa Karangbolong, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            ),
            Destination(
                id = "4",
                name = "Pantai Karangjahe",
                address = "Desa Karangjahe, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            ),
            Destination(
                id = "5",
                name = "Pantai Karanggandul",
                address = "Desa Karanggandul, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            ),
            Destination(
                id = "6",
                name = "Pantai Karangtengah",
                address = "Desa Karangtengah, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            ),
            Destination(
                id = "7",
                name = "Pantai Karangturi",
                address = "Desa Karangturi, Kecamatan Ayah, Kabupaten Kebumen",
                latitude = -7.7166,
                longitude = 109.6166,
                rating = 4.5f,
                photos = listOf(
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                    Photo("https://www.nativeindonesia.com/wp-content/uploads/2019/01/Pantai-Menganti.jpg"),
                ),
                reviews = listOf(
                    Review("Budi", 5, "Pantai yang sangat indah"),
                    Review("Joko", 4, "Pantai yang sangat indah"),
                    Review("Rudi", 3, "Pantai yang sangat indah"),
                )
            )
        )
    }

    //Mendapatkan semua destinasi dengan status favoritnya
    fun getAllDestinationWIthFavorites(userId: String): List<UiDestination> {
        val favoriteIds = userFavorites[userId] ?: emptyList()
        return dummyDestination.map { destination ->
            UiDestination(
                destination = destination,
                isFavorite = destination.id in favoriteIds
            )
        }
    }

    //Mendapatkan destinasi favorit saja
    fun getFavoriteDestinations(userId: String): List<Destination> {
        val favoriteIds = userFavorites[userId] ?: emptyList()
        return dummyDestination.filter { destination ->
            destination.id in favoriteIds
        }
    }

    //Menambahkan atau menghapus favorit
    fun updateFavoriteDestinations(userId: String, destinationId: String, isFavorite: Boolean) {
        val userIndex = dummyUsers.indexOfFirst { it.id == userId }
        if (userIndex == -1) throw IllegalStateException("User with ID $userId does not exist")
        val updatedFavorites = dummyUsers[userIndex].favoriteDestinations.toMutableList()
        if (isFavorite) {
            if (!updatedFavorites.contains(destinationId)) {
                updatedFavorites.add(destinationId)
            }
        } else {
            updatedFavorites.remove(destinationId)
        }

        // Ganti seluruh list dengan elemen yang diperbarui
        dummyUsers = dummyUsers.mapIndexed { index, user ->
            if (index == userIndex) {
                user.copy(favoriteDestinations = updatedFavorites)
            } else {
                user
            }
        }
    }

    fun getNearbyDestinations(userId: String): List<UiDestination> {
        // Contoh dummy data nearby
        val nearbyDestinations = dummyDestination.filter { it.id in listOf("4", "5", "6") }
        return nearbyDestinations.map { destination ->
            UiDestination(
                destination = destination,
                isFavorite = destination.id in userFavorites[userId].orEmpty()
            )
        }
    }

    //Fetch from api and save to database
    suspend fun fetchAndSavePlace(placeId: String, apiKey: String): Destination {
        try {
            val response = apiService.getDetailPlaces(placeId, key = apiKey)
            val destination = response.toDestination(placeId, apiKey)
            saveDestination(destination)
            return destination
        } catch (e: Exception) {
            println("Error fetching/saving place $placeId: ${e.message}")
            throw e
        }
    }

    private suspend fun saveDestination(destination: Destination) {
        // Save to database
        try {
            println("Saving destination ${destination.id}")
            database.child(destination.id).setValue(destination).await()
            println("Successfully saved destination ${destination.id}")
        } catch (e: Exception) {
            println("Error saving destination ${destination.id}: ${e.message}")
            throw e
        }
    }
//
//    suspend fun getDestinations(): List<Destination> {
//        return try {
//            val documents = db.collection("destinations").get().await()
//
//            documents.documents.forEach {
//                println("Document: ${it.id} => ${it.data}")
//            }
//
//            documents.documents.mapNotNull { document ->
//                document.toObject(Destination::class.java)
//            }
//        } catch (e: Exception) {
//            println("Error fetching destinations: ${e.message}")
//            emptyList()
//        }
//    }
//
//    suspend fun getDestinationById(destinationId: String): Destination? {
//        return try {
//            val document = db.collection("destinations").document(destinationId).get().await()
//            if (document.exists()) {
//                document.toObject(Destination::class.java)
//            } else {
//                null
//            }
//        } catch (e: Exception) {
//            println("Error fetching destination by ID $destinationId: ${e.message}")
//            null
//        }
//    }
//
//    suspend fun updateFavoriteDestinations(
//        userId: String,
//        destinationId: String,
//        isFavorite: Boolean
//    ) {
//        try {
//            val userDocRef = db.collection("users").document(userId)
//
//            db.runTransaction { transaction ->
//                // Tentukan tipe data secara eksplisit
//                val snapshot = transaction.get(userDocRef)
//                val user = snapshot.toObject(User::class.java)
//                    ?: throw IllegalStateException("User with ID $userId does not exist")
//
//                val currentFavorites = user.favoriteDestinations.toMutableList()
//
//                if (isFavorite) {
//                    currentFavorites.add(destinationId)
//                } else {
//                    currentFavorites.remove(destinationId)
//                }
//
//                transaction.update(userDocRef, "favoriteDestinations", currentFavorites)
//            }.await()
//        } catch (e: Exception) {
//            println("Error updating favorite destinations: ${e.message}")
//            throw e
//        }
//    }
//
//
//    suspend fun getFavoriteDestinations(userId: String): List<Destination> {
//        return try {
//            val userDoc = db.collection("users").document(userId).get().await()
//            val user =
//                userDoc.toObject(User::class.java) ?: throw IllegalStateException("User not found")
//            val favoriteIds = user.favoriteDestinations
//            if (favoriteIds.isNotEmpty()) {
//                val querySnapshot = db.collection("destinations")
//                    .whereIn("id", favoriteIds)
//                    .get()
//                    .await()
//                return querySnapshot.documents.mapNotNull { document ->
//                    document.toObject(Destination::class.java)
//                }
//            }
//            return emptyList()
//        } catch (e: Exception) {
//            println("Error fetching favorite destinations: ${e.message}")
//            emptyList()
//        }
//    }
//
//    suspend fun searchDestinations(query: String): List<Destination> {
//        return try {
//            val querySnapshot = db.collection("destinations")
//                .whereGreaterThanOrEqualTo("name", query)
//                .whereLessThanOrEqualTo("name", "$query\uF7FF")
//                .get()
//                .await()
//
//            querySnapshot.documents.mapNotNull { document ->
//                document.toObject(Destination::class.java)
//            }
//        } catch (e: Exception) {
//            println("Error searching destinations by name $query: ${e.message}")
//            emptyList()
//        }
//    }
//
//    suspend fun addDestination(destination: Destination) {
//        try {
//            db.collection("destinations").document(destination.id).set(destination).await()
//        } catch (e: Exception) {
//            println("Error adding destination ${destination.id}: ${e.message}")
//            throw e
//        }
//    }
//
//    suspend fun deleteDestination(destinationId: String) {
//        try {
//            db.collection("destinations").document(destinationId).delete().await()
//        } catch (e: Exception) {
//            println("Error deleting destination $destinationId: ${e.message}")
//            throw e
//        }
//    }
//
//    suspend fun addLocalReview(destinationId: String, review: String) {
//        try {
//            db.collection("destinations").document(destinationId)
//                .collection("reviews").add(mapOf("review" to review)).await()
//        } catch (e: Exception) {
//            println("Error adding review to destination $destinationId: ${e.message}")
//            throw e
//        }
//    }
//
//    suspend fun getLocalReviews(destinationId: String): List<String> {
//        return try {
//            val querySnapshot = db.collection("destinations").document(destinationId)
//                .collection("reviews").get().await()
//
//            querySnapshot.documents.mapNotNull { document ->
//                document.getString("review")
//            }
//        } catch (e: Exception) {
//            println("Error fetching reviews for destination $destinationId: ${e.message}")
//            emptyList()
//        }
//    }
//
//    suspend fun loginUser(email: String, password: String): Boolean {
//        return try {
//            val querySnapshot = db.collection("users")
//                .whereEqualTo("email", email)
//                .whereEqualTo("password", password)
//                .get()
//                .await()
//
//            querySnapshot.documents.isNotEmpty()
//        } catch (e: Exception) {
//            println("Error logging in user $email: ${e.message}")
//            false
//        }
//    }
//
//    suspend fun registerUser(email: String, password: String): Boolean {
//        return try {
//            db.collection("users").add(mapOf("email" to email, "password" to password)).await()
//            true
//        } catch (e: Exception) {
//            println("Error registering user $email: ${e.message}")
//            false
//        }
//    }
//
//    suspend fun forgotPassword(email: String): Boolean {
//        return try {
//            val querySnapshot = db.collection("users")
//                .whereEqualTo("email", email)
//                .get()
//                .await()
//
//            querySnapshot.documents.isNotEmpty()
//        } catch (e: Exception) {
//            println("Error resetting password for user $email: ${e.message}")
//            false
//        }
//    }
}