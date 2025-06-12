package com.banyumas.wisata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.data.repository.AuthRepository
import com.banyumas.wisata.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = authRepository.userData.map { user ->
        MainActivityUiState.Success(user)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000)
    )
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val user: User?) : MainActivityUiState
}