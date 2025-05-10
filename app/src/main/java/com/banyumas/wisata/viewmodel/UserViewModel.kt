package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.R
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.repository.UserRepository
import com.banyumas.wisata.utils.MyDataStore
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import com.banyumas.wisata.utils.isValidEmail
import com.banyumas.wisata.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository,
    private val dataStore: MyDataStore
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val authState: StateFlow<UiState<User>> = _authState

    private val _loginState = MutableStateFlow<UiState<String>>(UiState.Empty)
    val loginState: StateFlow<UiState<String>> = _loginState

    val token: Flow<String?> = dataStore.token

    fun saveToken(token: String) {
        viewModelScope.launch {
            dataStore.saveTokenKey(token)
        }
    }

    fun clearToken() {
        viewModelScope.launch {
            dataStore.clearToken()
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
            val result = repository.login(email, password)
            result.onSuccess { response ->
                response.token?.let {
                    saveToken(response.token)
                    _loginState.value = UiState.Success(it)
                } ?: run {
                    _loginState.value = UiState.Error(UiText.DynamicString("token tidak tersedia"))
                }
            }.onFailure { error ->
                _loginState.value =
                    UiState.Error(UiText.DynamicString(error.message ?: "Login gagal"))
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