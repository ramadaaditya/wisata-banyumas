package com.banyumas.wisata.view.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.components.EmailInputField
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun ResetPasswordScreen(
    onSignInClick: () -> Unit,
    onResetPasswordSuccess: () -> Unit,
    viewModel: UserViewModel = hiltViewModel(),
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    val resetPasswordState by viewModel.resetPasswordState.collectAsStateWithLifecycle()

    var isLoading by remember { mutableStateOf(false) }
    var showMessage by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Observasi hasil reset password
    LaunchedEffect(resetPasswordState) {
        when (val state = resetPasswordState) {
            is UiState.Loading -> isLoading = true
            is UiState.Success -> {
                isLoading = false
                showMessage = "Email reset kata sandi telah dikirim!"
                Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show()
                onResetPasswordSuccess()
            }
            is UiState.Error -> {
                isLoading = false
                showMessage = state.message
                Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show() // 🔥 Tambahkan Toast
            }
            else -> Unit
        }
    }

    // Tampilkan Snackbar jika ada pesan
    LaunchedEffect(showMessage) {
        showMessage?.let {
            snackbarHostState.showSnackbar(it)
            showMessage = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
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
                    contentDescription = "Kembali"
                )
            }
        }

        Spacer(modifier = Modifier.height(56.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Reset Kata Sandi",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Masukkan email untuk reset kata sandi.",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.Gray),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Form Input
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            EmailInputField(
                value = email,
                onValueChange = { email = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tombol Reset Password
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CustomButton(
                onClick = {
                    if (!isValidEmail(email)) {
                        showMessage = "Format email tidak valid!"
                        Toast.makeText(context, showMessage, Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.requestPasswordReset(email)
                    }
                },
                text = if (isLoading) "Memproses..." else "Reset Kata Sandi",
                enabled = email.isNotBlank() && !isLoading
            )
        }
    }
}
