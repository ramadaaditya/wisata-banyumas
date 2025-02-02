package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.data.model.Role
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.EmailTextField
import com.banyumas.wisata.view.components.PasswordTextField
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.AppViewModel

@Composable
fun LoginScreen(
    navigateToDestination: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var hasAttemptedLogin by remember { mutableStateOf(false) } // 🔹 Tambahkan variabel ini

    LaunchedEffect(authState) {
        when (val state = authState) {
            is UiState.Success -> {
                val userId = state.data.id
                if (userId.isNotBlank()) {
                    Log.d("LoginScreen", "AuthState is success, userId: $userId")
                    val destination = if (viewModel.userRole == Role.ADMIN) {
                        Screen.DashboardScreen.route
                    } else {
                        Screen.Home.ROUTE
                    }
                    Log.d("LoginScreen", "Navigating to $destination with userId: $userId")
                    navigateToDestination(destination)
                } else {
                    Log.e("LoginScreen", "userId is blank, cannot navigate")
                }
            }

            is UiState.Error -> {
                if (hasAttemptedLogin) { // 🔹 Hanya tampilkan error jika sudah mencoba login
                    isLoading = false
                    errorMessage = state.message
                    Log.e("LoginScreen", "Login failed: $errorMessage")
                }
            }

            is UiState.Loading -> {
                isLoading = true
                Log.d("LoginScreen", "Loading state...")
            }

            UiState.Empty -> {
                isLoading = false
                errorMessage = null
                Log.d("LoginScreen", "Auth state is empty")
            }
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
        errorMessage = errorMessage,
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
    errorMessage: String?,
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
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(
                text = "Login to Wisata Banyumas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Login to your account to continue",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Email TextField
            EmailTextField(
                email = email,
                onValueChange = onEmailChange
            )

            // Password TextField
            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange
            )

            // Forget Password
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Forget Password?", color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tampilkan pesan error jika ada
        errorMessage?.let {
            ErrorState(message = it)
        }

        // Buttons and Footer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = onSignInClick,
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(48.dp),
            ) {
                if (isLoading) {
                    LoadingState(modifier = Modifier.fillMaxWidth())
                } else {
                    Text(text = "Masuk", color = AppTheme.colorScheme.background)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Don't have an account?")
                TextButton(onClick = onSignupClick) {
                    Text(text = "Sign up", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
