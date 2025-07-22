package com.banyumas.wisata.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.core.model.Destination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val repository: DestinationRepository,
) : ViewModel() {
//    private val _destinationState = MutableStateFlow<UiState<Destination>>(UiState.Empty)
//    val destinationsState: StateFlow<UiState<Destination>> = _destinationState.asStateFlow()
//    private val destinationId: String? = savedStateHandle["destinationId"]
//
//    sealed class EditorEvent {
//        data object Loading : EditorEvent()
//        data class Success(val message: UiText) : EditorEvent()
//        data class Error(val message: UiText) : EditorEvent()
//    }
//
//    private val _editorEvent = MutableSharedFlow<EditorEvent>()
//    val editorEvent = _editorEvent.asSharedFlow()
//
//
//    init {
//        if (!destinationId.isNullOrBlank()) {
//            loadDestinationForEditing(destinationId)
//        }
//    }
//
//    private fun loadDestinationForEditing(id: String) {
//        viewModelScope.launch {
//            _destinationState.value = UiState.Loading
//            when (val result = repository.getDestinationById(id, userId = "")) {
//                is UiState.Success -> _destinationState.value =
//                    UiState.Success(result.data.destination)
//
//                is UiState.Error -> _destinationState.value = UiState.Error(result.message)
//                else -> _destinationState.value = UiState.Empty
//            }
//        }
//    }
//
//
//    fun saveOrUpdateDestination(
//        name: String,
//        address: String,
//        category: String,
//    ) {
//        if (name.isBlank() || address.isBlank() || category.isBlank()) {
//            viewModelScope.launch {
//                _editorEvent.emit(EditorEvent.Error(UiText.StringResource(R.string.error_fields_required)))
//            }
//            return
//        }
//
//        viewModelScope.launch {
//            _editorEvent.emit(EditorEvent.Loading)
//
//            val destination = Destination(
//                id = destinationId
//                    ?: "",
//                name = name,
//                address = address,
//                category = category,
//            )
//
//            when (val result = repository.saveDestination(destination)) {
//                is UiState.Success -> {
//                    _editorEvent.emit(EditorEvent.Success(UiText.StringResource(R.string.success_save_destination)))
//                }
//
//                is UiState.Error -> {
//                    _editorEvent.emit(EditorEvent.Error(result.message))
//                }
//
//                else -> { // Handle kasus lain jika ada
//                    _editorEvent.emit(EditorEvent.Error(UiText.StringResource(R.string.error_else)))
//                }
//            }
//        }
//    }
}