package com.banyumas.wisata.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.core.model.UiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DestinationRepository,
) : ViewModel() {
    private val _userId = MutableStateFlow<String?>(null)

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow<String?>("All")

    private val _allDestinations: Flow<UiState<List<UiDestination>>> = _userId.flatMapLatest { id ->
        if (id.isNullOrBlank()) {
            MutableStateFlow(UiState.Error(UiText.StringResource(R.string.error_user_id_empty)))
        } else {
            repository.getAllDestinations(id)
        }
    }

    val uiState: StateFlow<UiState<List<UiDestination>>> =
        combine(
            _allDestinations,
            _searchQuery,
            _selectedCategory
        ) { destinationState, query, category ->
            when (destinationState) {
                is UiState.Success -> {
                    val filteredList = destinationState.data.filter { uiDestination ->
                        val mathesQuery =
                            uiDestination.destination.name.contains(query, ignoreCase = true)
                        val mathesCategory =
                            category == "All" || uiDestination.destination.category.equals(
                                category, ignoreCase = true
                            )
                        mathesQuery && mathesCategory
                    }
                    if (filteredList.isEmpty()) UiState.Empty else UiState.Success(filteredList)
                }

                else -> destinationState
            }

        }.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = UiState.Loading
        )
//
//    fun loadInitialDestinations(userId: String) {
//        if (allDestinations.isNotEmpty()) return
//        if (userId.isBlank()) {
//            _uiDestinations.value =
//                UiState.Error(UiText.StringResource(R.string.error_user_id_empty))
//            return
//        }
//
//        _uiDestinations.value = UiState.Loading
//        viewModelScope.launch {
//            val result = repository.getAllDestinations(userId)
//            if (result is UiState.Success) {
//                allDestinations = result.data
//            }
//            _uiDestinations.value = result
//        }
//    }


    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun searchDestinations(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    // --- Event Channel ---
    private val _eventChannel = Channel<DestinationEvent>()
    val eventFlow: Flow<DestinationEvent> = _eventChannel.receiveAsFlow()

    sealed class DestinationEvent {
        data class ShowMessage(val message: UiText) : DestinationEvent()
        data object DeletionSuccess : DestinationEvent()
    }


    fun deleteDestinationById(destinationId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteDestination(destinationId)) {
                is UiState.Success -> {
                    _eventChannel.send(DestinationEvent.DeletionSuccess)
                    // Tidak perlu update state manual, karena `_allDestinations` akan diperbarui oleh repository
                    // dan `uiState` akan otomatis bereaksi. Cukup refresh data.
                    refreshDestinations()
                }

                is UiState.Error -> {
                    _eventChannel.send(DestinationEvent.ShowMessage(result.message))
                }

                else -> {}
            }
        }
    }

    fun toggleFavorite(destinationId: String, isCurrentlyFavorite: Boolean) {
        val currentUserId = _userId.value ?: return

        // Implementasi Optimistic Update yang lebih aman
        val currentState = uiState.value
        if (currentState !is UiState.Success) return

        // Simpan state lama untuk rollback jika gagal
        val originalList = currentState.data

        // Update UI secara optimis
        val optimisticList = originalList.map {
            if (it.destination.id == destinationId) it.copy(isFavorite = !isCurrentlyFavorite) else it
        }
        // Cara update state yang lebih modern dan aman dengan `update`
        // Ini tidak bisa langsung dilakukan karena uiState adalah `combine`.
        // Pendekatan optimistic update di sini menjadi lebih kompleks.
        // Untuk menjaga kesederhanaan, kita bisa memilih untuk tidak melakukan optimistic update
        // dan hanya mengandalkan refresh data setelah sukses.
        // Namun, jika ingin tetap optimis, kita perlu memodifikasi state `_allDestinations` secara manual.
        // Mari kita lakukan dengan cara yang lebih sederhana: panggil API lalu refresh.

        viewModelScope.launch {
            val result =
                repository.updateFavoriteStatus(currentUserId, destinationId, !isCurrentlyFavorite)
            if (result is UiState.Error) {
                _eventChannel.send(DestinationEvent.ShowMessage(result.message))
            } else {
                // Refresh data untuk mendapatkan status favorit terbaru
                refreshDestinations()
            }
        }
    }

    fun refreshDestinations() {
        // Memicu pemuatan ulang dengan cara memperbarui nilai _userId dengan nilainya saat ini.
        _userId.value = _userId.value
    }
}
