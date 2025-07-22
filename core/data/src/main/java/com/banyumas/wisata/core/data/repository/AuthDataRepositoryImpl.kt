package com.banyumas.wisata.core.data.repository

import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.common.UiText
import com.banyumas.wisata.core.data.R
import com.banyumas.wisata.core.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthDataRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val TAG = "AuthRepository"
    }

    override val userData: Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            this.launch {
                val firebaseUser = firebaseAuth.currentUser
                if (firebaseUser == null) {
                    trySend(null)
                } else {
                    try {
                        val userProfile = firestore.collection(USERS_COLLECTION)
                            .document(firebaseUser.uid)
                            .get()
                            .await()
                            .toObject(User::class.java)
                        trySend(userProfile)
                    } catch (e: Exception) {
                        Timber.tag(TAG).e(e, "Gagal mengambil profil user dari firestore")
                        trySend(null)
                    }
                }
            }
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    private suspend fun <T> safeCall(
        errorMessage: UiText,
        call: suspend () -> UiState<T>
    ): UiState<T> {
        return try {
            call()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            // Memberikan pesan error yang lebih spesifik jika memungkinkan
            val specificMessage =
                e.localizedMessage?.let { UiText.DynamicString(it) } ?: errorMessage
            UiState.Error(specificMessage, e)
        }
    }


    override suspend fun registerUser(
        email: String,
        password: String,
        name: String
    ): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_register)
    ) {
        auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = auth.currentUser
            ?: return@safeCall UiState.Error(UiText.StringResource(R.string.error_load_user))

        // BEST PRACTICE: JANGAN PERNAH SIMPAN PASSWORD DI DATABASE, BAHKAN HASHED.
        // Hapus field hashedPassword dari model User Anda.
        val newUser = User(id = firebaseUser.uid, name = name, email = email)

        firestore.collection(USERS_COLLECTION).document(newUser.id).set(newUser).await()
        firebaseUser.sendEmailVerification().await()
        UiState.Success(Unit)
    }

    override suspend fun loginUser(email: String, password: String): UiState<User> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_login)
    ) {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val userId = result.user?.uid
            ?: return@safeCall UiState.Error(UiText.StringResource(R.string.error_user_not_found))

        val documentSnapshot = firestore.collection(USERS_COLLECTION).document(userId).get().await()
        val user = documentSnapshot.toObject(User::class.java)
            ?: return@safeCall UiState.Error(UiText.StringResource(R.string.error_user_not_found))

        UiState.Success(user)
    }


    override suspend fun logoutUser(): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_logout)
    ) {
        auth.signOut()
        UiState.Success(Unit)
    }

    override suspend fun resetPassword(email: String): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_reset_password)
    ) {
        auth.sendPasswordResetEmail(email).await()
        UiState.Success(Unit)
    }

    override suspend fun deleteAccount(): UiState<Unit> = safeCall(
        errorMessage = UiText.StringResource(R.string.error_delete_account)
    ) {
        val user = auth.currentUser
            ?: return@safeCall UiState.Error(UiText.StringResource(R.string.error_user_not_logged_in))

        firestore.collection(USERS_COLLECTION).document(user.uid).delete().await()
        user.delete().await()
        UiState.Success(Unit)
    }
}