package com.banyumas.wisata.feature.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.core.model.UiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class DetailScreenEvent {
    data class ShowMessage(val message: UiText) : DetailScreenEvent()
}

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: DestinationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _destinationState = MutableStateFlow<UiState<UiDestination>>(UiState.Empty)
    val destinationState: StateFlow<UiState<UiDestination>> = _destinationState.asStateFlow()

    // Gunakan requireNotNull untuk validasi yang aman dan pesan error yang jelas
    private val destinationId: String = requireNotNull(savedStateHandle["destinationId"]) {
        "Argumen 'destinationId' tidak ditemukan. Periksa pemanggilan navigasi."
    }

    private val _eventFlow = MutableSharedFlow<DetailScreenEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun loadDestinationDetail(userId: String) {
        viewModelScope.launch {
            _destinationState.value = UiState.Loading
            _destinationState.value = repository.getDestinationById(destinationId, userId)
        }
    }

    fun toggleFavorite(userId: String) {
        val currentState = (_destinationState.value as? UiState.Success)?.data ?: return
        val isCurrentlyFavorite = currentState.isFavorite
        val optimisticList = currentState.copy(isFavorite = !isCurrentlyFavorite)
        _destinationState.value = UiState.Success(optimisticList)

        viewModelScope.launch {
            val result =
                repository.updateFavoriteStatus(userId, destinationId, !isCurrentlyFavorite)

            if (result is UiState.Error) {
                _destinationState.value = UiState.Success(currentState)
                _eventFlow.emit(DetailScreenEvent.ShowMessage(result.message))
            }
        }
    }
}
