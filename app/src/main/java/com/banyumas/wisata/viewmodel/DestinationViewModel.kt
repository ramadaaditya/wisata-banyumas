package com.banyumas.wisata.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.BuildConfig
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.model.Photo
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

    private val _eventFlow = MutableSharedFlow<Unit>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _searchResults = MutableStateFlow<UiState<List<UiDestination>>>(UiState.Loading)
    val searchResults: StateFlow<UiState<List<UiDestination>>> = _searchResults

    private var allDestinations: List<UiDestination> = emptyList()

    private val _uploadedPhotos = MutableStateFlow<List<Photo>>(emptyList())
    val uploadedPhotos: StateFlow<List<Photo>> = _uploadedPhotos

    fun uploadNewPhotos(imageUris: List<Uri>, destinationId: String) {
        viewModelScope.launch {
            val photoUrls = repository.uploadPhotos(destinationId, imageUris)
            if (photoUrls.isNotEmpty()) {
                _uploadedPhotos.value = photoUrls.map { Photo(photoUrl = it) }
            }
        }
    }

    fun loadDestinations(userId: String) {
        if (userId.isBlank()) {
            Log.d("DestinationViewModel", "loadDestinations called with empty userId")
            return // 🔥 Cegah pemanggilan dengan userId kosong
        }

        Log.d("DestinationViewModel", "Fetching destinations for userId: $userId")

        viewModelScope.launch {
            _uiDestinations.value = UiState.Loading // 🔥 Pastikan hanya diatur sekali

            try {
                val destinations = repository.getAllDestination(userId)
                Log.d("DestinationViewModel", "Destinations fetched: ${destinations.size}")
                allDestinations = destinations

                _uiDestinations.value =
                    if (destinations.isEmpty()) UiState.Empty else UiState.Success(destinations)

                _eventFlow.emit(Unit) // 🔥 Sinyalkan ke UI bahwa data sudah diperbarui
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Error fetching destinations: ${e.message}", e)
                _uiDestinations.value = UiState.Error(
                    message = "Gagal memuat destinasi: ${e.message}",
                    throwable = e
                )
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

    fun addDestination(destination: Destination, imageUris: List<Uri>) {
        viewModelScope.launch {
            _uiDestinations.value = UiState.Loading
            try {
                // 🔥 Unggah foto ke Firebase Storage
                val uploadedPhotoUrls = repository.uploadPhotos(destination.id, imageUris)

                // 🔹 Tambahkan destinasi ke Firestore dengan menyertakan foto dari Google Maps API
                repository.addDestination(destination, uploadedPhotoUrls)

                // 🔹 Gabungkan foto dari Google API dan foto yang diunggah pengguna
                val allPhotos = destination.photos + uploadedPhotoUrls.map { Photo(it) }

                val newDestination =
                    UiDestination(destination.copy(photos = allPhotos), isFavorite = false)
                allDestinations = allDestinations + newDestination
                _uiDestinations.value = UiState.Success(allDestinations)
                Log.d("DestinationViewModel", "Berhasil menambahkan destinasi")

                _eventFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Gagal menambahkan destinasi: ${e.message}", e)
                _uiDestinations.value = UiState.Error(
                    message = e.message ?: "Gagal menambahkan destinasi",
                    throwable = e
                )
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
                Log.d("DestinationViewModel", "Updating destination: $destination")
                repository.updateDestination(destination, newImageUris, deletedPhotos)

                allDestinations = allDestinations.map {
                    if (it.destination.id == destination.id) UiDestination(
                        destination,
                        isFavorite = it.isFavorite
                    )
                    else it
                }

                _uiDestinations.value = UiState.Success(allDestinations)
                Log.d("DestinationViewModel", "Successfully updated destination: $destination")
                _eventFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Failed to update destination: ${e.message}", e)
                _uiDestinations.value = UiState.Error(
                    message = e.message ?: "Gagal memperbarui destinasi",
                    throwable = e
                )
            }
        }
    }

    /** 🔹 Ambil destinasi berdasarkan ID */
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
                // Perbarui state favorit setelah berhasil mengubah
                (_selectedDestination.value as? UiState.Success)?.let { state ->
                    _selectedDestination.value = UiState.Success(
                        state.data.copy(isFavorite = isFavorite)
                    )
                }

                allDestinations = allDestinations.map {
                    if (it.destination.id == destinationId) it.copy(isFavorite = isFavorite)
                    else it
                }
                _uiDestinations.value = UiState.Success(allDestinations)

                _eventFlow.emit(Unit)
            } catch (e: Exception) {
                Log.e("DestinationViewModel", "Failed to toggle favorite: ${e.message}", e)
                _selectedDestination.value = UiState.Error(
                    message = "Failed to toggle favorite: ${e.message}"
                )
            }
        }
    }

    fun searchDestinationsByName(placeName: String) {
        viewModelScope.launch {
            _searchResults.value = UiState.Loading

            try {
                val destinations =
                    repository.searchAndFetchPlacesByName(placeName, BuildConfig.ApiKey)

                if (destinations.isNotEmpty()) {
                    val searchDestinations =
                        destinations.map { UiDestination(it, isFavorite = false) }
                    _searchResults.value = UiState.Success(searchDestinations)
                    Log.d(
                        "DestinationViewModel",
                        "Berhasil menemukan ${destinations.size} destinasi untuk '$placeName'"
                    )
                } else {
                    _searchResults.value = UiState.Empty
                    Log.w("DestinationViewModel", "Tidak ditemukan destinasi untuk '$placeName'")
                }

                _eventFlow.emit(Unit)

            } catch (e: Exception) {
                Log.e(
                    "DestinationViewModel",
                    "searchDestinationsByName: Error mencari destinasi: ${e.message}",
                    e
                )
                _searchResults.value = UiState.Error(
                    message = e.message ?: "Gagal mencari destinasi",
                    throwable = e
                )
            }
        }
    }

    fun fetchPlaceDetailsFromGoogle(placeId: String) {
        if (placeId.isBlank()) {
            Log.e("DestinationViewModel", "fetchPlaceDetailsFromGoogle: placeId kosong!")
            return
        }

        viewModelScope.launch {
            _selectedDestination.value = UiState.Loading
            try {
                val destination = repository.fetchPlaceDetails(placeId, BuildConfig.ApiKey)
                if (destination != null) {
                    _selectedDestination.value =
                        UiState.Success(UiDestination(destination, isFavorite = false))
                    Log.d(
                        "DestinationViewModel",
                        "fetchPlaceDetailsFromGoogle: Berhasil mendapatkan detail untuk $placeId"
                    )
                } else {
                    _selectedDestination.value = UiState.Empty
                    Log.w(
                        "DestinationViewModel",
                        "fetchPlaceDetailsFromGoogle: Tidak ditemukan data untuk $placeId"
                    )
                }
            } catch (e: Exception) {
                _selectedDestination.value = UiState.Error(
                    message = "Gagal mengambil detail wisata",
                    throwable = e
                )
                Log.e("DestinationViewModel", "fetchPlaceDetailsFromGoogle: ${e.message}", e)
            }
        }
    }
}