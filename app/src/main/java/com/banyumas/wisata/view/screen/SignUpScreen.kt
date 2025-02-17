package com.banyumas.wisata.view.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.EmailInputField
import com.banyumas.wisata.view.components.PasswordInputField
import com.banyumas.wisata.view.components.UsernameInputField
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    onSignInClick: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    var hasAttemptedSignup by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is UiState.Success -> {
                // Toast ketika sukses register
                Toast.makeText(
                    context,
                    "Akun berhasil dibuat. Silakan cek email untuk verifikasi.",
                    Toast.LENGTH_LONG
                ).show()
            }

            is UiState.Error -> {
                if (hasAttemptedSignup) {
                    isLoading = false
                    Toast.makeText(
                        context,
                        "Registrasi gagal: ${state.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            is UiState.Empty -> {
                isLoading = false
            }

            is UiState.Loading -> {
                isLoading = true
            }
        }
    }

    RegisterContent(
        email = email,
        password = password,
        username = username,
        onEmailChange = { email = it },
        isLoading = isLoading,
        onPasswordChange = { password = it },
        onUsernameChange = { username = it },
        onSignInClick = onSignInClick,
        onSignUpClick = {
            hasAttemptedSignup = true
            viewModel.registerUser(email, password, username)
        },
    )
}

@Composable
private fun RegisterContent(
    email: String,
    password: String,
    username: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
    isLoading: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onSignInClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Daftar Sekarang",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Lengkapi formulir untuk membuat akun",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UsernameInputField(
                value = username,
                onValueChange = onUsernameChange
            )

            Spacer(modifier = Modifier.height(8.dp))


            EmailInputField(
                value = email,
                onValueChange = onEmailChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordInputField(
                value = password,
                onValueChange = onPasswordChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons and Footer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CustomButton(
                onClick = onSignUpClick,
                text = if (isLoading) "Sedang daftar..." else "Daftar",
                enabled = username.isNotBlank() && password.isNotBlank() && username.isNotBlank() && !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Sudah memiliki akun ? ")
                TextButton(onClick = onSignInClick) {
                    Text(text = "Masuk", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignupScreenPreview() {
    AppTheme {
        RegisterContent(
            email = "mramadaaditya@gmail.com",
            username = "Ramada",
            password = "112323123",
            onSignInClick = {},
            onSignUpClick = {},
            onUsernameChange = {},
            onPasswordChange = {},
            onEmailChange = {},
            isLoading = false
        )
    }

}