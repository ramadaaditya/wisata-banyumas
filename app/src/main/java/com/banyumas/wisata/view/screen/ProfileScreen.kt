package com.banyumas.wisata.view.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.banyumas.wisata.data.model.User
import com.banyumas.wisata.view.components.CustomButton
import com.banyumas.wisata.view.theme.AppTheme
import com.banyumas.wisata.viewmodel.AppViewModel

@Composable
fun ProfileScreen(
    user: User,
    viewModel: AppViewModel = hiltViewModel(),
    onLogout: () -> Unit,
    innerPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(innerPadding)
    ) {
        // Informasi profil
        ProfileItem(icon = Icons.Default.Person, title = user.name)
        Spacer(modifier = Modifier.height(16.dp))
        ProfileItem(icon = Icons.Default.Email, title = user.email)

        // Spacer fleksibel untuk memindahkan tombol Logout ke bawah
        Spacer(modifier = Modifier.weight(1f))

        // Tombol Logout
        CustomButton(
            text = "Logout",
            onClick = {
                viewModel.logout()
                onLogout()
            },
            isCancel = true,
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            iconContentDescription = "Logout",
            border = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun ProfileItem(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
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
            style = AppTheme.typography.body,
            color = Color.Black
        )
    }
}