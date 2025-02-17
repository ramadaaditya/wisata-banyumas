package com.banyumas.wisata.view.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.model.User
import com.banyumas.wisata.utils.ErrorState
import com.banyumas.wisata.utils.LoadingState
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.UserViewModel

@Composable
fun ProfileScreen(
    viewModel: UserViewModel = hiltViewModel(),
    onLogout: () -> Unit,
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    LaunchedEffect(authState) {
        if (authState is UiState.Empty) {
            onLogout()
        }
    }
    when (val state = authState) {
        is UiState.Loading -> {
            LoadingState()
        }

        is UiState.Error -> {
            ErrorState("Terjadi kesalahan : ${state.message}")
        }

        is UiState.Empty -> {}

        is UiState.Success -> {
            val user = state.data
            ProfileContent(
                user = user,
                onLogout = {
                    viewModel.logout()
                    onLogout()
                },
            )
        }
    }
}

@Composable
fun ProfileContent(
    user: User,
    onLogout: () -> Unit,
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ProfileItem(icon = Icons.Default.Person, title = user.name)
        Spacer(modifier = Modifier.height(16.dp))
        ProfileItem(icon = Icons.Default.Email, title = user.email)

        Spacer(modifier = Modifier.weight(1f))

        CustomButton(
            text = "Logout",
            onClick = { showLogoutDialog = true },
            isCancel = true,
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            iconContentDescription = "Logout",
            border = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun ProfileItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}

@Preview(
    showBackground = true
)
@Composable
fun ProfileContentPreview() {
    AppTheme {
        ProfileContent(
            user = User(
                name = "Ramados",
                email = "Ramados24@gmail.com"
            ),
            onLogout = {},
        )
    }
}