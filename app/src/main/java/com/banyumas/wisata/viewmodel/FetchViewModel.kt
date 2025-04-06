package com.banyumas.wisata.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.repository.DestinationRepository
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {

    private val _destinations = MutableStateFlow<UiState<List<Destination>>>(UiState.Loading)
    val destinations: StateFlow<UiState<List<Destination>>> = _destinations

    fun uploadData(context: Context, apiKey: String) {
        viewModelScope.launch {
            _destinations.value = UiState.Loading
            try {
                val placeIds = repository.readPlaceFromJson(context)
                if (placeIds.isEmpty()) {
                    _destinations.value = UiState.Empty
                    return@launch
                }
                val updatedDestinations = placeIds.map { placeId ->
                    repository.savePlaceFromApi(placeId, apiKey)
                }
                _destinations.value = if (updatedDestinations.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(updatedDestinations)
                }
            } catch (e: Exception) {
                _destinations.value =
                    UiState.Error(UiText.StringResource(R.string.error_upload_data))
            }
        }
    }
}