package com.banyumas.wisata.feature.dashboard

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

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DestinationRepository,
) : ViewModel() {

    private val _uiDestinations = MutableStateFlow<UiState<List<UiDestination>>>(UiState.Empty)
    val uiDestinations: StateFlow<UiState<List<UiDestination>>> = _uiDestinations.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var allDestinations: List<UiDestination> = emptyList()

    sealed class DestinationEvent {
        data class ShowMessage(val message: String) : DestinationEvent()
        data object Success : DestinationEvent()
    }

    fun loadInitialDestinations(userId: String) {
        if (allDestinations.isNotEmpty()) return
        if (userId.isBlank()) {
            _uiDestinations.value =
                UiState.Error(UiText.StringResource(R.string.error_user_id_empty))
            return
        }

        _uiDestinations.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getAllDestinations(userId)
            if (result is UiState.Success) {
                allDestinations = result.data
            }
            _uiDestinations.value = result
        }
    }


    fun deleteDestinationById(destinationId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteDestination(destinationId)) {
                is UiState.Success -> {
                    allDestinations =
                        allDestinations.filterNot { it.destination.id == destinationId }
                    _uiDestinations.value = UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                }

                is UiState.Error -> {
                    _eventFlow.emit(DestinationEvent.ShowMessage(result.message.toString()))
                }

                else -> {}
            }
        }
    }

    fun searchDestinations(query: String, category: String?) {
        val filtered = allDestinations.filter { destination ->
            val matchesQuery =
                query.isBlank() || destination.destination.name.contains(query, ignoreCase = true)
            val matchesCategory =
                category == null || category == "All" || destination.destination.category.equals(
                    category,
                    ignoreCase = true
                )
            matchesQuery && matchesCategory
        }
        _uiDestinations.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
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
