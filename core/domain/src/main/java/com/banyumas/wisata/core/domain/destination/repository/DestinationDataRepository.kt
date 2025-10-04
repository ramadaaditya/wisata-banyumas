package com.banyumas.wisata.core.domain.destination.repository

import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.SearchResultItem
import com.banyumas.wisata.core.model.UiDestination
import kotlinx.coroutines.flow.Flow

interface DestinationRepository {

    suspend fun getDestinationDetails(placeId: String): UiState<Destination>

    suspend fun searchDestinationByName(placeName: String): UiState<List<SearchResultItem>>

    fun getAllDestinations(userId: String): Flow<UiState<List<UiDestination>>>

    suspend fun getDestinationById(destinationId: String, userId: String): UiState<UiDestination>

    suspend fun getFavoriteDestinations(userId: String): UiState<List<Destination>>

    suspend fun updateFavoriteStatus(
        userId: String,
        destinationId: String,
        shouldBeFavorite: Boolean
    ): UiState<Unit>

    suspend fun saveDestination(destination: Destination): UiState<Unit>

    suspend fun updateDestinationFields(
        destinationId: String,
        updatedFields: Map<String, Any>
    ): UiState<Unit>

    suspend fun deleteDestination(destinationId: String): UiState<Unit>

    suspend fun fetchAndSaveDestination(placeId: String): UiState<Destination>
}