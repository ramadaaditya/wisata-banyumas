package com.banyumas.wisata.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.UiDestination
import com.banyumas.wisata.model.repository.DestinationRepository
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import com.banyumas.wisata.utils.updateWithFields
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

    private val _uiDestinations = MutableStateFlow<UiState<List<UiDestination>>>(UiState.Empty)
    val uiDestinations: StateFlow<UiState<List<UiDestination>>> = _uiDestinations

    private val _selectedDestination = MutableStateFlow<UiState<UiDestination>>(UiState.Empty)
    val selectedDestination: StateFlow<UiState<UiDestination>> = _selectedDestination

    private val _eventFlow = MutableSharedFlow<DestinationEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private var allDestinations: List<UiDestination> = emptyList()

    sealed class DestinationEvent {
        data class ShowMessage(val message: String) : DestinationEvent()
        data object Success : DestinationEvent()
    }

    fun loadDestinations(userId: String) {
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
                    _uiDestinations.value = if (result.data.isEmpty()) UiState.Empty else result
                }

                is UiState.Error -> _uiDestinations.value = result
                else -> _uiDestinations.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun getDestinationById(destinationId: String, userId: String) {
        _selectedDestination.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getDestinationById(destinationId, userId)) {
                is UiState.Success -> _selectedDestination.value = result
                is UiState.Error -> _selectedDestination.value = result
                else -> _selectedDestination.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun saveNewDestination(context: Context, destination: Destination) {
        _uiDestinations.value = UiState.Loading
        viewModelScope.launch {
            when (val saveResult = repository.saveDestination(destination)) {
                is UiState.Success -> {
                    _uiDestinations.value = UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                }

                is UiState.Error -> {
                    _uiDestinations.value = saveResult
                    _eventFlow.emit(DestinationEvent.ShowMessage(saveResult.message.asString(context)))
                }

                else -> {
                    _uiDestinations.value =
                        UiState.Error(UiText.StringResource(R.string.error_else))
                }
            }
        }
    }

    fun deleteDestinationById(destinationId: String) {
        _uiDestinations.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.deleteDestination(destinationId)) {
                is UiState.Success -> {
                    allDestinations =
                        allDestinations.filterNot { it.destination.id == destinationId }
                    _uiDestinations.value = UiState.Success(allDestinations)
                    _eventFlow.emit(DestinationEvent.Success)
                }

                is UiState.Error -> _uiDestinations.value = result
                else -> _uiDestinations.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
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
                        UiState.Error(UiText.StringResource(R.string.error_else))
                }
            }
        }
    }


    fun searchDestinations(query: String, category: String?) {
        val filtered = allDestinations.filter { destination ->
            val matchesQuery =
                query.isBlank() || destination.destination.name.contains(query, ignoreCase = true)
            val matchesCategory = category == null || category.equals("Semua", ignoreCase = true) ||
                    destination.destination.category.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }

        _uiDestinations.value = if (filtered.isEmpty()) UiState.Empty else UiState.Success(filtered)
    }

    fun filterDestinations(category: String?) {
        searchDestinations("", category)
    }
}
