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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.view.components.EmailTextField
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun ForgotPasswordScreen(
    onSignInClick: () -> Unit = {},
    onSignUpCLick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onGoogleSignInClick: () -> Unit = {},
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
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
        ) {

            Text(
                text = "Forgot Password",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Enter your email to reset your password",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            // Email TextField
            EmailTextField(
                value = email,
                onValueChange = { email = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons and Footer
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = onSignInClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(48.dp),
            ) {
                Text(text = "Reset Password", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    WisataBanyumasTheme {
        ForgotPasswordScreen(
            onSignInClick = {},
            onSignUpCLick = {},
            onForgotPasswordClick = {},
            onGoogleSignInClick = {}
        )
    }
}