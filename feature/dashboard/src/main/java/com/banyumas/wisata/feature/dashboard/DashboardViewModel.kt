package com.banyumas.wisata.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.repository.DestinationDataRepository
import com.banyumas.wisata.core.model.UiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DestinationDataRepository,
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
            when (val result = repository.getAllDestinations(userId)) {
                is UiState.Success -> {
                    allDestinations = result.data
                    _uiDestinations.value = UiState.Success(allDestinations)
                    Timber.tag("VIEWMODEL")
                        .d("getAllDestinations: berhasil mengambil data wisata $result")
                }

                is UiState.Error -> _uiDestinations.value = result
                is UiState.Empty -> _uiDestinations.value = UiState.Empty
                else -> {
                    Timber.tag("VIEWMODEL").e("getAllDestinations: $result")
                    _uiDestinations.value =
                        UiState.Error(UiText.StringResource(R.string.error_else))
                }
            }
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

                is UiState.Error -> _uiDestinations.value = result
                else -> {
                    _eventFlow.emit(
                        DestinationEvent.ShowMessage(
                            UiText.StringResource(R.string.error_else)
                                .toString()
                        )
                    )
                }
            }
        }
    }

    fun searchDestinations(query: String, category: String?) {
        val filtered = allDestinations.filter { destination ->
            val matchesQuery =
                query.isBlank() || destination.destination.name.contains(query, ignoreCase = true)
            val matchesCategory = category == null || category.equals(
                "Semua",
                ignoreCase = true
            ) || destination.destination.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
        _uiDestinations.value = UiState.Success(filtered)
    }

    fun toggleFavorite(userId: String, destinationId: String, isCurrentlyFavorite: Boolean) {
        // 1. Lakukan update pada UI secara langsung (Optimistic Update)
        val currentList = (uiDestinations.value as? UiState.Success)?.data ?: return

        val newList = currentList.map {
            if (it.destination.id == destinationId) {
                it.copy(isFavorite = !isCurrentlyFavorite)
            } else {
                it
            }
        }
        _uiDestinations.value = UiState.Success(newList)
        // Juga update cache utama
        allDestinations = allDestinations.map {
            if (it.destination.id == destinationId) {
                it.copy(isFavorite = !isCurrentlyFavorite)
            } else {
                it
            }
        }

        // 2. Lakukan proses update ke repository di background
        viewModelScope.launch {
            val result =
                repository.updateFavoriteDestination(userId, destinationId, !isCurrentlyFavorite)

            // 3. Jika gagal, kembalikan state ke semula dan beri tahu user
            if (result is UiState.Error) {
                // Kembalikan state UI ke sebelum di-toggle
                _uiDestinations.value = UiState.Success(currentList)
                // Kembalikan cache utama juga
                allDestinations = allDestinations.map {
                    if (it.destination.id == destinationId) {
                        it.copy(isFavorite = isCurrentlyFavorite) // kembalikan ke nilai awal
                    } else {
                        it
                    }
                }
                _eventFlow.emit(
                    DestinationEvent.ShowMessage(
                        UiText.StringResource(R.string.error_update_favorite)
                            .toString()
                    )
                )
            }
        }
    }
}