package com.banyumas.wisata.core.data.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.common.isValidEmail
import com.banyumas.wisata.core.common.isValidPassword
import com.banyumas.wisata.core.data.R
import com.banyumas.wisata.core.data.repository.AuthRepository
import com.banyumas.wisata.core.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// PERBAIKAN: Buat sealed interface untuk mendefinisikan event satu kali yang jelas.
sealed interface AuthEvent {
    data object LoginSuccess : AuthEvent
    data class ActionFailed(val message: UiText) : AuthEvent
    // Anda bisa menambahkan event lain di sini, mis: RegistrationSuccess, PasswordResetSent
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: AuthRepository,
) : ViewModel() {
    private val _localState = MutableStateFlow<UiState<Nothing>>(UiState.Empty)

    val authState: StateFlow<UiState<User>> = combine(
        repository.userData,
        _localState
    ) { userData, localState ->
        when (localState) {
            is UiState.Loading -> UiState.Loading
            is UiState.Error -> localState
            else -> {
                if (userData != null) {
                    UiState.Success(userData)
                } else {
                    UiState.Empty
                }
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UiState.Loading
    )

    private val _authEvent = Channel<AuthEvent>()
    val authEvent = _authEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.userData.collect { user ->
                Timber.tag("UserViewModel").d("Repository userData changed: $user")
            }
        }
    }


    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _localState.value = UiState.Error(UiText.StringResource(R.string.error_fields_required))
            return
        }
        if (!isValidEmail(email)) {
            _localState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }

        _localState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.loginUser(email, password)) {
                is UiState.Success -> {
                    _localState.value =
                        UiState.Empty // Reset ke empty, userData dari repository akan handle success
                    _authEvent.send(AuthEvent.LoginSuccess)
                }

                is UiState.Error -> {
                    _localState.value = UiState.Error(result.message)
                    _authEvent.send(AuthEvent.ActionFailed(result.message))
                }

                else -> {
                    _localState.value = UiState.Error(UiText.StringResource(R.string.error_else))
                }
            }
        }
    }

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _localState.value =
                UiState.Error(UiText.StringResource(R.string.error_fields_required))
            return
        }

        if (!isValidEmail(email)) {
            _localState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        if (!isValidPassword(password)) {
            _localState.value =
                UiState.Error(UiText.StringResource(R.string.error_invalid_password))
            return
        }

        _localState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.registerUser(email, password, name)) {
                is UiState.Success -> _localState.value = UiState.Empty
                is UiState.Error -> _localState.value = UiState.Error(result.message)
                else -> _localState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }


    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _localState.value = UiState.Error(UiText.StringResource(R.string.error_email_empty))
            return
        }
        if (!isValidEmail(email)) {
            _localState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        _localState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.resetPassword(email)) {
                is UiState.Success -> _localState.value = UiState.Empty
                is UiState.Error -> _localState.value = UiState.Error(result.message)
                else -> _localState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun logout() {
        _localState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.logoutUser()) {
                is UiState.Success -> _localState.value = UiState.Empty
                is UiState.Error -> _localState.value = UiState.Error(result.message)
                else -> _localState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun deleteAccount() {
        _localState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.deleteAccount()) {
                is UiState.Success -> _localState.value = UiState.Empty
                is UiState.Error -> _localState.value = UiState.Error(result.message)
                else -> _localState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }
}