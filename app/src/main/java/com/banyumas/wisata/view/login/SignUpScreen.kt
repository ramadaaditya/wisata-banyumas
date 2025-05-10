package com.banyumas.wisata.view.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.BackIcon
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.EmailInputField
import com.banyumas.wisata.view.components.PasswordInputField
import com.banyumas.wisata.view.components.UsernameInputField
import com.banyumas.wisata.view.theme.WisataBanyumasTheme
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun RegisterScreen(
    onSignInClick: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val isLoading = authState is UiState.Loading

    LaunchedEffect(authState) {
        when (val state = authState) {
            is UiState.Success -> {
                Toast.makeText(
                    context,
                    "Akun berhasil dibuat. Silahkan cek email untuk verifikasi.",
                    Toast.LENGTH_LONG
                ).show()
            }

            is UiState.Error -> {
                Toast.makeText(
                    context,
                    "Registrasi gagal: ${state.message.asString(context)}",
                    Toast.LENGTH_LONG
                ).show()
            }

            else -> Unit
        }
    }

    RegisterContent(
        email = email,
        password = password,
        username = username,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onUsernameChange = { username = it },
        onSignInClick = onSignInClick,
        isLoading = isLoading,
        onSignUpClick = {
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
    Box {
        BackIcon(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            onClick = onSignInClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Daftar Sekarang",
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Lengkapi formulir untuk membuat akun",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ),
            )
            Spacer(modifier = Modifier.height(32.dp))

            UsernameInputField(value = username, onValueChange = onUsernameChange)
            Spacer(modifier = Modifier.height(8.dp))
            EmailInputField(value = email, onValueChange = onEmailChange)
            Spacer(modifier = Modifier.height(8.dp))
            PasswordInputField(value = password, onValueChange = onPasswordChange)
            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                onClick = onSignUpClick,
                text = if (isLoading) "Sedang daftar..." else "Daftar",
                enabled = username.isNotBlank() && password.isNotBlank() && username.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

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
    WisataBanyumasTheme {
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