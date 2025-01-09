package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.data.model.UiDestination
import com.banyumas.wisata.data.repository.DataRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    // State for destinations (for use in Compose)
    private val _destinations = MutableStateFlow<UiState<List<UiDestination>>>(UiState.Loading)
    val destinations: StateFlow<UiState<List<UiDestination>>> = _destinations

    private val _nearbyDestinations =
        MutableStateFlow<UiState<List<UiDestination>>>(UiState.Loading)
    val nearbyDestinations: StateFlow<UiState<List<UiDestination>>> = _nearbyDestinations


    init {
        loadDestinations("tono")
    }

    private fun loadDestinations(userId: String) {
        viewModelScope.launch {
            _destinations.value = UiState.Loading
            try {
                val uiDestination = repository.getAllDestinationWIthFavorites(userId)
                _destinations.value = UiState.Success(uiDestination)
            } catch (e: Exception) {
                _destinations.value = UiState.Error(e.message ?: "An error occurred")
            }
        }
    }

    //
//    fun searchDestinations(query: String) {
//        viewModelScope.launch {
//            _destinations.value = UiState.Loading
//            try {
//                val results = repository.searchDestinations(query)
//                _destinations.value = UiState.Success(results)
//            } catch (e: Exception) {
//                _destinations.value = UiState.Error(e.message ?: "An error occurred")
//            }
//        }
//    }
//
    fun toggleFavorite(userId: String, destinationId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateFavoriteDestinations(userId, destinationId, isFavorite)
                loadDestinations(userId)
            } catch (e: Exception) {
                println("Error toggling favorite: ${e.message}")
            }
        }
    }

//    fun loadNearbyDestinations(userId: String) {
//        viewModelScope.launch {
//            _nearbyDestinations.value = UiState.Loading
//            try {
//                val nearby = repository.getNearbyDestinations(userId)
//                _nearbyDestinations.value = UiState.Success(nearby)
//            } catch (e: Exception) {
//                _nearbyDestinations.value =
//                    UiState.Error(e.message ?: "Failed to load nearby places")
//            }
//        }
//    }
}