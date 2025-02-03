package com.banyumas.wisata.view.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.EmailInputField
import com.banyumas.wisata.view.components.EmailTextField
import com.banyumas.wisata.view.components.PasswordInputField
import com.banyumas.wisata.view.components.PasswordTextField
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.viewmodel.AppViewModel

@Composable
fun LoginScreen(
    navigateToDestination: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    val userRoleState by viewModel.userRoleState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasAttemptedLogin by remember { mutableStateOf(false) } // 🔹 Tambahkan variabel ini

    LaunchedEffect(authState) {
        when (val state = authState) {
            is UiState.Success -> {
                val userId = state.data.id
                if (userId.isNotBlank() && userRoleState !is UiState.Success) {
                    Log.d("LoginScreen", "AuthState success, userId: $userId")
                    // 🔥 Hanya panggil fetchUserRole jika belum ada role yang berhasil
                    viewModel.fetchUserRole(userId)
                }
            }

            is UiState.Error -> {
                if (hasAttemptedLogin) {
                    isLoading = false
                    Toast.makeText(context, "Login gagal: ${state.message}", Toast.LENGTH_LONG)
                        .show()
                }
            }

            is UiState.Loading -> {
                isLoading = true
            }

            UiState.Empty -> {
                isLoading = false
            }
        }
    }

    // 🔥 Tunggu hingga role diperbarui sebelum berpindah halaman
    LaunchedEffect(userRoleState) {
        if (userRoleState is UiState.Success && authState is UiState.Success) {
            val userId = (authState as UiState.Success).data.id
            val role = (userRoleState as UiState.Success<Role>).data
            val destination = if (role == Role.ADMIN) {
                Screen.DashboardScreen.createRoute(userId)
            } else {
                Screen.Home.createRoute(userId)
            }
            Log.d("LoginScreen", "Navigasi ke $destination dengan role: $role")
            navigateToDestination(destination)
        }
    }

    LoginContent(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSignInClick = {
            hasAttemptedLogin = true  // 🔹 Set true saat pengguna menekan login
            viewModel.loginUser(email, password)
        },
        isLoading = isLoading,
        onSignupClick = onSignupClick,
        onForgotPasswordClick = onForgotPasswordClick
    )
}
@Composable
fun LoginContent(
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    onSignInClick: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Masuk ke Wisata Banyumas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Silakan masuk untuk melanjutkan",
                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // TextField Email
            EmailInputField(value = email, onValueChange = onEmailChange)

            Spacer(modifier = Modifier.height(8.dp))

            // 🔥 Menggunakan CustomTextField untuk Password
            PasswordInputField(value = password, onValueChange = onPasswordChange)

            // Tombol Lupa Kata Sandi
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Lupa Kata Sandi?", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔥 Menggunakan CustomButton untuk tombol login
        CustomButton(
            text = if (isLoading) "Sedang masuk..." else "Masuk",
            onClick = onSignInClick,
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Footer: Daftar Akun
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Belum punya akun?")
            TextButton(onClick = onSignupClick) {
                Text(text = "Daftar", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}