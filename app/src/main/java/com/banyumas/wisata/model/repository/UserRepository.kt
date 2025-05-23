package com.banyumas.wisata.model.repository

import com.auth0.android.jwt.JWT
import com.banyumas.wisata.R
import com.banyumas.wisata.model.User
import com.banyumas.wisata.model.api.BackendService
import com.banyumas.wisata.model.api.LoginRequest
import com.banyumas.wisata.model.api.LoginResponse
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.utils.UiText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authService: BackendService
) {
    companion object {
        private const val USERS_COLLECTION = "Users"
    }

    suspend fun registerUser(email: String, password: String, name: String): UiState<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = auth.currentUser
                ?: return UiState.Error(UiText.StringResource(R.string.error_load_user))
            val newUser = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                hashedPassword = password,
            )
            firestore.collection(USERS_COLLECTION).document(newUser.id).set(newUser).await()
            firebaseUser.sendEmailVerification().await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            handleException(R.string.error_register, e)
        }
    }

//    suspend fun loginUser(email: String, password: String): UiState<User> {
//        return try {
//            val result = auth.signInWithEmailAndPassword(email, password).await()
//            val firebaseUser = result.user
//            val userId = firebaseUser?.uid
//            if (userId.isNullOrBlank()) {
//                return UiState.Error(UiText.StringResource(R.string.error_user_not_found))
//            }
//            val documentSnapshot =
//                firestore.collection(USERS_COLLECTION)
//                    .document(userId)
//                    .get()
//                    .await()
//            val user = documentSnapshot.toObject(User::class.java)
//            if (user != null) {
//                UiState.Success(user)
//            } else {
//                UiState.Error(UiText.StringResource(R.string.error_user_not_found))
//            }
//        } catch (e: Exception) {
//            handleException(R.string.error_login, e)
//        }
//    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = authService.loginUser(LoginRequest(email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun parseJwt(token: String): String? {
        val jwt = JWT(token)
        val userId = jwt.getClaim("id").asString()
        return userId
    }

    suspend fun getCurrentUserId(): UiState<User?> {
        return try {
            val userId = auth.currentUser?.uid
            if (userId.isNullOrBlank()) {
                return UiState.Error(UiText.StringResource(R.string.error_user_not_found))
            }
            val documentSnapshot =
                firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = documentSnapshot.toObject(User::class.java)
            UiState.Success(user)
        } catch (e: Exception) {
            handleException(R.string.error_get_user_id, e)
        }
    }

    fun logoutUser(): UiState<Unit> {
        return try {
            auth.signOut()
            UiState.Success(Unit)
        } catch (e: Exception) {
            handleException(R.string.error_logout, e)
        }
    }

    suspend fun resetPassword(email: String): UiState<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            UiState.Success(Unit)
        } catch (e: Exception) {
            handleException(R.string.error_reset_password, e)
        }
    }

    suspend fun deleteAccount(): UiState<Unit> {
        return try {
            val user = auth.currentUser
            if (user != null) {
                firestore.collection(USERS_COLLECTION).document(user.uid).delete().await()
                user.delete().await()
                UiState.Success(Unit)
            } else {
                UiState.Error(UiText.StringResource(R.string.error_user_not_logged_in))
            }
        } catch (e: Exception) {
            handleException(R.string.error_delete_account, e)
        }
    }

    private fun handleException(
        @androidx.annotation.StringRes resId: Int,
        throwable: Throwable? = null
    ): UiState.Error {
        return UiState.Error(UiText.StringResource(resId), throwable)
    }
}