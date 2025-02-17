package com.banyumas.wisata.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.banyumas.wisata.view.theme.AppTheme


@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AppTheme.colorScheme.onBackground) },
        leadingIcon = leadingIcon,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Sembunyikan Password" else "Lihat Password"
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Gray,
            cursorColor = AppTheme.colorScheme.primary,
            focusedLabelColor = AppTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun EmailInputField(value: String, onValueChange: (String) -> Unit) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Email",
        leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon") }
    )
}

@Composable
fun PasswordInputField(value: String, onValueChange: (String) -> Unit) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Kata Sandi",
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon") },
        isPassword = true
    )
}


@Composable
fun UsernameInputField(value: String, onValueChange: (String) -> Unit) {
    CustomTextField(
        value = value,
        onValueChange = onValueChange,
        label = "Username",
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Person icon"
            )
        },
    )
}