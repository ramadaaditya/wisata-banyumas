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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
    private val _authState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val authState: StateFlow<UiState<User>> = _authState

    // PERBAIKAN: Tambahkan Channel untuk mengirim event satu kali.
    private val _authEvent = Channel<AuthEvent>()
    val authEvent = _authEvent.receiveAsFlow()

//
//    fun checkLoginStatus() {
//        viewModelScope.launch {
//            when (val result = repository.getCurrentUser()) {
//                is UiState.Success -> {
//                    val user = result.data
//                    if (user != null) {
//                        Timber.tag("VIEWMODEL").d("checkLoginStatus: ${user.id}")
//                        _authState.value = UiState.Success(user)
//                    } else {
//                        _authState.value =
//                            UiState.Error(
//                                UiText.StringResource(
//                                    R.string.error_user_not_found
//                                )
//                            )
//                    }
//                }
//
//                is UiState.Error -> _authState.value =
//                    UiState.Error(result.message)
//
//                else -> {}
//            }
//        }
//    }


    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_fields_required))
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.loginUser(email, password)) {
                is UiState.Success -> {
                    _authState.value = UiState.Success(result.data)
                }

                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value = UiState.Error(UiText.StringResource(R.string.error_else))
            }

        }
    }

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value =
                UiState.Error(UiText.StringResource(R.string.error_fields_required))
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        if (!isValidPassword(password)) {
            _authState.value =
                UiState.Error(UiText.StringResource(R.string.error_invalid_password))
            return
        }

        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.registerUser(email, password, name)) {
                is UiState.Success -> _authState.value = UiState.Empty
                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }


    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_email_empty))
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.resetPassword(email)) {
                is UiState.Success -> _authState.value = UiState.Empty
                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun logout() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.logoutUser()) {
                is UiState.Success -> _authState.value = UiState.Empty
                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun deleteAccount() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.deleteAccount()) {
                is UiState.Success -> _authState.value = UiState.Empty
                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value =
                    UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }
}