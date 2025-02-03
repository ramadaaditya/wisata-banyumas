package com.banyumas.wisata.data.repository

import android.util.Log
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
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
    ): User {
        return try {
            // Create a user in Firebase Authentication
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: throw IllegalStateException("Firebase user is null after registration")

            // Create a new User object
            val newUser = User(
                id = firebaseUser.uid,
                name = name,
                email = email,
                hashedPassword = password,
            )

            // Save the user data to Firestore
            addUserToFireStore(newUser)
            Log.d(TAG, "registerUser: User registered successfully")
            newUser
        } catch (e: Exception) {
            Log.e(TAG, "registerUser: Error during registration - ${e.message}", e)
            throw e // Reth
        }
    }

    suspend fun loginUser(email: String, password: String): FirebaseUser {
        return try {
            Log.d(TAG, "loginUser: Attempting login for email: $email")
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
                ?: throw IllegalStateException("FirebaseUser is null after login")

            var isUserUpdated = false // Variabel untuk melacak status sinkronisasi

            repeat(5) { // Retry hingga 5 kali
                if (firebaseAuth.currentUser?.uid == firebaseUser.uid) {
                    isUserUpdated = true // Tandai sebagai berhasil
                    return@repeat // Keluar dari lambda repeat
                }
                Log.w(TAG, "loginUser: firebaseAuth.currentUser belum diperbarui, mencoba ulang...")
                delay(200) // Delay 200ms
            }

            // Periksa apakah sinkronisasi berhasil
            if (!isUserUpdated) {
                throw IllegalStateException("firebaseAuth.currentUser tidak diperbarui setelah login")
            }

            Log.d(TAG, "loginUser: Successfully logged in user with UID: ${firebaseUser.uid}")
            firebaseUser
        } catch (e: Exception) {
            Log.e(TAG, "loginUser: Error during login - ${e.message}", e)
            throw e
        }
    }



    private suspend fun addUserToFireStore(user: User) {
        try {
            Log.d(TAG, "Adding user to Firestore: ${user.id}")
            firestore.collection(USERS_COLLECTION).document(user.id).set(user).await()
            Log.d(TAG, "User successfully added to Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "addUserToFireStore: ${e.message}", e)
            throw e
        }
    }

    suspend fun sendEmailVerification(): Boolean {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            try {
                user.sendEmailVerification().await()
                Log.d(TAG, "sendEmailVerification: success")
                true
            } catch (e: Exception) {
                Log.e(TAG, "sendEmailVerification: failure", e)
                false
            }
        } else {
            Log.w(TAG, "sendEmailVerification: User not logged in")
            false
        }
    }

    fun logoutUser() {
        firebaseAuth.signOut()
    }

    suspend fun getUserById(userId: String): User {
        try {
            if (userId.isBlank()) {
                throw IllegalArgumentException("User ID is invalid")
            }

            Log.d(TAG, "Fetching user data for userId: $userId")
            val document = firestore.collection(USERS_COLLECTION).document(userId).get().await()
            val user = document.toObject(User::class.java)
                ?: throw IllegalStateException("User data not found for userId: $userId")

            Log.d(TAG, "User data fetched successfully for userId: $userId")
            return user
        } catch (e: Exception) {
            Log.e(TAG, "getUserById failed for userId: $userId, error: ${e.message}", e)
            throw Exception("Failed to fetch user data: ${e.message}", e)
        }
    }

    fun getCurrentUserId(): String {
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            Log.e(TAG, "getCurrentUserId: No user is currently logged in")
            throw IllegalStateException("No user is currently logged in")
        }
        Log.d("FirebaseUserRepository", "Current user ID: ${currentUser.uid}")
        return currentUser.uid
    }

    suspend fun getUserRole(userId: String): Role {
        return try {
            val document = firestore.collection("Users").document(userId).get().await()
            val role = document.getString("role") ?: "USER"
            Role.valueOf(role.uppercase()) // 🔹 Konversi dari String ke Enum Role
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user role: ${e.message}")
            Role.USER // Default ke USER jika gagal
        }
    }
}