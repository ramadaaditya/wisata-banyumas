package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.repository.DestinationRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {
    private val _favoriteDestination = MutableStateFlow<UiState<List<Destination>>>(UiState.Loading)
    val favoriteDestination: StateFlow<UiState<List<Destination>>> = _favoriteDestination

    fun loadFavoriteDestinations(userId: String) {
        viewModelScope.launch {
            _favoriteDestination.value = UiState.Loading
            try {
                val favoriteData = repository.getFavoriteDestinations(userId)
                _favoriteDestination.value = if (favoriteData.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(favoriteData)
                }
            } catch (e: Exception) {
                _favoriteDestination.value = UiState.Error(
                    message = "Failed to load favorite destinations",
                    throwable = e
                )
            }
        }
    }

    fun toggleFavorite(userId: String, destinationId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            _favoriteDestination.value = UiState.Loading
            try {
                repository.updateFavoriteDestinations(userId, destinationId, isFavorite)
                loadFavoriteDestinations(userId) // Refresh data setelah berhasil diupdate
            } catch (e: Exception) {
                _favoriteDestination.value = UiState.Error(
                    message = "Failed to toggle favorite destination",
                    throwable = e
                )
            }
        }
    }
}