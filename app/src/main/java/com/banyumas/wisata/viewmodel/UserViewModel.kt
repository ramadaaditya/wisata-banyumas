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

    fun requestPasswordReset(email: String) {
        if (email.isBlank()) {
            _resetPasswordState.value = UiState.Error("Email tidak boleh kosong")
            return
        }
        _resetPasswordState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val success = userRepository.resetPassword(email)
                _resetPasswordState.value = if (success) {
                    UiState.Success(true)
                } else {
                    UiState.Error("Gagal mengirimkan email reset password")
                }
            } catch (e: Exception) {
                _resetPasswordState.value = UiState.Error(e.localizedMessage ?: "Terjadi kesalahan")

            }
        }
    }

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = UiState.Error("Email, password, dan nama tidak boleh kosong.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value =
                UiState.Error("Format email tidak valid. Harap masukkan email yang benar.")
            return
        }

        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val newUser = userRepository.registerUser(email, password, name)
                // Kirim email verifikasi
                userRepository.sendEmailVerification()

                // Setelah berhasil register, kita logout lagi agar pengguna
                // tidak otomatis masuk saat kembali ke LoginScreen
                userRepository.logoutUser()

                _authState.value = UiState.Success(newUser)
            } catch (e: Exception) {
                _authState.value =
                    UiState.Error("Gagal mendaftar: ${e.localizedMessage ?: "Terjadi kesalahan"}")
            }
        }
    }



    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
        _authState.value = UiState.Loading
            try {
                val firebaseUser = userRepository.loginUser(email, password)
                if (!firebaseUser.isEmailVerified) {
                    userRepository.logoutUser()
                }
                val user = userRepository.getUserById(firebaseUser.uid)
                _authState.value = UiState.Success(user)
            } catch (e: Exception) {
                _authState.value =
                    UiState.Error("Login gagal : ${e.localizedMessage ?: "Terjadi kesalahan"}")
            }
        }
    }


    fun checkLoginStatus() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val userId = userRepository.getCurrentUserId()
                if (userId == null) {
                    _authState.value = UiState.Error("Pengguna tidak masuk")
                    return@launch
                }
                val user = userRepository.getUserById(userId)
                _authState.value = UiState.Success(user)
            } catch (e: Exception) {
                _authState.value = UiState.Error("Gagal mengambil data pengguna")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                userRepository.logoutUser()
                _authState.value = UiState.Empty
            } catch (e: Exception) {
                _authState.value = UiState.Error("Gagal logout")
            }
        }
    }
}