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
import com.banyumas.wisata.view.components.PasswordInputField
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    navigateToDashboard: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        Log.d("LoginScreen", "Auth State Changed: $authState") // Tambahkan log ini

        when (val state = authState) {
            is UiState.Success -> {
                val currentRole = state.data.role
                Log.d("LoginScreen", "Login berhasil: ${state.data.name}, Role: $currentRole")

                if (currentRole == Role.ADMIN) {
                    navigateToDashboard()
                } else {
                    navigateToHome()
                }
            }

            is UiState.Error -> {
                Log.e("LoginScreen", "Login gagal: ${state.message}")
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
            }

            else -> { /* No action needed */ }
        }
    }



    LoginContent(
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSignInClick = { viewModel.loginUser(email, password) },
        isLoading = authState is UiState.Loading,
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
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.6f
                    )
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            EmailInputField(value = email, onValueChange = onEmailChange)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordInputField(value = password, onValueChange = onPasswordChange)
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Lupa Kata Sandi?", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            text = if (isLoading) "Sedang masuk..." else "Masuk",
            onClick = onSignInClick,
            enabled = email.isNotBlank() && password.isNotBlank() && !isLoading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Belum punya akun?")
            TextButton(onClick = onSignupClick) {
                Text(text = "Daftar", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}