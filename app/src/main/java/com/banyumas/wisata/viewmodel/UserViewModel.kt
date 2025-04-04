package com.banyumas.wisata.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.repository.UserRepository
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.isValidEmail
import com.banyumas.wisata.utils.isValidPassword
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
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
//
//    private val _booleanState = MutableStateFlow<UiState<Boolean>>(UiState.Empty)
//    val booleanState: StateFlow<UiState<Boolean>> = _booleanState

    fun checkLoginStatus() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val userId = repository.getCurrentUserId()
                Log.d("VIEWMODEL", "checkLoginStatus: Check user for $userId ")
                if (userId == null) {
                    _authState.value = UiState.Error("Pengguna tidak masuk")
                    return@launch
                }
                val user = repository.getUserById(userId)
                if (user == null) {
                    _authState.value = UiState.Error("Data pengguna tidak ditemukan")
                    return@launch
                }
                Log.d("VIEWMODEL", "checkLoginStatus: User status $user ")
                _authState.value = UiState.Success(user)
            } catch (e: Exception) {
                Log.e("VIEWMODEL", "checkLoginStatus: Gagal mengambil data pengguna ", e)
                _authState.value = UiState.Error("Gagal mengambil data pengguna")
            }
        }
    }

    fun registerUser(email: String, password: String, name: String) {
        if (email.isBlank() || password.isBlank() || name.isBlank()) {
            _authState.value = UiState.Error("Email, password, dan nama tidak boleh kosong.")
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = UiState.Error("Email tidak valid, periksa kembali email Anda.")
            return
        }
        if (!isValidPassword(password)) {
            _authState.value =
                UiState.Error("Password tidak valid, pastikan panjang minimal 6 karakter dan mengandung angka dan huruf.")
            return
        }

        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.registerUser(email, password, name)
                if (result) {
                    _authState.value = UiState.Empty
                } else {
                    _authState.value = UiState.Error("Terjadi kesalahan")
                }
            } catch (e: Exception) {
                val exceptionMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                    is FirebaseAuthUserCollisionException -> "Email sudah digunakan, Silahkan gunakan email lain."
                    is FirebaseAuthInvalidCredentialsException -> "Email tidak valid, Periksa kembali email anda."
                    is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                    else -> e.localizedMessage ?: "Terjadi kesalahan, coba lagi nanti"
                }
                _authState.value = UiState.Error(exceptionMessage)
            }
        }
    }

    fun loginUser(email: String, password: String) {
        if (!isValidEmail(email)) {
            _authState.value = UiState.Error("Email tidak valid, periksa kembali email Anda.")
            return
        }
        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val firebaseUser = repository.loginUser(email, password)
                if (firebaseUser == null) {
                    _authState.value =
                        UiState.Error("Login gagal, periksa kembali email dan password anda.")
                    return@launch
                }
                val user = repository.getUserById(firebaseUser.uid)
                if (user == null) {
                    _authState.value = UiState.Error(
                        "Data pengguna tidak ditemukan setelah login."
                    )
                    return@launch
                }
                _authState.value = UiState.Success(user)
            } catch (e: Exception) {
                val exceptionMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Email tidak valid, Periksa kembali email anda."
                    is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                    else -> e.localizedMessage ?: "Terjadi kesalahan, coba lagi nanti"
                }
                Log.e("VIEWMODEL", "loginUser: Error saat login", e)
                _authState.value = UiState.Error(exceptionMessage)
            }
        }
    }

    fun resetPassword(email: String) {
        _authState.value = UiState.Loading
        if (email.isBlank()) {
            _authState.value = UiState.Error("Email tidak boleh kosong")
            return
        }
        if (!isValidEmail(email)) {
            _authState.value = UiState.Error("Email tidak valid, periksa kembali email Anda.")
            return
        }
        viewModelScope.launch {
            try {
                val result = repository.resetPassword(email)
                if (result) {
                    _authState.value = UiState.Empty
                } else {
                    _authState.value = UiState.Error("Email tidak valid, periksa kembali email")
                }
            } catch (e: Exception) {
                val exceptionMessage = when (e) {
                    is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan Anda dan coba lagi."
                    is FirebaseAuthInvalidUserException -> "Email tidak terdaftar. Periksa kembali email Anda."
                    else -> e.localizedMessage ?: "Terjadi kesalahan. Silahkan coba lagi nanti."
                }
                _authState.value = UiState.Error(exceptionMessage)
            }
        }
    }

    fun logout() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.logoutUser()
                if (result) {
                    _authState.value = UiState.Empty
                } else {
                    _authState.value = UiState.Error("Terjadi Kesalahan, Coba lagi nanti")
                }
            } catch (e: Exception) {
                val exceptionMessage = when (e) {
                    is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                    is FirebaseAuthInvalidUserException -> "Pengguna tidak ditemukan. Pastikan Anda telah login."
                    else -> e.localizedMessage ?: "Terjadi kesalahan. Silahkan coba lagi nanti."
                }
                _authState.value = UiState.Error(exceptionMessage)
            }
        }
    }

    fun deleteAccount() {
        _authState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.deleteAccount()
                if (result) {
                    _authState.value = UiState.Empty
                } else {
                    _authState.value = UiState.Error("Akun tidak ditemukan.")
                }
            } catch (e: Exception) {
                val exceptionMessage = when (e) {
                    is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                    is FirebaseAuthInvalidUserException -> "Pengguna tidak ditemukan. Pastikan Anda telah login."
                    else -> e.localizedMessage ?: "Terjadi kesalahan. Silahkan coba lagi nanti."
                }
                _authState.value = UiState.Error(exceptionMessage)
            }
        }
    }
}