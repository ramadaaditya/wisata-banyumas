package com.banyumas.wisata.model.repository

import android.content.Context
import android.util.Log
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.api.ApiService
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import com.banyumas.wisata.utils.getPlaceIdFromJson
import com.banyumas.wisata.utils.toSearchResult
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class DestinationRepository @Inject constructor(
    private val apiService: ApiService,
    private val firestore: FirebaseFirestore,
) {
    companion object {
        private const val DESTINATIONS_COLLECTION = "destinations"
        private const val USERS_COLLECTION = "users"
    }

    //START of Initialize firebase
    fun readPlaceFromJson(context: Context) = getPlaceIdFromJson(context)
//
//    suspend fun getDestinationById(placeId: String): UiState<Destination?> {
//        return try {
//            val response = apiService.getDetailPlaces(placeId)
//            val destination = response.toDestination(placeId)
//            UiState.Success(destination)
//        } catch (e: Exception) {
//            UiState.Error(UiText.StringResource(R.string.error_save_place), e)
//        }
//    }

//    suspend fun getAllDestinationFromApi(
//        placeIds: List<String>,
//    ): List<Destination> {
//        return placeIds.mapNotNull { placeId ->
//            try {
//                val response = apiService.getDetailPlaces(placeId)
//                response.toDestination(placeId)
//            } catch (e: Exception) {
//                Log.e(
//                    "REPOSITORY",
//                    "getAllDestinationFromApi: Terdapat kesalahan dalam mendapatkan destinasi $placeId",
//                    e
//                )
//                null
//            }
//        }
//    }

//    suspend fun getDestinationDetails(placeId: String): UiState<Destination> {
//        return try {
//            val result = apiService.getDetailPlaces(placeId).toDestination(placeId)
//            UiState.Success(result)
//        } catch (e: Exception) {
//            UiState.Error(UiText.StringResource(R.string.error_detail_place), e)
//        }
//    }

    suspend fun searchDestinationByName(
        placeName: String,
    ): UiState<List<SearchResultItem>> {
        return try {
            val response = apiService.getDestinationByName(query = placeName)
            val candidates = response.takeIf { it.status == "OK" }?.candidates.orEmpty()
            if (candidates.isEmpty()) {
                UiState.Error(UiText.StringResource(R.string.error_no_place_found))
            } else {
                val result = response.toSearchResult()
                UiState.Success(result)
            }
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_fetch_place), e)
        }
    }
    //END of Initialize database


    suspend fun getAllDestinations(userId: String): UiState<List<UiDestination>> {
        if (userId.isBlank()) {
            return UiState.Error(UiText.StringResource(R.string.error_user_not_found))
        }
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val favoriteIds = userDoc.toObject(User::class.java)?.favoriteDestinations.orEmpty()
            val snapshot = firestore.collection(DESTINATIONS_COLLECTION).get().await()
            val destinations = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Destination::class.java)?.let {
                    UiDestination(destination = it, isFavorite = favoriteIds.contains(doc.id))
                }
            }
            UiState.Success(destinations)
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_fetch_place), e)
        }
    }

    suspend fun updateDestinationFields(
        destinationId: String,
        updatedFields: Map<String, Any>
    ): UiState<Unit> {
        return try {
            firestore.collection(DESTINATIONS_COLLECTION)
                .document(destinationId)
                .update(updatedFields)
                .await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_update_place), e)
        }
    }

    suspend fun getFavoriteDestinations(userId: String): UiState<List<Destination>> {
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val favoriteIds =
                userDoc.toObject(User::class.java)?.favoriteDestinations ?: emptyList()
            if (favoriteIds.isEmpty()) return UiState.Success(emptyList())

            val favorites = favoriteIds.chunked(10).flatMap { chunk ->
                firestore.collection(DESTINATIONS_COLLECTION)
                    .whereIn(FieldPath.documentId(), chunk)
                    .get().await()
                    .documents.mapNotNull { it.toObject(Destination::class.java) }
            }

            UiState.Success(favorites)
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_fetch_favorites), e)

        }
    }

    suspend fun updateFavoriteDestination(
        userId: String,
        destinationId: String,
        isFavorite: Boolean
    ): UiState<Unit> {
        return try {
            val userDoc = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = userDoc.toObject(User::class.java)
                ?: return UiState.Error(UiText.StringResource(R.string.error_user_not_found))

            val updatedFavorites = if (isFavorite) {
                (user.favoriteDestinations + destinationId).distinct()
            } else {
                user.favoriteDestinations - destinationId
            }

            firestore.collection(USERS_COLLECTION).document(userId)
                .update("favoriteDestinations", updatedFavorites)
                .await()

            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_update_favorites), e)
        }
    }

    suspend fun saveDestination(destination: Destination): UiState<Unit> {
        return try {
            val id = destination.id.ifBlank { UUID.randomUUID().toString() }
            firestore.collection(DESTINATIONS_COLLECTION)
                .document(id)
                .set(destination.copy(id = id), SetOptions.merge())
                .await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_save_place), e)
        }
    }


    suspend fun getDestinationById(destinationId: String, userId: String): UiState<UiDestination> {
        if (destinationId.isBlank() || userId.isBlank()) {
            return UiState.Error(UiText.StringResource(R.string.error_fields_required))
        }

        return try {
            val snapshot = firestore.collection(DESTINATIONS_COLLECTION)
                .document(destinationId).get().await()

            val destination = snapshot.toObject(Destination::class.java)
                ?: return UiState.Empty

            val userSnapshot = firestore.collection(USERS_COLLECTION)
                .document(userId).get().await()

            val isFavorite = userSnapshot.toObject(User::class.java)
                ?.favoriteDestinations?.contains(destinationId) == true

            UiState.Success(UiDestination(destination, isFavorite))
        } catch (e: Exception) {
            Log.e("Repository", "Error fetching destination", e)
            UiState.Error(UiText.StringResource(R.string.error_fetch_place), e)
        }
    }

    suspend fun deleteDestination(destinationId: String): UiState<Unit> {
        return try {
            val destinationRef = firestore.collection("destinations").document(destinationId)
            destinationRef.delete().await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            UiState.Error(UiText.StringResource(R.string.error_delete_place), e)
        }
    }
}