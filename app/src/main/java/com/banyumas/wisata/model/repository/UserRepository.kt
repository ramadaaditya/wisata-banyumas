package com.banyumas.wisata.model.repository

import android.util.Log
import com.banyumas.wisata.model.User
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.isValidEmail
import com.banyumas.wisata.utils.isValidPassword
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,

    ) {
    companion object {
        private const val USERS_COLLECTION = "Users"
        private const val TAG = "FirebaseUserRepository"
    }

    suspend fun registerUser(
        email: String,
        password: String,
        name: String,
    ): UiState<User> {
        if (!isValidEmail(email)) {
            return UiState.Error("Email tidak valid, periksa kembali email anda")
        }
        if (!isValidPassword(password)) {
            return UiState.Error("Password terlalu lemah. Gunakan kombinasi huruf dan angka dan panjang 6 karakter")
        }
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = auth.currentUser ?: throw Exception("Failed Load User Account")
            val newUser = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                hashedPassword = password,
            )
            firestore.collection(USERS_COLLECTION).document(newUser.id).set(newUser).await()
            firebaseUser.sendEmailVerification().await()
            Log.d(TAG, "registerUser: User registered successfully")
            UiState.Success(newUser)
        } catch (e: Exception) {
            val exceptionMessage = when (e) {
                is FirebaseAuthWeakPasswordException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                is FirebaseAuthUserCollisionException -> "Email sudah digunakan, Silahkan gunakan email lain."
                is FirebaseAuthInvalidCredentialsException -> "Email tidak valid, Periksa kembali email anda."
                is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                else -> e.localizedMessage ?: "Terjadi kesalahan, coba lagi nanti"
            }
            UiState.Error(exceptionMessage)
        }
    }

    suspend fun loginUser(email: String, password: String): UiState<FirebaseUser> {
        return try {
            val firebaseUser = auth.currentUser ?: throw Exception("Failed Load User Account")
            if (firebaseUser.isEmailVerified) {
                auth.signInWithEmailAndPassword(email, password).await()
            }
            UiState.Success(firebaseUser)
        } catch (e: Exception) {
            val exceptionMessage = when (e) {
                is FirebaseAuthInvalidCredentialsException -> "Email tidak valid, Periksa kembali email anda."
                is FirebaseNetworkException -> "Tidak ada koneksi internet. Periksa jaringan anda dan coba lagi."
                else -> e.localizedMessage ?: "Terjadi kesalahan, coba lagi nanti"
            }
            UiState.Error(exceptionMessage)
        }
    }

    fun logoutUser(): UiState<Unit> {
        return try {
            auth.signOut()
            UiState.Empty
        } catch (e: Exception) {
            UiState.Error("Logout failed : ${e.localizedMessage}")
        }
    }

    suspend fun getUserById(userId: String): User {
        if (userId.isBlank()) {
            throw IllegalArgumentException("User ID cannot be empty or blank.")
        }
        return try {
            val document = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = document.toObject(User::class.java)
                ?: throw Exception("Failed to map Firestore document to User Object.")
            Log.d(TAG, "getUserById: $user")
            user
        } catch (e: FirebaseFirestoreException) {
            throw Exception("Failed to fetch user data from Firestore: ${e.localizedMessage}")
        } catch (e: Exception) {
            throw Exception("An error occured : ${e.localizedMessage}")
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    suspend fun resetPassword(email: String): UiState<Boolean> {
        if (!isValidEmail(email)) {
            return UiState.Error("Email tidak valid, periksa kembali email anda")
        }
        return try {
            auth.sendPasswordResetEmail(email).await()
            UiState.Success(true)
        } catch (e: Exception) {
            val exceptionMessage = when (e) {
                is FirebaseNetworkException -> ""
                is FirebaseAuthInvalidUserException -> ""
                else -> e.localizedMessage ?: "Terjadi kesalahan. Silahkan coba lagi nanti"
            }
            UiState.Error(exceptionMessage)
        }
    }

    suspend fun deleteAccount() {
        try {
            val user = auth.currentUser ?: throw Exception("User not found")
            user.delete().await()
            Log.d(TAG, "deleteAccount: User Account deleted")
        } catch (e: Exception) {
            Log.d(TAG, "deleteAccount: Error deleting account")
            throw e
        }
    }
}