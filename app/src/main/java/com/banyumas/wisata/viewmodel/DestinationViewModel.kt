package com.banyumas.wisata.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.UiDestination
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DestinationViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {

    private val _uiDestinations = MutableStateFlow<com.banyumas.wisata.core.common.UiState<List<com.banyumas.wisata.core.model.UiDestination>>>(
        com.banyumas.wisata.core.common.UiState.Empty)
    val uiDestinations: StateFlow<com.banyumas.wisata.core.common.UiState<List<com.banyumas.wisata.core.model.UiDestination>>> = _uiDestinations

    private val _selectedDestination = MutableStateFlow<com.banyumas.wisata.core.common.UiState<com.banyumas.wisata.core.model.UiDestination>>(
        com.banyumas.wisata.core.common.UiState.Empty)
    val selectedDestination: StateFlow<com.banyumas.wisata.core.common.UiState<com.banyumas.wisata.core.model.UiDestination>> = _selectedDestination

    private val _favoriteDestination = MutableStateFlow<com.banyumas.wisata.core.common.UiState<List<com.banyumas.wisata.core.model.Destination>>>(
        com.banyumas.wisata.core.common.UiState.Empty)
    val favoriteDestination: StateFlow<com.banyumas.wisata.core.common.UiState<List<com.banyumas.wisata.core.model.Destination>>> = _favoriteDestination

    private val _toggleFavoriteState = MutableStateFlow<com.banyumas.wisata.core.common.UiState<Unit>>(
        com.banyumas.wisata.core.common.UiState.Empty)
    val toggleFavoriteState: StateFlow<com.banyumas.wisata.core.common.UiState<Unit>> = _toggleFavoriteState

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var allDestinations: List<com.banyumas.wisata.core.model.UiDestination> = emptyList()

    sealed class DestinationEvent {
        data class ShowMessage(val message: String) : DestinationEvent()
        data object Success : DestinationEvent()
    }

    fun getAllDestinations(userId: String) {
        if (userId.isBlank()) {
            _uiDestinations.value =
                com.banyumas.wisata.core.common.UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_user_id_empty))
            return
        }

        _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getAllDestinations(userId)) {
                is com.banyumas.wisata.core.common.UiState.Success -> {
                    allDestinations = result.data
                    _uiDestinations.value = if (result.data.isEmpty()) com.banyumas.wisata.core.common.UiState.Empty else result
                    Log.d("VIEWMODEL", "getAllDestinations: berhasil mengambil data wisata $result")
                }

                is com.banyumas.wisata.core.common.UiState.Error -> _uiDestinations.value = result
                else -> {
                    Log.e("VIEWMODEL", "getAllDestinations: $result")
                    _uiDestinations.value =
                        com.banyumas.wisata.core.common.UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
                }
            }
        }
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
        _favoriteDestination.value = com.banyumas.wisata.core.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getFavoriteDestinations(userId)) {
                is com.banyumas.wisata.core.common.UiState.Success -> {
                    _favoriteDestination.value = result
                }

                com.banyumas.wisata.core.common.UiState.Empty -> _favoriteDestination.value = com.banyumas.wisata.core.common.UiState.Empty
                is com.banyumas.wisata.core.common.UiState.Error -> _favoriteDestination.value = result
                else -> _favoriteDestination.value =
                    com.banyumas.wisata.core.common.UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
            }
        }
    }



    fun getDetailDestination(destinationId: String, userId: String) {
        viewModelScope.launch {
            _selectedDestination.value = com.banyumas.wisata.core.common.UiState.Loading
            _selectedDestination.value = repository.getDestinationById(destinationId, userId)
        }
    }

    fun saveNewDestination(context: Context, destination: com.banyumas.wisata.core.model.Destination) {
        _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Loading
        viewModelScope.launch {
            when (val saveResult = repository.saveDestination(destination)) {
                is com.banyumas.wisata.core.common.UiState.Success -> {
                    _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                }

                is com.banyumas.wisata.core.common.UiState.Error -> {
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
                        com.banyumas.wisata.core.common.UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
                }
            }
        }
    }

    fun deleteDestinationById(destinationId: String) {
        _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.deleteDestination(destinationId)) {
                is com.banyumas.wisata.core.common.UiState.Success -> {
                    allDestinations =
                        allDestinations.filterNot { it.destination.id == destinationId }
                    _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                }

                is com.banyumas.wisata.core.common.UiState.Error -> _uiDestinations.value = result
                else -> _uiDestinations.value =
                    com.banyumas.wisata.core.common.UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun updateDestination(
        context: Context,
        destinationId: String,
        updatedFields: Map<String, Any>
    ) {
        _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Loading
        viewModelScope.launch {
            when (val result =
                repository.updateDestinationFields(destinationId, updatedFields)) {
                is com.banyumas.wisata.core.common.UiState.Success -> {
                    allDestinations = allDestinations.map {
                        if (it.destination.id == destinationId) {
                            val updated = it.destination.updateWithFields(updatedFields)
                            it.copy(destination = updated)
                        } else it
                    }
                    _uiDestinations.value = com.banyumas.wisata.core.common.UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                    _eventFlow.emit(DestinationEvent.ShowMessage("Destinasi berhasil diperbarui."))
                }

                is com.banyumas.wisata.core.common.UiState.Error -> {
                    _uiDestinations.value = result
                    _eventFlow.emit(DestinationEvent.ShowMessage(result.message.asString(context)))
                }

                else -> {
                    _uiDestinations.value =
                        com.banyumas.wisata.core.common.UiState.Error(com.banyumas.wisata.core.common.UiText.StringResource(R.string.error_else))
                }
            }
        }
    }


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

    //TODO Perhatikan lagi fungsi dibawah
    fun searchDestinations(query: String, category: String?) {
        val filtered = allDestinations.filter { destination ->
            val matchesQuery =
                query.isBlank() || destination.destination.name.contains(
                    query,
                    ignoreCase = true
                )
            val matchesCategory =
                category == null || category.equals("Semua", ignoreCase = true) ||
                        destination.destination.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }

        _uiDestinations.value =
            if (filtered.isEmpty()) com.banyumas.wisata.core.common.UiState.Empty else com.banyumas.wisata.core.common.UiState.Success(filtered)
    }

    fun filterDestinations(category: String?) {
        searchDestinations("", category)
    }

    fun toggleFavorite(userId: String, destinationId: String, isFavorite: Boolean) {
        _toggleFavoriteState.value = com.banyumas.wisata.core.common.UiState.Loading
        viewModelScope.launch {
            when (val result =
                repository.updateFavoriteDestination(userId, destinationId, isFavorite)) {
                is com.banyumas.wisata.core.common.UiState.Success -> {
                    _toggleFavoriteState.value = com.banyumas.wisata.core.common.UiState.Success(Unit)
                    loadFavoriteDestinations(userId)
                }

                is com.banyumas.wisata.core.common.UiState.Error -> {
                    _toggleFavoriteState.value = com.banyumas.wisata.core.common.UiState.Error(result.message)
                }

                com.banyumas.wisata.core.common.UiState.Empty -> {
                    _toggleFavoriteState.value = com.banyumas.wisata.core.common.UiState.Empty
                }

                com.banyumas.wisata.core.common.UiState.Loading -> Unit
            }
        }
    }
}
