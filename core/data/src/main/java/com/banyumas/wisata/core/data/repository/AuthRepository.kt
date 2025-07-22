package com.banyumas.wisata.core.data.repository

import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.model.User
import kotlinx.coroutines.flow.Flow


interface AuthRepository {
    val userData: Flow<User?>

    suspend fun registerUser(email: String, password: String, name: String): UiState<Unit>

    suspend fun loginUser(email: String, password: String): UiState<User>

//    suspend fun getCurrentUser(): UiState<User?>

    suspend fun logoutUser(): UiState<Unit>

    suspend fun resetPassword(email: String): UiState<Unit>

    suspend fun deleteAccount(): UiState<Unit>
}
