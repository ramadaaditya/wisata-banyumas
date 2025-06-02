package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.common.isValidEmail
import com.banyumas.wisata.core.common.isValidPassword
import com.banyumas.wisata.core.data.repository.UserDataRepository
import com.banyumas.wisata.core.model.User
import com.wisata.banyumas.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserDataRepository,
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val authState: StateFlow<UiState<User>> = _authState.asStateFlow()

    companion object {
        private const val TAG = "UserViewModel"
    }

    init {
        Timber.tag(TAG).d("UserViewModel instance created")
        // Jika Anda ingin checkLoginStatus dipanggil setiap kali ViewModel dibuat,
        // Anda bisa memanggilnya di sini dan menghapusnya dari MainActivity.
        // Untuk saat ini, kita biarkan MainActivity yang memanggilnya.
        // checkLoginStatus()
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            _authState.value = UiState.Loading
            when (val result = repository.getCurrentUser()) {
                is UiState.Success -> {
                    val user = result.data
                    if (user != null) {
                        Timber.tag("VIEWMODEL").d("checkLoginStatus: ${user.id}")
                        _authState.value = UiState.Success(user)
                    } else {
                        _authState.value =
                            UiState.Error(UiText.StringResource(R.string.error_user_not_found))
                    }
                }

                is UiState.Error -> _authState.value = UiState.Error(result.message)
                else -> {}
            }
        }
    }

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