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
class DetailViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _destination =
        MutableStateFlow<UiState<UiDestination>>(UiState.Loading)
    val destination: StateFlow<UiState<UiDestination>> = _destination

    fun fetchDestinationById(destinationId: String?) {
        viewModelScope.launch {
            try {
                // Simulasi fetch data dari repository
                val destination = repository.getAllDestinationWIthFavorites("1")
                    .find { it.destination.id == destinationId }
                if (destination != null) {
                    _destination.value = UiState.Success(destination)
                } else {
                    _destination.value = UiState.Error("Destination not found")
                }
            } catch (e: Exception) {
                _destination.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

//    fun toggleFavorite(destinationId: String, isFavorite: Boolean) {
//        viewModelScope.launch {
//            val success = repository.toggleFavorite(destinationId, isFavorite)
//            if (success) {
//                (_destination.value as? UiState.Success)?.let { state ->
//                    _destination.value = UiState.Success(state.data.copy(isFavorite = isFavorite))
//                }
//            } else {
//                println("Failed to toggle favorite")
//            }
//        }
//    }
}