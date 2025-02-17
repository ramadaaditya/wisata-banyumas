package com.banyumas.wisata.model.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.Photo
import com.banyumas.wisata.model.Review
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.api.ApiService
import com.banyumas.wisata.utils.EmbeddingLoader
import com.banyumas.wisata.utils.EmbeddingProcessor
import com.banyumas.wisata.utils.SimilarityCalculator
import com.banyumas.wisata.utils.getPlaceIdFromJson
import com.banyumas.wisata.utils.toDestination
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class DestinationRepository @Inject constructor(
    private val apiService: ApiService,
    private val firestore: FirebaseFirestore,
) {
    companion object {
        private const val TAG = "DestinationRepository"
    }

    private var embeddingMap: Map<String, FloatArray>? = null

    /**
     * **Memuat `embeddingMap` hanya sekali**
     */
    private fun loadEmbeddingMapIfNeeded(context: Context) {
        if (embeddingMap == null) {
            embeddingMap = EmbeddingLoader.loadEmbeddingMap(context)
        }
    }

    /**
     * **Hitung embedding review jika belum tersedia di Firestore**
     */
    suspend fun getReviewEmbedding(context: Context, destination: UiDestination): FloatArray {
        return withContext(Dispatchers.Default) {
            loadEmbeddingMapIfNeeded(context)

            // 🔥 Ambil embedding yang sudah ada di Firestore
            val cachedEmbedding = getDestinationEmbedding(destination.destination.id)

            // 🔥 Jika embedding sudah tersedia, langsung gunakan tanpa menghitung ulang
            if (cachedEmbedding.isNotEmpty()) {
                return@withContext cachedEmbedding
            }

            // 🔥 Jika tidak ada embedding yang tersimpan, hitung ulang
            val map = embeddingMap ?: return@withContext FloatArray(0)
            val computedEmbedding = EmbeddingProcessor.getDestinationEmbedding(destination, map)

            // 🔥 Simpan hasil embedding hanya jika hasilnya valid
            if (computedEmbedding.isNotEmpty()) {
                saveEmbeddingIfNeeded(destination.destination.id, computedEmbedding)
            }

            return@withContext computedEmbedding
        }
    }


    /**
     * **Cek apakah embedding sudah ada di Firestore dan update jika diperlukan**
     */
    suspend fun saveEmbeddingIfNeeded(destinationId: String, newEmbedding: FloatArray) {
        val docRef = firestore.collection("destinations").document(destinationId)
        val snapshot = docRef.get().await()

        val existingEmbedding = snapshot.get("reviewEmbedding") as? Map<*, *>
        val lastSavedTimestamp = snapshot.getLong("lastReviewTimestamp") ?: 0L

        val latestReviewTimestamp = System.currentTimeMillis()

        if (existingEmbedding != null && lastSavedTimestamp >= latestReviewTimestamp) {
            Log.d(
                TAG,
                "Embedding sudah ada dan terbaru, tidak perlu update untuk destinasi $destinationId"
            )
            return // 🔥 Jika embedding sudah ada dan up-to-date, tidak perlu menyimpan ulang
        }

        Log.d(TAG, "Menyimpan embedding baru untuk destinasi: $destinationId")

        val embeddingMap =
            newEmbedding.mapIndexed { index, value -> "dim$index" to value.toDouble() }.toMap()

        firestore.collection("destinations").document(destinationId)
            .set(
                mapOf(
                    "reviewEmbedding" to embeddingMap,
                    "lastReviewTimestamp" to latestReviewTimestamp
                ),
                SetOptions.merge()
            )
            .await()

        Log.d(TAG, "Embedding baru disimpan untuk destinasi $destinationId")
    }

    /**
     * **Mengambil embedding dari Firestore**
     */
    private suspend fun getDestinationEmbedding(destinationId: String): FloatArray {
        return try {
            val snapshot = firestore.collection("destinations")
                .document(destinationId)
                .get()
                .await()

            // 🔥 Ambil data dari Firestore
            val rawEmbedding = snapshot.get("reviewEmbedding")

            // 🔥 Periksa apakah data benar-benar Map<String, Number>
            if (rawEmbedding is Map<*, *>) {
                val embeddingMap = rawEmbedding.mapNotNull { (key, value) ->
                    if (key is String && value is Number) {
                        key to value.toFloat()
                    } else {
                        null
                    }
                }.toMap()

                // 🔥 Ubah ke FloatArray dengan indeks berurutan
                val maxIndex = embeddingMap.keys.mapNotNull { it.removePrefix("dim").toIntOrNull() }
                    .maxOrNull() ?: return FloatArray(0)

                FloatArray(maxIndex + 1) { index -> embeddingMap["dim$index"] ?: 0f }
            } else {
                FloatArray(0) // 🔥 Jika tidak sesuai format, return array kosong
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil embedding", e)
            FloatArray(0) // 🔥 Jangan return `null`, gunakan array kosong
        }
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

    /**
     * **Tambahkan review lokal dan perbarui embedding**
     */
    suspend fun addLocalReview(placeId: String, review: Review) {
        val documentRef = firestore.collection("destinations").document(placeId)

        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(documentRef)

            if (snapshot.exists()) {
                val existingDestination = snapshot.toObject(Destination::class.java)
                val updatedLocalReviews = existingDestination?.reviewsFromLocal.orEmpty() + review

                val updatedDestination =
                    existingDestination?.copy(reviewsFromLocal = updatedLocalReviews)

                if (updatedDestination != null) {
                    transaction.set(documentRef, updatedDestination)
                }
            }
        }.await()

//        // **Hitung ulang embedding setelah review baru ditambahkan**
//        val destination = getAllDestination(placeId).find { it.destination.id == placeId }
//        if (destination != null) {
//            val newEmbedding = getReviewEmbedding(context, destination)
//            saveEmbeddingIfNeeded(placeId, newEmbedding)
//        }
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
                .get()
                .await()

            destinationsSnapshot.documents.mapNotNull { document ->
                val destination = document.toObject(Destination::class.java)
                destination?.let {
                    UiDestination(destination = it, isFavorite = favoriteIds.contains(document.id))
                }
            }

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
            val userDoc = firestore.collection("Users").document(userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User not found")

            val updatedFavorites = if (isFavorite) {
                (user.favoriteDestinations + destinationId).distinct()
            } else {
                user.favoriteDestinations - destinationId
            }

            firestore.collection("Users").document(userId)
                .update("favoriteDestinations", updatedFavorites)
                .await()
        } catch (e: Exception) {
            Log.e("UpdateFavorites", "Error updating favorites: ${e.message}", e)
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

    // Logika Rekomendasi

    suspend fun getRecommendationsBasedOnFavorite(
        userId: String,
        favoriteDestinationId: String
    ): List<UiDestination> = coroutineScope {
        val allUiDestinations = getAllDestination(userId)

        val favDest = allUiDestinations.find { it.destination.id == favoriteDestinationId }
            ?: return@coroutineScope emptyList()

        val favVector = getDestinationEmbedding(favDest.destination.id)
        if (favVector.isEmpty()) {
            return@coroutineScope emptyList()
        }

        allUiDestinations
            .filterNot { it.destination.id == favoriteDestinationId }
            .map { dest ->
                async {
                    val vec = getDestinationEmbedding(dest.destination.id)
                    if (vec.isNotEmpty()) {
                        dest to SimilarityCalculator.cosineSimilarity(favVector, vec)
                    } else {
                        null
                    }
                }
            }
            .awaitAll() // 🔥 Tunggu hasil async agar tidak ada error coroutine
            .sortedByDescending { it!!.second }
            .take(5)
            .map { it!!.first }
    }


    suspend fun fixFirestoreFieldNames() {
        val firestore = FirebaseFirestore.getInstance()
        val destinationsRef = firestore.collection("destinations")

        val snapshot = destinationsRef.get().await()
        for (document in snapshot.documents) {
            val data = document.data ?: continue

            val newData = mutableMapOf<String, Any>()

            // Perbaiki nama field jika ditemukan
            data["review_embedding"]?.let {
                newData["reviewEmbedding"] = it // Ganti nama menjadi format yang benar
                firestore.collection("destinations").document(document.id)
                    .update("review_embedding", null) // Hapus field lama setelah diperbaiki
            }

            data["last_review_timestamp"]?.let {
                newData["lastReviewTimestamp"] = it
                firestore.collection("destinations").document(document.id)
                    .update("last_review_timestamp", null)
            }

            // Simpan perubahan ke Firestore
            if (newData.isNotEmpty()) {
                firestore.collection("destinations").document(document.id)
                    .update(newData)
                    .await()
                Log.d(
                    "FIRESTORE_FIX",
                    "Dokumen ${document.id} diperbarui dengan nama field yang benar."
                )
            }
        }
    }

    suspend fun removeOldFieldsFromFirestore() {
        val firestore = FirebaseFirestore.getInstance()
        val destinationsRef = firestore.collection("destinations")

        val snapshot = destinationsRef.get().await()
        for (document in snapshot.documents) {
            val documentRef = destinationsRef.document(document.id)

            // 🔥 Hapus field lama yang salah
            documentRef.update(
                mapOf(
                    "review_embedding" to null,  // Hapus field lama
                    "last_review_timestamp" to null // Hapus field lama
                )
            ).await()

            Log.d("FIRESTORE_CLEANUP", "Field lama dihapus dari dokumen ${document.id}")
        }
    }


}