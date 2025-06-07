package com.banyumas.wisata.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.UiDestination
import com.banyumas.wisata.core.data.repository.DestinationDataRepository
import com.banyumas.wisata.core.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val repository: DestinationDataRepository
) : ViewModel() {

    private val _uiDestinations = MutableStateFlow<UiState<List<UiDestination>>>(
        UiState.Empty)
    val uiDestinations: StateFlow<UiState<List<UiDestination>>> = _uiDestinations

    private val _selectedDestination = MutableStateFlow<UiState<UiDestination>>(
        UiState.Empty)
    val selectedDestination: StateFlow<UiState<UiDestination>> = _selectedDestination

    private val _favoriteDestination = MutableStateFlow<UiState<List<Destination>>>(
        UiState.Empty)
    val favoriteDestination: StateFlow<UiState<List<Destination>>> = _favoriteDestination

    private val _toggleFavoriteState = MutableStateFlow<UiState<Unit>>(
        UiState.Empty)
    val toggleFavoriteState: StateFlow<UiState<Unit>> = _toggleFavoriteState

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var allDestinations: List<UiDestination> = emptyList()

    sealed class DestinationEvent {
        data class ShowMessage(val message: String) : DestinationEvent()
        data object Success : DestinationEvent()
    }


//    fun uploadData(context: Context) {
//        viewModelScope.launch {
//            _favoriteDestination.value = UiState.Loading
//            try {
//                val placeIds = repository.readPlaceFromJson(context)
//                if (placeIds.isEmpty()) {
//                    _favoriteDestination.value = UiState.Empty
//                    return@launch
//                }
//
//                val updatedDestinations = mutableListOf<Destination>()
//
//                for (placeId in placeIds) {
//                    when (val result = repository.getDestinationById(placeId)) {
//                        is UiState.Success -> updatedDestinations.add(result.data)
//                        is UiState.Error -> {
//                            // Optional: log error or collect errors if needed
//                            continue // skip this placeId and continue
//                        }
//
//                        else -> continue
//                    }
//                }
//
//                _favoriteDestination.value = if (updatedDestinations.isEmpty()) {
//                    UiState.Empty
//                } else {
//                    UiState.Success(updatedDestinations)
//                }
//
//            } catch (e: Exception) {
//                _favoriteDestination.value = UiState.Error(
//                    UiText.StringResource(R.string.error_upload_data),
//                    e
//                )
//            }
//        }
//    }

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
                    UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
            }
        }
    }



    fun getDetailDestination(destinationId: String, userId: String) {
        viewModelScope.launch {
            _selectedDestination.value = UiState.Loading
            _selectedDestination.value = repository.getDestinationById(destinationId, userId)
        }
    }

    fun saveNewDestination(context: Context, destination: com.banyumas.wisata.core.model.Destination) {
        _uiDestinations.value = UiState.Loading
        viewModelScope.launch {
            when (val saveResult = repository.saveDestination(destination)) {
                is UiState.Success -> {
                    _uiDestinations.value = UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                }

                is UiState.Error -> {
                    _uiDestinations.value = saveResult
                    _eventFlow.emit(
                        DestinationEvent.ShowMessage(
                            saveResult.message.asString(
                                context
                            )
                        )
                    )
                }

                else -> {
                    _uiDestinations.value =
                        UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
                }
            }
        }
    }

    fun updateDestination(
        context: Context,
        destinationId: String,
        updatedFields: Map<String, Any>
    ) {
        _uiDestinations.value = UiState.Loading
        viewModelScope.launch {
            when (val result =
                repository.updateDestinationFields(destinationId, updatedFields)) {
                is UiState.Success -> {
                    allDestinations = allDestinations.map {
                        if (it.destination.id == destinationId) {
                            val updated = it.destination.updateWithFields(updatedFields)
                            it.copy(destination = updated)
                        } else it
                    }
                    _uiDestinations.value = UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                    _eventFlow.emit(DestinationEvent.ShowMessage("Destinasi berhasil diperbarui."))
                }

                is UiState.Error -> {
                    _uiDestinations.value = result
                    _eventFlow.emit(DestinationEvent.ShowMessage(result.message.asString(context)))
                }

                else -> {
                    _uiDestinations.value =
                        UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
                }
            }
        }
    }

//
//    fun searchDestinations(query: String, category: String?) {
//        val filtered = allDestinations.filter { destination ->
//            val matchesQuery =
//                query.isBlank() || destination.destination.name.contains(
//                    query,
//                    ignoreCase = true
//                )
//            val matchesCategory =
//                category == null || category.equals("Semua", ignoreCase = true) ||
//                        destination.destination.category.equals(category, ignoreCase = true)
//            matchesQuery && matchesCategory
//        }
//
//        _uiDestinations.value =
//            if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
//    }


}
