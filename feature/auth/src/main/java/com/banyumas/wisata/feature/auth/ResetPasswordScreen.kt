package com.banyumas.wisata.feature.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.core.designsystem.components.CustomButton
import com.banyumas.wisata.core.designsystem.components.EmailInputField
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme

@Composable
fun ResetPasswordScreen(
    onSignInClick: () -> Unit,
    viewModel: UserViewModel,
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val resetPasswordState by viewModel.authState.collectAsStateWithLifecycle()
    var isLoading = resetPasswordState is UiState.Loading

    val isValidEmail = remember(email) {
        Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    LaunchedEffect(resetPasswordState) {
        when (val state = resetPasswordState) {
            is UiState.Loading -> isLoading = true
            is UiState.Success -> {
                isLoading = false
                Toast.makeText(context, "Email reset kata sandi telah dikirim!", Toast.LENGTH_SHORT)
                    .show()
            }

            is UiState.Error -> {
            }

            else -> Unit
        }
    }

    ResetPasswordContent(
        email = email,
        onResetClick = {
            viewModel.resetPassword(email)
        },
        onSignInClick = onSignInClick,
        onEmailChange = { email = it },
        isLoading = isLoading,
        isValidEmail = isValidEmail
    )
}

@Composable
fun ResetPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    onResetClick: () -> Unit,
    onSignInClick: () -> Unit,
    isLoading: Boolean,
    isValidEmail: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Reset Kata Sandi",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Masukkan email untuk reset kata sandi.",
            style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        EmailInputField(
            value = email,
            onValueChange = onEmailChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        CustomButton(
            onClick = onResetClick,
            text = if (isLoading) "Memproses..." else "Reset Kata Sandi",
            enabled = isValidEmail && !isLoading,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun ResetPasswordContentPreview() {
    WisataBanyumasTheme {
        ResetPasswordContent(
            onSignInClick = {},
            onResetClick = {},
            onEmailChange = {},
            email = "Ramados",
            isLoading = false,
            isValidEmail = true
        )
    }
}