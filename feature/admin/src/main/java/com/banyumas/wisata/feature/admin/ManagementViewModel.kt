package com.banyumas.wisata.feature.admin

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.repository.DestinationRepository
import com.banyumas.wisata.core.model.Destination
import com.banyumas.wisata.core.model.UiDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class EditorUiState(
    val destination: UiDestination? = null,
    val isLoading: Boolean = false,
    val isSaveSuccess: Boolean = false,
    val userMessage: UiText? = null,
)

@HiltViewModel
class ManagementViewModel @Inject constructor(
    private val repository: DestinationRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState = _uiState.asStateFlow()
    private val destinationId: String? = savedStateHandle["destinationId"]

    init {
        if (destinationId != null) {
            loadDestinationForEditing(destinationId)
        }
    }

    private fun loadDestinationForEditing(id: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.getDestinationById(id, userId = "")) {
                is UiState.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            destination = result.data
                        )
                    }
                }

                is UiState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = result.message
                        )
                    }
                }

                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun saveOrUpdateDestination(
        name: String,
        address: String,
        category: String,
    ) {
        if (name.isBlank() || address.isBlank() || category.isBlank()) {
            _uiState.update {
                it.copy(userMessage = (UiText.StringResource(R.string.error_fields_required)))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val destinationToSave = destinationId?.let {
                Destination(
                    id = it,
                    name = name,
                    address = address,
                    category = category,
                )
            }

            when (val result = destinationToSave?.let { repository.saveDestination(it) }) {
                is UiState.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = UiText.StringResource(R.string.success_save_destination),
                            isSaveSuccess = true
                        )
                    }
                }

                is UiState.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userMessage = result.message
                        )
                    }
                }

                else -> {
                    _uiState.update { it.copy(userMessage = UiText.StringResource(R.string.error_else)) }
                }
            }
        }
    }
}