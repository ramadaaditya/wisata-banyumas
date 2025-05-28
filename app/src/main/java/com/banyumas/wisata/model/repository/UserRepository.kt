package com.banyumas.wisata.model.repository

import com.banyumas.wisata.R
import com.banyumas.wisata.model.User
import com.wisata.banyumas.common.UiState
import com.wisata.banyumas.common.UiText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    companion object {
        private const val USERS_COLLECTION = "users"
    }

    suspend fun registerUser(email: String, password: String, name: String): com.wisata.banyumas.common.UiState<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = auth.currentUser
                ?: return com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_load_user))
            val newUser = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                hashedPassword = password,
            )
            firestore.collection(USERS_COLLECTION).document(newUser.id).set(newUser).await()
            firebaseUser.sendEmailVerification().await()
            com.wisata.banyumas.common.UiState.Success(Unit)
        } catch (e: Exception) {
            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_register), e)
        }
    }

    suspend fun loginUser(email: String, password: String): com.wisata.banyumas.common.UiState<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            val userId = firebaseUser?.uid
            if (userId.isNullOrBlank()) {
                return com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_user_not_found))
            }
            val documentSnapshot =
                firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .get()
                    .await()
            val user = documentSnapshot.toObject(User::class.java)
            if (user != null) {
                com.wisata.banyumas.common.UiState.Success(user)
            } else {
                com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_user_not_found))
            }
        } catch (e: Exception) {
            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_login), e)
        }
    }

    suspend fun getCurrentUser(): com.wisata.banyumas.common.UiState<User?> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId.isNullOrBlank()) {
                return com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_user_not_found))
            }
            val documentSnapshot =
                firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            com.wisata.banyumas.common.UiState.Success(user)
        } catch (e: Exception) {
            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_get_user_id), e)
        }
    }

    fun logoutUser(): com.wisata.banyumas.common.UiState<Unit> {
        return try {
            auth.signOut()
            com.wisata.banyumas.common.UiState.Success(Unit)
        } catch (e: Exception) {
            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_logout), e)
        }
    }

    suspend fun resetPassword(email: String): com.wisata.banyumas.common.UiState<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            com.wisata.banyumas.common.UiState.Success(Unit)
        } catch (e: Exception) {
            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_reset_password), e)
        }
    }

    suspend fun deleteAccount(): com.wisata.banyumas.common.UiState<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                firestore.collection(USERS_COLLECTION).document(user.uid).delete().await()
                user.delete().await()
                com.wisata.banyumas.common.UiState.Success(Unit)
            } else {
                com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_user_not_logged_in))
            }
        } catch (e: Exception) {
            com.wisata.banyumas.common.UiState.Error(com.wisata.banyumas.common.UiText.StringResource(R.string.error_delete_account), e)
        }
    }
}