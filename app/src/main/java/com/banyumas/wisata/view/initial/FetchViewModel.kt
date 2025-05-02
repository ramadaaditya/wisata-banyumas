package com.banyumas.wisata.view.initial

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.model.repository.DestinationRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FetchViewModel @Inject constructor(
    private val repository: DestinationRepository
) : ViewModel() {

    private val _destination = MutableStateFlow<UiState<List<SearchResultItem>>>(UiState.Empty)
    val destination: StateFlow<UiState<List<SearchResultItem>>> = _destination

    private val _detailDestination = MutableStateFlow<UiState<Destination>>(UiState.Empty)
    val detailDestination: StateFlow<UiState<Destination>> = _detailDestination

    private val _detailDestinations = MutableStateFlow<UiState<List<Destination>>>(UiState.Empty)
    val detailDestinations: StateFlow<UiState<List<Destination>>> = _detailDestinations

    fun searchDestination(name: String) {
        _destination.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.searchDestinationByName(name)
            Log.d("VIEWMODEL", "searchDestination: $result ")
            _destination.value = result
        }
    }

    fun fetchDetailDestinations(placeId: String) {
        _detailDestination.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.getDestinationDetails(placeId)
            _detailDestination.value = result
        }
    }
}