package com.banyumas.wisata.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.repository.UserRepository
import com.wisata.banyumas.common.UiState
import com.wisata.banyumas.common.UiText
import com.wisata.banyumas.common.isValidEmail
import com.wisata.banyumas.common.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<com.wisata.banyumas.common.UiState<User>>(com.wisata.banyumas.common.UiState.Empty)
    val authState: StateFlow<com.wisata.banyumas.common.UiState<User>> = _authState

    init {
        Log.d(TAG, ": USERVIEWMODEL dibuat ulang")
    }

    fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _authState.value = com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_fields_required))
            return
        }
        if (!com.wisata.banyumas.common.isValidEmail(email)) {
            _authState.value = com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_invalid_email))
            return
        }
        _authState.value = com.wisata.banyumas.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.loginUser(email, password)) {
                is com.wisata.banyumas.common.UiState.Success -> {
                    _authState.value = com.wisata.banyumas.common.UiState.Success(result.data)
                }

                is com.wisata.banyumas.common.UiState.Error -> _authState.value = com.wisata.banyumas.common.UiState.Error(result.message)
                else -> _authState.value = com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_else))
            }

        }
    }

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value =
                com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_fields_required))
            return
        }

        if (!com.wisata.banyumas.common.isValidEmail(email)) {
            _authState.value = com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_invalid_email))
            return
        }
        if (!com.wisata.banyumas.common.isValidPassword(password)) {
            _authState.value =
                com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_invalid_password))
            return
        }

        _authState.value = com.wisata.banyumas.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.registerUser(email, password, name)) {
                is com.wisata.banyumas.common.UiState.Success -> _authState.value = com.wisata.banyumas.common.UiState.Empty
                is com.wisata.banyumas.common.UiState.Error -> _authState.value = com.wisata.banyumas.common.UiState.Error(result.message)
                else -> _authState.value =
                    com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            when (val result = repository.getCurrentUser()) {
                is com.wisata.banyumas.common.UiState.Success -> {
                    val user = result.data
                    if (user != null) {
                        Log.d("VIEWMODEL", "checkLoginStatus: ${user.id}")
                        _authState.value = com.wisata.banyumas.common.UiState.Success(user)
                    } else {
                        _authState.value =
                            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_user_not_found))
                    }
                }

                is com.wisata.banyumas.common.UiState.Error -> _authState.value = com.wisata.banyumas.common.UiState.Error(result.message)
                else -> {}
            }
        }
    }


    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_email_empty))
            return
        }
        if (!com.wisata.banyumas.common.isValidEmail(email)) {
            _authState.value = com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_invalid_email))
            return
        }
        _authState.value = com.wisata.banyumas.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.resetPassword(email)) {
                is com.wisata.banyumas.common.UiState.Success -> _authState.value = com.wisata.banyumas.common.UiState.Empty
                is com.wisata.banyumas.common.UiState.Error -> _authState.value = com.wisata.banyumas.common.UiState.Error(result.message)
                else -> _authState.value =
                    com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun logout() {
        _authState.value = com.wisata.banyumas.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.logoutUser()) {
                is com.wisata.banyumas.common.UiState.Success -> _authState.value = com.wisata.banyumas.common.UiState.Empty
                is com.wisata.banyumas.common.UiState.Error -> _authState.value = com.wisata.banyumas.common.UiState.Error(result.message)
                else -> _authState.value =
                    com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun deleteAccount() {
        _authState.value = com.wisata.banyumas.common.UiState.Loading
        viewModelScope.launch {
            when (val result = repository.deleteAccount()) {
                is com.wisata.banyumas.common.UiState.Success -> _authState.value = com.wisata.banyumas.common.UiState.Empty
                is com.wisata.banyumas.common.UiState.Error -> _authState.value = com.wisata.banyumas.common.UiState.Error(result.message)
                else -> _authState.value =
                    com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_else))
            }
        }
    }
}