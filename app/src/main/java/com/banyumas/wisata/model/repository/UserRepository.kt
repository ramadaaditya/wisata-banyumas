package com.banyumas.wisata.model.repository

import android.util.Log
import com.banyumas.wisata.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    suspend fun registerUser(email: String, password: String, name: String): Boolean {
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
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            Log.d(TAG, "loginUser: Attempting login for email $email")
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
            Log.e(TAG, "loginUser: Login failed", e)
            null
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val document = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = document.toObject(User::class.java)
            if (user == null) {
                Log.e(TAG, "getUserById: ")
                null
            } else {
                user
            }
        } catch (e: Exception) {
            Log.e(TAG, "getUserById: Failed load user for $userId", e)
            null
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun logoutUser(): Boolean {
        return try {
            if (auth.currentUser != null) {
                auth.signOut()
                Log.d(TAG, "logoutUser: Logout Successfully")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("LogoutError", "Gagal logout: ${e.message}")
            false
        }
    }

    suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            Log.e("ResetPassword", "Gagal mengirim email reset password: ${e.message}")
            false
        }
    }

    suspend fun deleteAccount(): Boolean {
        return try {
            if (auth.currentUser != null) {
                val user = auth.currentUser ?: throw Exception("User not found")
                firestore.collection(USERS_COLLECTION).document(user.uid).delete().await()
                user.delete().await()
                Log.d(TAG, "deleteAccount: Deleted Account succeffully for $user")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            throw e
        }
    }

    companion object {
        private const val USERS_COLLECTION = "Users"
        private const val TAG = "UserRepository"
    }
}