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

    //Fetch data from Google Maps
    fun readPlaceFromJson(context: Context) = getPlaceIdFromJson(context)

    suspend fun fetchAndSavePlace(placeId: String, apiKey: String): Destination {
        try {
            val response = apiService.getDetailPlaces(placeId, key = apiKey, language = "id")
            val destination = response.toDestination(placeId, apiKey)
            saveOrUpdateDestination(destination)
            return destination
        } catch (e: Exception) {
            println("Error fetching/saving place $placeId: ${e.message}")
            throw e
        }
    }

    //Manage Destinations
    private suspend fun saveOrUpdateDestination(destination: Destination) {
        try {
            firestore.collection("destinations")
                .document(destination.id)
                .set(destination.copy(lastUpdated = System.currentTimeMillis()), SetOptions.merge())
                .await()

            Log.d(
                TAG,
                "saveOrUpdateDestination: Data destinasi ${destination.id} berhasil diperbarui"
            )
        } catch (e: Exception) {
            Log.e(TAG, "saveOrUpdateDestination: Gagal menyimpan atau memperbarui destinasi", e)
            throw e
        }
    }

    // Save local review separately
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
                    response.candidates.mapNotNull { it?.placeId } // 🔹 Mengabaikan `null`
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
                    null // 🔹 Jika satu gagal, tetap lanjutkan untuk yang lain
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


    fun updateDestinationWithPlaceId(
        oldId: String,
        newPlaceId: String,
        updatedDestination: Destination
    ) {
        firestore.collection("destinations")
            .document(oldId)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    TAG,
                    "updateDestinationWithPlaceId: Old Document deleted successfully"
                )
            }
            .addOnFailureListener { e ->
                Log.e(
                    TAG,
                    "updateDestinationWithPlaceId: failed to delete document",
                    e
                )
            }
        firestore.collection("destinations")
            .document(newPlaceId)
            .set(updatedDestination.copy(id = newPlaceId))
    }


    suspend fun getAllDestination(userId: String): List<UiDestination> {
        if (userId.isBlank()) {
            Log.e("DestinationRepository", "userId is blank, cannot fetch destinations")
            return emptyList()
        }

        return try {
            Log.d("DestinationRepository", "Fetching destinations for userId: $userId")

            val userDoc = firestore.collection("Users").document(userId).get().await()
            val favoriteIds =
                userDoc.toObject(User::class.java)?.favoriteDestinations ?: emptyList()

            Log.d("DestinationRepository", "Favorite IDs: $favoriteIds")

            val query = if (favoriteIds.isNotEmpty()) {
                firestore.collection("destinations").whereIn(FieldPath.documentId(), favoriteIds)
            } else {
                firestore.collection("destinations").orderBy("rating", Query.Direction.DESCENDING)
            }

            val destinations = query.get().await().documents.mapNotNull { document ->
                val destination = document.toObject(Destination::class.java)
                destination?.let {
                    UiDestination(destination = it, isFavorite = favoriteIds.contains(document.id))
                }
            }

            // 🔹 Jika data diambil dengan whereIn(), sorting dilakukan di aplikasi
            val sortedDestinations = destinations.sortedByDescending { it.destination.rating }

            Log.d("DestinationRepository", "Fetched ${sortedDestinations.size} destinations")
            sortedDestinations
        } catch (e: Exception) {
            Log.e("DestinationRepository", "getAllDestination: Error fetching destinations", e)
            emptyList()
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
            // Ambil dokumen pengguna dari Firestore
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: throw Exception("User with ID $userId not found")

            // Perbarui daftar destinasi favorit
            val updatedFavorites = if (isFavorite) {
                (user.favoriteDestinations) + destinationId
            } else {
                (user.favoriteDestinations) - destinationId
            }

            // Simpan perubahan ke Firestore
            firestore.collection("Users").document(userId)
                .update("favoriteDestinations", updatedFavorites)
                .await()
            // Berikan hasil sukses
        } catch (e: Exception) {
            // Tangani kesalahan dengan baik
            Log.e("UpdateFavorites", "Error updating favorite destinations: ${e.message}", e)
            throw e
        }
    }

    suspend fun addDestination(destination: Destination, uploadedPhotoUrls: List<String>) {
        try {
            Log.d(TAG, "addDestination: Mulai menambahkan destinasi ${destination.name}")

            // 🔹 Gunakan Place ID jika ada, jika tidak, buat ID baru
            val generatedId = destination.id.ifBlank { UUID.randomUUID().toString() }

            val allPhotos = destination.photos + uploadedPhotoUrls.map { Photo(photoUrl = it) }
            // 🔹 Simpan destinasi baru ke Firestore dengan URL foto yang diperbarui
            val newDestination = destination.copy(
                id = generatedId,
                photos = allPhotos,
                lastUpdated = System.currentTimeMillis()
            )

            firestore.collection("destinations")
                .document(generatedId)
                .set(newDestination, SetOptions.merge())
                .await()

            Log.d(
                TAG,
                "addDestination: Berhasil menambahkan destinasi ${destination.name} dengan foto ${uploadedPhotoUrls.size}"
            )
        } catch (e: Exception) {
            Log.e(TAG, "addDestination: Gagal menambahkan destinasi", e)
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

            uploadTasks.awaitAll().filterNotNull() // 🔥 Tunggu semua unggahan selesai
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

            // 🔹 Hapus foto lama jika ada dalam daftar deletedPhotos
            coroutineScope {
                deletedPhotos.forEach { photo ->
                    launch { deletePhoto(photo.photoUrl) }
                }
            }

            // 🔹 Unggah foto baru ke Firebase Storage
            val uploadedUrls = uploadPhotos(destination.id, newImageUris)

            // 🔥 Simpan foto yang tersisa + foto baru ke Firestore
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

    private suspend fun deletePhoto(photoUrl: String) {
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
                // 🔹 Cek apakah destinasi ada di daftar favorit user
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

            favoriteSnapshot.exists() // 🔹 Jika dokumen ada, berarti favorit
        } catch (e: Exception) {
            Log.e(TAG, "isDestinationFavorite: Gagal mengecek favorit $destinationId", e)
            false
        }
    }
}