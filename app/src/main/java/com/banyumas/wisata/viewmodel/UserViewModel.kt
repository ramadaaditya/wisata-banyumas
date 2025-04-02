package com.banyumas.wisata.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.repository.UserRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val authState: StateFlow<UiState<User>> = _authState

    private val _resetPasswordState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
    val resetPasswordState: StateFlow<UiState<Boolean>> = _resetPasswordState

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = UiState.Error("Email, password, dan nama tidak boleh kosong.")
            return
        }
        _authState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.registerUser(email, password, name)
            userRepository.logoutUser()
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _resetPasswordState.value = UiState.Error("Email tidak boleh kosong")
        }
        _resetPasswordState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.resetPassword(email)
        }
    }

    fun loginUser(email: String, password: String) {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.loginUser(email, password)
        }
    }

    fun checkLoginStatus() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.getCurrentUserId()
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logoutUser()
        }
    }

    fun deleteAccount() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            userRepository.deleteAccount()
        }
    }
}