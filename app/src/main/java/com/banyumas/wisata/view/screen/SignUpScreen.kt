package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.EmailTextField
import com.banyumas.wisata.view.components.PasswordTextField
import com.banyumas.wisata.view.components.UsernameTextField
import com.banyumas.wisata.viewmodel.AppViewModel

@Composable
fun RegisterScreen(
    onSignInClick: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Success -> {
                onRegisterSuccess()
                viewModel.resetAuthState()
            }

            is UiState.Error -> {
                errorMessage = (authState as UiState.Error).message
            }

            else -> {
                errorMessage = null
            }
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
        onSignUpClick = { viewModel.registerUser(email, password, username) },
        errorMessage = errorMessage
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
    errorMessage: String?
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
                text = "Sign Up Now",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Please fill in the details to create an account",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UsernameTextField(
                value = username,
                onValueChange = onUsernameChange
            )

            EmailTextField(
                email = email,
                onValueChange = onEmailChange
            )

            PasswordTextField(
                value = password,
                onValueChange = onPasswordChange
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            ErrorState(message = it)
        }


        // Buttons and Footer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = onSignUpClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(48.dp),
            ) {
                Text(text = "Sign Up", color = Color.White)
            }


            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Already have an account?")
                TextButton(onClick = onSignInClick) {
                    Text(text = "Sign in", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}