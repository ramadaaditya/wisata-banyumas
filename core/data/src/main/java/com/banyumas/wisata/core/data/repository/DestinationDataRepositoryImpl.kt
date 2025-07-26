package com.banyumas.wisata.core.data.repository

import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.R
import com.banyumas.wisata.core.data.retrofit.ApiService
import com.banyumas.wisata.core.data.utils.toDestination
import com.banyumas.wisata.core.data.utils.toSearchResult
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.SearchResultItem
import com.banyumas.wisata.core.model.UiDestination
import com.banyumas.wisata.core.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DestinationDataRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val firestore: FirebaseFirestore,
) : DestinationRepository {

    companion object {
        private const val DESTINATIONS_COLLECTION = "destinations"
        private const val USERS_COLLECTION = "users"
        private const val TAG = "DestinationRepository"
    }

    private suspend fun <T> safeCall(
        errorMessage: UiText,
        call: suspend () -> UiState<T>
    ): UiState<T> {
        return try {
            call()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            UiState.Error(errorMessage, e)
        }
    }

    override suspend fun getDestinationDetails(placeId: String): UiState<Destination> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_detail_place)
    ) {
        val result = apiService.getDetailPlaces(placeId).toDestination(placeId)
        UiState.Success(result)
    }

    override suspend fun searchDestinationByName(placeName: String): UiState<List<SearchResultItem>> =
        safeCall(
            errorMessage = UiText.StringResource(R.string.error_fetch_place)
        ) {
            val response = apiService.getDestinationByName(query = placeName)
            if (response.status == "OK" && response.candidates.isNotEmpty()) {
                UiState.Success(response.toSearchResult())
            } else {
                UiState.Empty
            }
        }

    override fun getAllDestinations(userId: String): Flow<UiState<List<UiDestination>>> {
        // 1. Buat Flow untuk mendengarkan perubahan pada koleksi 'destinations'
        val allDestinationsFlow: Flow<List<Destination>> =
            firestore.collection(DESTINATIONS_COLLECTION)
                .snapshots() // Ini adalah real-time listener yang diubah jadi Flow
                .map { snapshot -> snapshot.toObjects(Destination::class.java) }

        // 2. Buat Flow untuk mendengarkan perubahan pada data favorit user
        val favoriteIdsFlow: Flow<List<String>> =
            firestore.collection(USERS_COLLECTION).document(userId)
                .snapshots() // Real-time listener juga untuk dokumen user
                .map { snapshot ->
                    snapshot.toObject(User::class.java)?.favoriteDestinations.orEmpty()
                }

        // 3. Gabungkan (combine) kedua flow tersebut
        return combine(allDestinationsFlow, favoriteIdsFlow) { destinations, favoriteIds ->
            // Blok combine ini akan dieksekusi setiap kali salah satu flow (destinasi atau favorit) memancarkan data baru.
            val favoriteIdSet = favoriteIds.toSet()
            val uiDestinations = destinations.map { destination ->
                UiDestination(
                    destination = destination,
                    isFavorite = destination.id in favoriteIdSet
                )
            }

            // Ubah hasil kombinasi menjadi UiState
            if (uiDestinations.isEmpty()) {
                UiState.Empty as UiState<List<UiDestination>> // Cast agar tipe data sesuai
            } else {
                UiState.Success(uiDestinations)
            }
        }.catch { e ->
            // Tangani error jika salah satu flow gagal
            Timber.tag(TAG).e(e)
            emit(UiState.Error(UiText.StringResource(R.string.error_fetch_place), e))
        }
    }

    override suspend fun getDestinationById(
        destinationId: String,
        userId: String
    ): UiState<UiDestination> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_fetch_place)
    ) {
        coroutineScope {
            val destinationDeferred = async {
                firestore.collection(DESTINATIONS_COLLECTION).document(destinationId).get().await()
            }
            val userDeferred =
                async { firestore.collection(USERS_COLLECTION).document(userId).get().await() }

            val destination = destinationDeferred.await().toObject(Destination::class.java)
                ?: return@coroutineScope UiState.Error(UiText.StringResource(R.string.error_no_place_found))

            val favoriteIds =
                userDeferred.await().toObject(User::class.java)?.favoriteDestinations.orEmpty()

            UiState.Success(UiDestination(destination, favoriteIds.contains(destinationId)))
        }
    }

    override suspend fun getFavoriteDestinations(userId: String): UiState<List<Destination>> =
        safeCall(
            errorMessage = UiText.StringResource(R.string.error_fetch_favorites)
        ) {
            val userDoc = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val favoriteIds =
                userDoc.toObject(User::class.java)?.favoriteDestinations ?: emptyList()

            if (favoriteIds.isEmpty()) return@safeCall UiState.Empty

            val favorites = favoriteIds.chunked(10).flatMap { chunk ->
                firestore.collection(DESTINATIONS_COLLECTION)
                    .whereIn(FieldPath.documentId(), chunk)
                    .get().await()
                    .documents.mapNotNull { it.toObject(Destination::class.java) }
            }
            UiState.Success(favorites)
        }

    override suspend fun updateFavoriteStatus(
        userId: String,
        destinationId: String,
        shouldBeFavorite: Boolean
    ): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_update_favorites)
    ) {
        val operation = if (shouldBeFavorite) {
            FieldValue.arrayUnion(destinationId)
        } else {
            FieldValue.arrayRemove(destinationId)
        }
        firestore.collection(USERS_COLLECTION).document(userId)
            .update("favoriteDestinations", operation).await()
        UiState.Success(Unit)
    }

    override suspend fun saveDestination(destination: Destination): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_save_place)
    ) {
        val id = destination.id.ifBlank { UUID.randomUUID().toString() }
        firestore.collection(DESTINATIONS_COLLECTION)
            .document(id)
            .set(destination.copy(id = id), SetOptions.merge()).await()
        UiState.Success(Unit)
    }

    override suspend fun updateDestinationFields(
        destinationId: String,
        updatedFields: Map<String, Any>
    ): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_update_place)
    ) {
        firestore.collection(DESTINATIONS_COLLECTION).document(destinationId)
            .update(updatedFields).await()
        UiState.Success(Unit)
    }

    override suspend fun deleteDestination(destinationId: String): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_delete_place)
    ) {
        firestore.collection(DESTINATIONS_COLLECTION).document(destinationId).delete().await()
        UiState.Success(Unit)
    }

    override suspend fun fetchAndSaveDestination(placeId: String): UiState<Destination> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_fetch_place)
    ) {
        val result = apiService.getDetailPlaces(placeId).toDestination(placeId)
        when (val saveResult = saveDestination(result)) {
            is UiState.Success -> UiState.Success(result)
            is UiState.Error -> saveResult // Propagate error from save operation
            else -> UiState.Error(UiText.StringResource(R.string.error_save_place))
        }
    }
}
