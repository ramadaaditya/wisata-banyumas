package com.banyumas.wisata.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.designsystem.components.CustomButton
import com.banyumas.wisata.core.designsystem.components.EmailInputField
import com.banyumas.wisata.core.designsystem.components.PasswordInputField
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.core.model.User
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navigateToHome: (User) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    viewModel: UserViewModel
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authState) {
        when (val state = authState) {
            is UiState.Success -> {
                focusManager.clearFocus()
                navigateToHome(state.data)
            }

            is UiState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message.asString(context)
                    )
                }
            }

            else -> {}
        }
    }
    LoginContent(
        snackbarHostState = snackbarHostState,
        email = email,
        password = password,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSignInClick = {
            focusManager.clearFocus() // Sembunyikan keyboard saat tombol ditekan
            viewModel.loginUser(email, password)
        },
        onSignupClick = onSignupClick,
        onForgotPasswordClick = onForgotPasswordClick,
        isLoading = authState is UiState.Loading
    )
}

@Composable
fun LoginContent(
    snackbarHostState: SnackbarHostState,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignupClick: () -> Unit,
    onSignInClick: () -> Unit,
    isLoading: Boolean
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Selamat Datang Kembali", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Masuk untuk melanjutkan",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))
            EmailInputField(value = email, onValueChange = onEmailChange, enabled = !isLoading)
            Spacer(modifier = Modifier.height(16.dp))
            PasswordInputField(
                value = password,
                onValueChange = onPasswordChange,
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onForgotPasswordClick, enabled = !isLoading) {
                    Text(text = "Lupa Kata Sandi?")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                text = if (isLoading) "Memproses..." else "Masuk",
                onClick = onSignInClick,
                enabled = email.isNotBlank() && password.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Belum punya akun?")
                TextButton(onClick = onSignupClick, enabled = !isLoading) {
                    Text(text = "Daftar di sini")
                }
            }
        }
    }
}


@Preview(showBackground = true, device = Devices.PIXEL_4, name = "Default State")
@Composable
private fun LoginContentPreview() {
    WisataBanyumasTheme {
        LoginContent(
            snackbarHostState = remember { SnackbarHostState() },
            onPasswordChange = {},
            onForgotPasswordClick = {},
            isLoading = false,
            onEmailChange = {},
            onSignInClick = {},
            onSignupClick = {},
            email = "email@example.com",
            password = "password"
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4, name = "Loading State")
@Composable
private fun LoginContentLoadingPreview() {
    WisataBanyumasTheme {
        LoginContent(
            snackbarHostState = remember { SnackbarHostState() },
            onPasswordChange = {},
            onForgotPasswordClick = {},
            isLoading = true, // <-- State loading
            onEmailChange = {},
            onSignInClick = {},
            onSignupClick = {},
            email = "email@example.com",
            password = "password"
        )
    }
}