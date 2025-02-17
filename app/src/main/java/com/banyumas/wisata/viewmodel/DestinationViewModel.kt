package com.banyumas.wisata.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.BuildConfig
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
import com.banyumas.wisata.data.model.Review
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.data.repository.DestinationRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {
    private val _uiDestinations = MutableStateFlow<UiState<List<UiDestination>>>(UiState.Loading)
    val uiDestinations: StateFlow<UiState<List<UiDestination>>> = _uiDestinations

    private val _selectedDestination = MutableStateFlow<UiState<UiDestination>>(UiState.Loading)
    val selectedDestination: StateFlow<UiState<UiDestination>> = _selectedDestination

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var allDestinations: List<UiDestination> = emptyList()

    sealed class DestinationEvent {
        data class ShowMessage(val message: String) : DestinationEvent()
        data object Success : DestinationEvent()
    }

    private fun handleError(tag: String, e: Exception, errorMessage: String): UiState.Error {
        Log.e(tag, "$errorMessage: ${e.message}", e)
        return UiState.Error(message = errorMessage, throwable = e)
    }

    fun loadDestinations(userId: String) {
        if (userId.isBlank()) {
            Log.w("DestinationViewModel", "UserId kosong, tidak bisa load destinasi")
            return
        }
        _uiDestinations.value = UiState.Loading
        viewModelScope.launch {
            try {
                val destinations = repository.getAllDestination(userId)
                allDestinations = destinations
                _uiDestinations.value =
                    if (destinations.isEmpty()) UiState.Empty else UiState.Success(destinations)
            } catch (e: Exception) {
                _uiDestinations.value =
                    handleError("DestinationViewModel", e, "Gagal memuat destinasi")
            }
        }
    }


    fun deleteDestination(destinationId: String) {
        viewModelScope.launch {
            _uiDestinations.value = UiState.Loading
            try {
                repository.deleteDestination(destinationId)
                allDestinations = allDestinations.filterNot { it.destination.id == destinationId }
                _uiDestinations.value = UiState.Success(allDestinations)
                _eventFlow.emit(DestinationEvent.Success)
            } catch (e: Exception) {
                _uiDestinations.value =
                    handleError("DstinationViewModel", e, "Gagal menghapus destinasi")
            }
        }
    }


    fun filterDestinations(query: String) {
        val filtered = if (query.isEmpty()) {
            allDestinations
        } else {
            allDestinations.filter {
                it.destination.name.contains(query, ignoreCase = true)
            }
        }
        _uiDestinations.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)

    }

    fun saveDestination(destination: Destination, imageUris: List<Uri>) {
        viewModelScope.launch {
            _uiDestinations.value = UiState.Loading
            try {
                val uploadedPhotoUrls = repository.uploadPhotos(destination.id, imageUris)
                val finalPhotos = destination.photos + uploadedPhotoUrls.map { Photo(it) }
                val updatedDestination = destination.copy(photos = finalPhotos)
                repository.saveDestination(updatedDestination)
                allDestinations =
                    allDestinations + UiDestination(updatedDestination, isFavorite = false)
                _uiDestinations.value = UiState.Success(allDestinations)
                _eventFlow.emit(DestinationEvent.Success)
                _eventFlow.emit(DestinationEvent.ShowMessage("Berhasil menyimpan destinasi !"))
            } catch (e: Exception) {
                _uiDestinations.value =
                    handleError("DestinationViewModel", e, "Gagal menyimpan destinasi")
            }
        }
    }


    fun updateDestination(
        destination: Destination,
        newImageUris: List<Uri>,
        deletedPhotos: List<Photo>
    ) {
        viewModelScope.launch {
            _uiDestinations.value = UiState.Loading
            try {
                deletedPhotos.forEach { repository.deletePhoto(it.photoUrl) }
                val uploadedPhotoUrls = repository.uploadPhotos(destination.id, newImageUris)
                val remainingPhotos = destination.photos - deletedPhotos.toSet()
                val finalPhotos = remainingPhotos + uploadedPhotoUrls.map { Photo(it) }
                val updatedDestination = destination.copy(photos = finalPhotos)

                repository.saveDestination(updatedDestination)

                allDestinations = allDestinations.map {
                    if (it.destination.id == destination.id) it.copy(destination = updatedDestination)
                    else it
                }

                _uiDestinations.value = UiState.Success(allDestinations)
                _eventFlow.emit(DestinationEvent.Success)
                _eventFlow.emit(DestinationEvent.ShowMessage("Destinasi berhasil diperbarui!"))
            } catch (e: Exception) {
                _uiDestinations.value =
                    handleError("DestinationViewModel", e, "Gagal memperbarui destinasi")
            }
        }
    }


    fun getDestinationById(destinationId: String, userId: String) {
        viewModelScope.launch {
            _selectedDestination.value = UiState.Loading
            try {
                val destination = repository.getDestinationById(destinationId, userId)
                if (destination != null) {
                    _selectedDestination.value = UiState.Success(destination)
                } else {
                    _selectedDestination.value = UiState.Empty
                    Log.w("DestinationViewModel", "Destination not found: $destinationId")
                }
            } catch (e: Exception) {
                _selectedDestination.value = UiState.Error(
                    message = e.message ?: "Gagal mengambil destinasi",
                    throwable = e
                )
                Log.e("DestinationViewModel", "Error fetching destination: ${e.message}", e)
            }
        }
    }

    fun toggleFavorite(userId: String, destinationId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateFavoriteDestinations(userId, destinationId, isFavorite)
                allDestinations = allDestinations.map {
                    if (it.destination.id == destinationId) it.copy(isFavorite = isFavorite)
                    else it
                }
                _uiDestinations.value = UiState.Success(allDestinations)
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Failed to toggle favorite: ${e.message}", e)
            }
        }
    }

    fun searchDestinationsByName(placeName: String) {
        viewModelScope.launch {
            _uiDestinations.value = UiState.Loading
            try {
                val destinations =
                    repository.searchAndFetchPlacesByName(placeName, BuildConfig.ApiKey)

                val newState = if (destinations.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(destinations.map { UiDestination(it, isFavorite = false) })
                }

                _uiDestinations.value = newState

            } catch (e: Exception) {
                _uiDestinations.value =
                    handleError("DestinationViewModel", e, "Gagal mencari destinasi")
            }
        }
    }


    fun fetchPlaceDetailsFromGoogle(placeId: String) {
        viewModelScope.launch {
            _selectedDestination.value = UiState.Loading
            try {
                val detailDestination = repository.fetchPlaceDetails(placeId, BuildConfig.ApiKey)
                if (detailDestination != null) {
                    _selectedDestination.value = UiState.Success(
                        UiDestination(detailDestination, isFavorite = false)
                    )
                } else {
                    _selectedDestination.value = UiState.Empty
                }
            } catch (e: Exception) {
                _selectedDestination.value = UiState.Error(
                    message = "Gagal mengambil detail wisata: ${e.message}",
                    throwable = e
                )
            }
        }
    }

    fun addLocalReview(
        userId: String?,
        destinationId: String,
        authorName: String,
        rating: Int,
        reviewText: String,
        source: String = ""
    ) {
        viewModelScope.launch {
            try {
                Log.d(
                    "DestinationViewModel",
                    "addReview() called with destinationId=$destinationId"
                )
                val newReview = Review(
                    authorName = authorName,
                    rating = rating,
                    text = reviewText,
                    source = source
                )

                repository.addLocalReview(destinationId, newReview)

                Log.d("DestinationViewModel", "addReview() success, fetching updated reviews...")

                val updatedUiDestination = repository.getDestinationById(
                    destinationId,
                    userId.orEmpty()
                )

                Log.d("DestinationViewModel", "addReview() finished updating state")
                if (updatedUiDestination != null) {
                    _selectedDestination.value = UiState.Success(updatedUiDestination)
                } else {
                    _selectedDestination.value = UiState.Empty
                }
                _eventFlow.emit(DestinationEvent.ShowMessage("Berhasil menambahkan review!"))
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Failed to add review: ${e.message}", e)
                _eventFlow.emit(DestinationEvent.ShowMessage("Gagal menambahkan review: ${e.message}"))
            }
        }
    }
}
