package com.banyumas.wisata.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.data.model.User
import com.banyumas.wisata.data.repository.FirebaseUserRepository
import com.banyumas.wisata.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userRepository: FirebaseUserRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<UiState<User>>(UiState.Empty)
    val authState: StateFlow<UiState<User>> = _authState

    private val _userRoleState = MutableStateFlow<UiState<Role>>(UiState.Loading)
    val userRoleState: StateFlow<UiState<Role>> = _userRoleState

    fun fetchUserRole(userId: String) {
        viewModelScope.launch {
            _userRoleState.value = UiState.Loading
            try {
                val role =
                    userRepository.getUserRole(userId) // 🔹 Ambil role dari Firestore/Database
                _userRoleState.value = UiState.Success(role)
                Log.d("AppViewModel", "User role updated: $role")
            } catch (e: Exception) {
                _userRoleState.value = UiState.Error(e.message ?: "Gagal mengambil role")
                Log.e("AppViewModel", "Error fetching user role: ${e.message}")
            }
        }
    }

    init {
        checkLoginStatus()
    }

    private fun setLoadingState() {
        _authState.value = UiState.Loading
    }

    private fun handleError(e: Exception, fallbackMessage: String) {
        Log.e("AppViewModel", "Error : ${e.localizedMessage} ")
        _authState.value = UiState.Error(fallbackMessage)
    }

    fun resetAuthState() {
        _authState.value = UiState.Empty
    }

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = UiState.Error("Email, password, and name cannot be empty.")
            return
        }
        setLoadingState()
        viewModelScope.launch {
            try {
                val newUser = userRepository.registerUser(email, password, name)
                _authState.value = UiState.Success(newUser)
                userRepository.sendEmailVerification()
            } catch (e: Exception) {
                handleError(e, "Failed to register user")
            }
        }
    }

    fun loginUser(email: String, password: String) {
        setLoadingState()
        viewModelScope.launch {
            try {
                Log.d("AppViewModel", "Login started for email: $email")
                val firebaseUser = userRepository.loginUser(email, password)
                Log.d("AppViewModel", "FirebaseUser UID: ${firebaseUser.uid}")

                val user = userRepository.getUserById(firebaseUser.uid)
                Log.d("AppViewModel", "User fetched: ${user.name}, Role: ${user.role}")

                _userRoleState.value = UiState.Success(user.role)
                _authState.value = UiState.Success(user)
                Log.d("AppViewModel", "Auth state updated to success")
            } catch (e: Exception) {
                Log.e("AppViewModel", "Error during login: ${e.message}")
                _authState.value = UiState.Error("Login failed: ${e.message}")
            }
        }
    }

    private fun checkLoginStatus() {
        setLoadingState()
        viewModelScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                val user = userRepository.getUserById(userId)
                _userRoleState.value = UiState.Success(user.role)
                Log.d("AppViewModel", "User role identified as: ${user.role}")
                _authState.value = UiState.Success(user)
            } catch (e: Exception) {
                handleError(e, "User not logged in")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logoutUser()
                _authState.value = UiState.Empty
            } catch (e: Exception) {
                handleError(e, "Failed to logout")
            }
        }
    }
}