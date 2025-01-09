package com.banyumas.wisata.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.data.model.Destination
import com.banyumas.wisata.data.repository.DataRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchViewModel @Inject constructor(
    private val repository: DataRepository
) : ViewModel() {

    private val _destinations = MutableStateFlow<List<UiState<Destination>>>(emptyList())
    val destinations: StateFlow<List<UiState<Destination>>> = _destinations

    fun uploadData(
        context: Context,
        apiKey: String,
    ) {
        viewModelScope.launch {
            val placeIds = repository.readPlaceFromJson(context)
            // Set semua destinasi ke Loading
            _destinations.value = placeIds.map { UiState.Loading }

            val updatedDestinations = placeIds.map { placeId ->
                try {
                    println("Fetching place $placeId")
                    repository.fetchAndSavePlace(placeId, apiKey)
                    val destination = Destination(
                        id = placeId, // Isi dengan data yang relevan
                        name = "Fetched Place $placeId" // Placeholder
                    )
                    UiState.Success(destination)
                } catch (e: Exception) {
                    println("Error processing place $placeId: ${e.message}")
                    UiState.Error("Error fetching place $placeId: ${e.message}")
                }
            }
            _destinations.value = updatedDestinations
        }
    }
}