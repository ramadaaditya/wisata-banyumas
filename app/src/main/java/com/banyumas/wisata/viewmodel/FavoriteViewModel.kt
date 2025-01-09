package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.repository.DataRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {
    private val _favoriteDestination = MutableStateFlow<UiState<List<Destination>>>(UiState.Loading)
    val favoriteDestination: StateFlow<UiState<List<Destination>>> = _favoriteDestination

    fun loadFavoriteDestinations(userId: String) {
        viewModelScope.launch {
            _favoriteDestination.value = UiState.Loading
            try {
                val favorites = repository.getFavoriteDestinations(userId)
                _favoriteDestination.value = UiState.Success(favorites)
            } catch (e: Exception) {
                _favoriteDestination.value =
                    UiState.Error(e.message ?: "Failed to load favorite destinations")
            }
        }
    }

    fun toggleFavorite(userId: String, destination: Destination, isFavorite: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateFavoriteDestinations(userId, destination.id, isFavorite)
                loadFavoriteDestinations(userId)
            } catch (e: Exception) {
                println("Error toggling favorite: ${e.message}")
            }
        }
    }
}