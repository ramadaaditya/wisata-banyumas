package com.banyumas.wisata.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.data.model.Review
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.data.model.User
import com.banyumas.wisata.data.remote.ApiService
import com.banyumas.wisata.utils.getPlaceIdFromJson
import com.banyumas.wisata.utils.toDestination
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class DestinationRepository @Inject constructor(
    private val apiService: ApiService,
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "DestinationRepository"
    }

    fun readPlaceFromJson(context: Context) = getPlaceIdFromJson(context)

    suspend fun savePlaceFromApi(placeId: String, apiKey: String): Destination {
        try {
            val response = apiService.getDetailPlaces(placeId, key = apiKey, language = "id")
            val destination = response.toDestination(placeId, apiKey)
            saveDestination(destination)
            return destination
        } catch (e: Exception) {
            println("Error fetching/saving place $placeId: ${e.message}")
            throw e
        }
    }

    suspend fun addLocalReview(placeId: String, review: Review) {
        val documentRef = firestore.collection("destinations").document(placeId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)

            if (snapshot.exists()) {
                val existingDestination = snapshot.toObject(Destination::class.java)
                val updatedLocalReviews = existingDestination?.reviewsFromLocal.orEmpty() + review

                val updatedDestination = existingDestination?.copy(
                    reviewsFromLocal = updatedLocalReviews
                )

                if (updatedDestination != null) {
                    transaction.set(documentRef, updatedDestination)
                }
            }
        }.await()
    }


    private suspend fun searchPlaceIdsByName(placeName: String, apiKey: String): List<String> {
        return try {
            val response = apiService.searchPlacesByName(query = placeName, key = apiKey)

            if (response.status == "OK" && !response.candidates.isNullOrEmpty()) {
                val placeIds =
                    response.candidates.mapNotNull { it?.placeId }
                Log.d(TAG, "searchPlaceIdsByName: Ditemukan ${placeIds.size} ID untuk '$placeName'")
                placeIds
            } else {
                Log.w(TAG, "searchPlaceIdsByName: Tidak ada hasil untuk '$placeName'")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "searchPlaceIdsByName: Error mencari tempat '$placeName'", e)
            emptyList()
        }
    }

    private suspend fun fetchPlacesDetailsByIds(
        placeIds: List<String>,
        apiKey: String
    ): List<Destination> {
        return try {
            placeIds.mapNotNull { placeId ->
                try {
                    val response =
                        apiService.getDetailPlaces(placeId, key = apiKey, language = "id")
                    response.toDestination(placeId, apiKey)
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "fetchPlacesDetailsByIds: Gagal mendapatkan detail untuk $placeId",
                        e
                    )
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "fetchPlacesDetailsByIds: Gagal mengambil data destinasi", e)
            emptyList()
        }
    }


    suspend fun fetchPlaceDetails(placeId: String, apiKey: String): Destination? {
        return try {
            val response = apiService.getDetailPlaces(placeId, key = apiKey, language = "id")
            val destination = response.toDestination(placeId, apiKey)

            Log.d(TAG, "fetchPlaceDetails: Berhasil mendapatkan detail untuk $placeId")
            destination
        } catch (e: Exception) {
            Log.e(TAG, "fetchPlaceDetails: Gagal mendapatkan detail untuk $placeId", e)
            null
        }
    }

    suspend fun searchAndFetchPlacesByName(placeName: String, apiKey: String): List<Destination> {
        val placeIds = searchPlaceIdsByName(placeName, apiKey)

        return if (placeIds.isNotEmpty()) {
            fetchPlacesDetailsByIds(placeIds, apiKey)
        } else {
            Log.e(TAG, "searchAndFetchPlacesByName: Tidak ditemukan ID untuk '$placeName'")
            emptyList()
        }
    }

    suspend fun getAllDestination(userId: String): List<UiDestination> {
        if (userId.isBlank()) {
            Log.e("DestinationRepository", "userId is blank, cannot fetch destinations")
            return emptyList()
        }

        return try {
            Log.d("DestinationRepository", "Fetching destinations for userId: $userId")

            // 🔹 Ambil daftar destinasi favorit user
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val favoriteIds =
                userDoc.toObject(User::class.java)?.favoriteDestinations ?: emptyList()

            Log.d("DestinationRepository", "Favorite IDs: $favoriteIds")

            // 🔹 Ambil semua destinasi
            val destinationsSnapshot = firestore.collection("destinations")
                .orderBy("rating", Query.Direction.DESCENDING)
                .get()
                .await()

            val destinations = destinationsSnapshot.documents.mapNotNull { document ->
                val destination = document.toObject(Destination::class.java)
                destination?.let {
                    UiDestination(destination = it, isFavorite = favoriteIds.contains(document.id))
                }
            }

            val sortedDestinations = destinations.sortedByDescending { it.destination.rating }

            Log.d("DestinationRepository", "Fetched ${sortedDestinations.size} destinations")
            sortedDestinations
        } catch (e: Exception) {
            Log.e("DestinationRepository", "getAllDestination: Error fetching destinations", e)
            emptyList() // 🛠️ Pastikan hanya ada satu return di akhir blok try-catch
        }
    }


    suspend fun getFavoriteDestinations(userId: String): List<Destination> {
        return try {
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val favoriteIds =
                userDoc.toObject(User::class.java)?.favoriteDestinations ?: emptyList()
            if (favoriteIds.isEmpty()) return emptyList()

            favoriteIds.chunked(10).flatMap { chunk ->
                firestore.collection("destinations")
                    .whereIn(FieldPath.documentId(), chunk)
                    .get().await()
                    .documents.mapNotNull { it.toObject(Destination::class.java) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "getFavoriteDestinations: Error", e)
            emptyList()
        }
    }

    suspend fun updateFavoriteDestinations(
        userId: String,
        destinationId: String,
        isFavorite: Boolean
    ) {
        try {
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User with ID $userId not found")

            val updatedFavorites = if (isFavorite) {
                (user.favoriteDestinations) + destinationId
            } else {
                (user.favoriteDestinations) - destinationId
            }

            firestore.collection("Users").document(userId)
                .update("favoriteDestinations", updatedFavorites)
                .await()
        } catch (e: Exception) {
            Log.e("UpdateFavorites", "Error updating favorite destinations: ${e.message}", e)
            throw e
        }
    }

    suspend fun saveDestination(destination: Destination, imageUris: List<Uri> = emptyList()) {
        try {
            Log.d(TAG, "saveDestination: Memproses destinasi ${destination.name}")

            val generatedId = destination.id.ifBlank { UUID.randomUUID().toString() }

            // 🔥 Langkah 1: Unggah foto dari perangkat admin ke Firebase Storage
            val uploadedPhotoUrls = if (imageUris.isNotEmpty()) {
                uploadPhotos(generatedId, imageUris)
            } else {
                emptyList()
            }

            // 🔥 Langkah 2: Gabungkan foto dari API dan yang diunggah
            val allPhotos = destination.photos + uploadedPhotoUrls.map { Photo(photoUrl = it) }

            // 🔥 Langkah 3: Simpan ke Firestore
            val newDestination = destination.copy(
                id = generatedId,
                photos = allPhotos,
                lastUpdated = System.currentTimeMillis()
            )

            firestore.collection("destinations")
                .document(generatedId)
                .set(newDestination, SetOptions.merge())
                .await()

            Log.d(TAG, "saveDestination: Berhasil menyimpan destinasi ${destination.name}")
        } catch (e: Exception) {
            Log.e(TAG, "saveDestination: Gagal menyimpan destinasi", e)
            throw e
        }
    }


    suspend fun uploadPhotos(destinationId: String, imageUris: List<Uri>): List<String> {
        return coroutineScope {
            val uploadTasks = imageUris.map { uri ->
                async {
                    try {
                        val storageRef = FirebaseStorage.getInstance()
                            .reference.child("destinations/$destinationId/photos/${UUID.randomUUID()}.jpg")

                        storageRef.putFile(uri).await()
                        val photoUrl = storageRef.downloadUrl.await().toString()

                        Log.d(TAG, "uploadPhoto: Berhasil mengunggah foto: $photoUrl")
                        photoUrl
                    } catch (e: Exception) {
                        Log.e(TAG, "uploadPhoto: Gagal mengunggah foto: ${e.message}", e)
                        null // 🔥 Jangan batalkan seluruh coroutine, cukup return null
                    }
                }
            }

            uploadTasks.awaitAll().filterNotNull() // 🔥 Hanya menyimpan URL yang berhasil diunggah
        }
    }


    suspend fun updateDestination(
        destination: Destination,
        newImageUris: List<Uri>,
        deletedPhotos: List<Photo>
    ) {
        try {
            val documentRef = firestore.collection("destinations").document(destination.id)
            val snapshot = documentRef.get().await()

            if (!snapshot.exists()) {
                Log.e(TAG, "updateDestination: Destinasi tidak ditemukan")
                return
            }

            val existingDestination = snapshot.toObject(Destination::class.java) ?: return

            coroutineScope {
                deletedPhotos.forEach { photo ->
                    launch { deletePhoto(photo.photoUrl) }
                }
            }

            val uploadedUrls = uploadPhotos(destination.id, newImageUris)

            val remainingPhotos = existingDestination.photos.filterNot { it in deletedPhotos }
            val updatedPhotos = remainingPhotos + uploadedUrls.map { Photo(photoUrl = it) }

            val updatedData = mapOf(
                "photos" to updatedPhotos,
                "lastUpdated" to System.currentTimeMillis()
            )

            documentRef.update(updatedData).await()
            Log.d(TAG, "updateDestination: Berhasil memperbarui destinasi dengan foto baru")
        } catch (e: Exception) {
            Log.e(TAG, "updateDestination: Gagal memperbarui destinasi", e)
        }
    }

    suspend fun deletePhoto(photoUrl: String) {
        if (!photoUrl.contains("firebasestorage.googleapis.com")) {
            Log.w(
                "FirebaseStorage",
                "URL bukan dari Firebase Storage, tidak perlu dihapus: $photoUrl"
            )
            return
        }

        try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl)
            storageRef.delete().await()
            Log.d("FirebaseStorage", "Foto berhasil dihapus: $photoUrl")
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Gagal menghapus foto: ${e.message}", e)
        }
    }


    suspend fun getDestinationById(destinationId: String, userId: String): UiDestination? {
        return try {
            val documentSnapshot = firestore.collection("destinations")
                .document(destinationId)
                .get()
                .await()

            if (!documentSnapshot.exists()) {
                Log.w(TAG, "getDestinationById: Data destinasi $destinationId tidak ditemukan")
                return null
            }

            val destination = documentSnapshot.toObject(Destination::class.java)
            if (destination != null) {
                val isFavorite = isDestinationFavorite(userId, destinationId)

                val uiDestination =
                    UiDestination(destination = destination, isFavorite = isFavorite)
                Log.d(TAG, "getDestinationById: Berhasil mengambil data destinasi $destinationId")

                return uiDestination
            } else {
                Log.w(TAG, "getDestinationById: Data destinasi null untuk ID $destinationId")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getDestinationById: Gagal mengambil data destinasi $destinationId", e)
            null
        }
    }

    private suspend fun isDestinationFavorite(userId: String, destinationId: String): Boolean {
        return try {
            val favoriteSnapshot = firestore.collection("users")
                .document(userId)
                .collection("favorites")
                .document(destinationId)
                .get()
                .await()

            favoriteSnapshot.exists()
        } catch (e: Exception) {
            Log.e(TAG, "isDestinationFavorite: Gagal mengecek favorit $destinationId", e)
            false
        }
    }

    suspend fun deleteDestination(destinationId: String) {
        try {
            val destinationRef = firestore.collection("destinations").document(destinationId)
            val snapshot = destinationRef.get().await()

            if (!snapshot.exists()) {
                Log.w(TAG, "deleteDestination: Destinasi dengan ID $destinationId tidak ditemukan.")
                throw Exception("Destinasi tidak ditemukan.")
            }

            val destination = snapshot.toObject(Destination::class.java)

            // 🔥 Periksa apakah foto berasal dari Firebase Storage sebelum dihapus
            destination?.photos?.forEach { photo ->
                deletePhoto(photo.photoUrl)
            }

            // 🔥 Hapus destinasi dari Firestore
            destinationRef.delete().await()
            Log.d(
                TAG,
                "deleteDestination: Destinasi $destinationId berhasil dihapus dari Firestore."
            )

        } catch (e: Exception) {
            Log.e(TAG, "deleteDestination: Gagal menghapus destinasi $destinationId", e)
            throw e
        }
    }
}