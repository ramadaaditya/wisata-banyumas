package com.banyumas.wisata.view.initial

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.model.Destination
import com.banyumas.wisata.model.SearchResultItem
import com.banyumas.wisata.model.repository.DestinationRepository
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import com.banyumas.wisata.utils.parsePlaceIdsFromJson
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

    private val _detailDestinations = MutableStateFlow<UiState<List<Destination>>>(UiState.Empty)
    val detailDestinations: StateFlow<UiState<List<Destination>>> =
        _detailDestinations

    private var _importedIds = MutableStateFlow<List<String>>(emptyList())
    val importedIds: StateFlow<List<String>> = _importedIds

    fun searchDestination(name: String) {
        if (name.isBlank()) {
            _destination.value =
                UiState.Error(UiText.DynamicString("Nama tempat tidak ditemukan"))
            return
        }

        _destination.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.searchDestinationByName(name)
            _destination.value = result
        }
    }

    fun importJsonFromUri(json: String) {
        viewModelScope.launch {
            try {
                val ids = parsePlaceIdsFromJson(json)
                _importedIds.value = ids
            } catch (e: Exception) {
                _importedIds.value = emptyList()
                Log.e(TAG, "importJsonFromUri: Error parsing JSON", e)
            }
        }
    }

    fun fetchAndSaveAllDestination(ids: List<String>) {
        _detailDestinations.value = UiState.Loading
        viewModelScope.launch {
            try {
                val allDestinations = mutableListOf<Destination>()

                for (id in ids) {
                    when (val result = repository.fetchAndSaveDestination(id)) {
                        is UiState.Success -> {
                            result.data?.let { destination ->
                                allDestinations.add(destination)
                            }
                        }

                        is UiState.Error -> {
                            _detailDestinations.value = result
                            return@launch
                        }

                        else -> {}
                    }
                }

                _detailDestinations.value = UiState.Success(allDestinations)
            } catch (e: Exception) {
                _detailDestinations.value =
                    UiState.Error(UiText.DynamicString("Gagal mengambil data destinasi: ${e.message}"))
            }
        }
    }
}