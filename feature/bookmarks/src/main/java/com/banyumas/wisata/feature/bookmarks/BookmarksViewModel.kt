package com.banyumas.wisata.feature.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.UiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {

    private val _uiDestinations = MutableStateFlow<UiState<List<UiDestination>>>(UiState.Empty)
    val uiDestinations: StateFlow<UiState<List<UiDestination>>> = _uiDestinations.asStateFlow()

    private var allDestinations: List<UiDestination> = emptyList()

    private val _favoriteDestination = MutableStateFlow<UiState<List<Destination>>>(UiState.Empty)
    val favoriteDestination: StateFlow<UiState<List<Destination>>> =
        _favoriteDestination.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    sealed class DestinationEvent {
        data class ShowMessage(val message: String) : DestinationEvent()
        data object Success : DestinationEvent()
    }

    fun loadFavoriteDestinations(userId: String) {
        _favoriteDestination.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getFavoriteDestinations(userId)) {
                is UiState.Success -> {
                    _favoriteDestination.value = result
                }

                UiState.Empty -> _favoriteDestination.value = UiState.Empty
                is UiState.Error -> _favoriteDestination.value = result
                else -> _favoriteDestination.value =
                    UiState.Error(UiText.DynamicString("Terjadi Kesalahan"))
            }
        }
    }

    fun toggleFavorite(userId: String, destinationId: String, isCurrentlyFavorite: Boolean) {
        val currentList = (uiDestinations.value as? UiState.Success)?.data ?: return

        val optimisticList = currentList.map {
            if (it.destination.id == destinationId) it.copy(isFavorite = !isCurrentlyFavorite) else it
        }
        _uiDestinations.value = UiState.Success(optimisticList)
        allDestinations = allDestinations.map {
            if (it.destination.id == destinationId) it.copy(isFavorite = !isCurrentlyFavorite) else it
        }

        viewModelScope.launch {
            val result =
                repository.updateFavoriteStatus(userId, destinationId, !isCurrentlyFavorite)

            if (result is UiState.Error) {
                _uiDestinations.value = UiState.Success(currentList)
                allDestinations = allDestinations.map {
                    if (it.destination.id == destinationId) it.copy(isFavorite = isCurrentlyFavorite) else it
                }
                _eventFlow.emit(DestinationEvent.ShowMessage(result.message.toString()))
            }
        }
    }
}