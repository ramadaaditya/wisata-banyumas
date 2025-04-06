package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.repository.UserRepository
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import com.banyumas.wisata.utils.isValidEmail
import com.banyumas.wisata.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val authState: StateFlow<UiState<User>> = _authState

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

    fun checkLoginStatus() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.getCurrentUserId()) {
                is UiState.Success -> {
                    val user = result.data
                    if (user != null) {
                        _authState.value = UiState.Success(user)
                    } else {
                        _authState.value =
                            UiState.Error(UiText.StringResource(R.string.error_user_not_found))
                    }
                }

                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value = UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun loginUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
        if (email.isBlank() || password.isBlank()) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_fields_required))
            return
        }
        _authState.value = UiState.Loading
        viewModelScope.launch {
            when (val result = repository.loginUser(email, password)) {
                is UiState.Success -> _authState.value = result
                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> _authState.value = UiState.Error(UiText.StringResource(R.string.error_else))
            }
        }
    }

    fun resetPassword(email: String) {
        _authState.value = UiState.Loading
        if (email.isBlank()) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_email_empty))
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = UiState.Error(UiText.StringResource(R.string.error_invalid_email))
            return
        }
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