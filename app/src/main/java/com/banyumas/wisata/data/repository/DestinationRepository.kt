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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class DestinationRepository @Inject constructor(
    private val apiService: ApiService,
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "FirebaseUserRepository"
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
        val documentRef = firestore.collection("destinations").document(destination.id)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)

            if (snapshot.exists()) {
                // Merge new data with existing data
                val existingDestination = snapshot.toObject(Destination::class.java)
                val updatedDestination = existingDestination?.copy(
                    name = destination.name,
                    address = destination.address,
                    latitude = destination.latitude,
                    longitude = destination.longitude,
                    rating = destination.rating,
                    photos = destination.photos,
                    reviewsFromGoogle = destination.reviewsFromGoogle, // Replace Google reviews
                    lastUpdated = System.currentTimeMillis()
                ) ?: destination
                transaction.set(documentRef, updatedDestination)
            } else {
                // Create a new document
                transaction.set(
                    documentRef,
                    destination.copy(lastUpdated = System.currentTimeMillis())
                )
            }
        }.await()
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
                firestore.collection("destinations")
            }
            val destinations = query.get().await().documents.mapNotNull { document ->
                val destination = document.toObject(Destination::class.java)
                destination?.let {
                    UiDestination(
                        destination = it,
                        isFavorite = favoriteIds.contains(document.id)
                    )
                }
            }
            Log.d("DestinationRepository", "Fetched ${destinations.size} destinations")
            destinations
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

            firestore.collection("destinations")
                .whereIn("id", favoriteIds.toList()).get()
                .await().documents.mapNotNull { it.toObject(Destination::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "getFavoriteDestinations: Error fetching destinations : ", e)
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

    suspend fun addDestination(destination: Destination, imageUris: List<Uri>) {
        try {
            // 🔹 Jika tidak ada foto baru, gunakan foto lama
            val photoUrls = if (imageUris.isNotEmpty()) {
                val uploadedUrls = imageUris.mapNotNull { uri -> uploadPhoto(destination.id, uri) }
                if (uploadedUrls.isEmpty()) {
                    throw Exception("Gagal mengunggah foto, destinasi tidak dapat disimpan")
                }
                uploadedUrls
            } else {
                destination.photos.map { it.photoUrl } // Gunakan foto lama jika tidak ada yang baru
            }

            val newDestination = destination.copy(
                photos = photoUrls.map { Photo(photoUrl = it) },
                lastUpdated = System.currentTimeMillis()
            )

            // 🔹 Pastikan Firestore hanya diperbarui setelah upload selesai
            firestore.collection("destinations")
                .document(newDestination.id)
                .set(newDestination)
                .await()

            Log.d(TAG, "addDestination: Berhasil menambahkan destinasi")
        } catch (e: Exception) {
            Log.e(TAG, "addDestination: Gagal menambahkan destinasi", e)
            throw e
        }
    }

    private suspend fun uploadPhoto(destinationId: String, imageUri: Uri): String? {
        return try {
            val storageRef = FirebaseStorage.getInstance()
                .reference
                .child("destinations/$destinationId/photos/${UUID.randomUUID()}.jpg")

            Log.d(TAG, "uploadPhoto: Memulai upload foto untuk $imageUri")

            // 🔹 Pastikan file benar-benar diunggah sebelum mengambil URL
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            Log.d(TAG, "uploadPhoto: Berhasil mengunggah foto $downloadUrl")
            downloadUrl
        } catch (e: Exception) {
            Log.e(TAG, "uploadPhoto: Gagal mengunggah foto: ${e.message}", e)
            null // Jangan lempar exception, cukup kembalikan null
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
            deletedPhotos.forEach { photo -> deletePhoto(photo.photoUrl) }

            // 🔹 Upload foto baru hanya jika ada
            val uploadedUrls = newImageUris.mapNotNull { uri -> uploadPhoto(destination.id, uri) }

            // 🔹 Gabungkan foto lama (yang tidak dihapus) dengan yang baru diunggah
            val remainingPhotos = existingDestination.photos.filterNot { it in deletedPhotos }
            val updatedPhotos = remainingPhotos + uploadedUrls.map { Photo(photoUrl = it) }

            // 🔹 Persiapkan data yang hanya akan diperbarui
            val updatedData = mutableMapOf<String, Any>()

            if (destination.name != existingDestination.name) updatedData["name"] = destination.name
            if (destination.address != existingDestination.address) updatedData["address"] =
                destination.address
            if (destination.latitude != existingDestination.latitude) updatedData["latitude"] =
                destination.latitude!!
            if (destination.longitude != existingDestination.longitude) updatedData["longitude"] =
                destination.longitude!!
            if (updatedPhotos != existingDestination.photos) updatedData["photos"] = updatedPhotos

            if (updatedData.isNotEmpty()) {
                updatedData["lastUpdated"] = System.currentTimeMillis()
                documentRef.update(updatedData).await()
                Log.d(TAG, "updateDestination: Berhasil memperbarui destinasi")
            } else {
                Log.d(TAG, "updateDestination: Tidak ada perubahan")
            }

        } catch (e: Exception) {
            Log.e(TAG, "updateDestination: Gagal memperbarui destinasi", e)
            throw e
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