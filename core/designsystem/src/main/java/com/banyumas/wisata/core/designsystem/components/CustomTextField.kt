package com.banyumas.wisata.core.designsystem.components

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.banyumas.wisata.view.theme.BanyumasTheme
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BanyumasTheme.colors.primary,
            unfocusedBorderColor = BanyumasTheme.colors.outline,
            cursorColor = BanyumasTheme.colors.primary,
            focusedLabelColor = BanyumasTheme.colors.primary,
            unfocusedLabelColor = BanyumasTheme.colors.onSurface.copy(alpha = 0.6f),
            focusedLeadingIconColor = BanyumasTheme.colors.primary,
            unfocusedLeadingIconColor = BanyumasTheme.colors.onSurface.copy(alpha = 0.6f),
            focusedTrailingIconColor = BanyumasTheme.colors.primary,
            unfocusedTrailingIconColor = BanyumasTheme.colors.onSurface.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth(),
        label = { Text(label) },
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
    var isPasswordVisible by remember { mutableStateOf(false) }
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        label = { Text("Kata Sandi") },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock Icon") },
        trailingIcon = {
            if (isPasswordVisible) {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Sembunyikan Password" else "Lihat Password"
                    )
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BanyumasTheme.colors.primary,
            unfocusedBorderColor = BanyumasTheme.colors.outline,
            cursorColor = BanyumasTheme.colors.primary,
            focusedLabelColor = BanyumasTheme.colors.primary,
            unfocusedLabelColor = BanyumasTheme.colors.onSurface.copy(alpha = 0.6f),
            focusedLeadingIconColor = BanyumasTheme.colors.primary,
            unfocusedLeadingIconColor = BanyumasTheme.colors.onSurface.copy(alpha = 0.6f),
            focusedTrailingIconColor = BanyumasTheme.colors.primary,
            unfocusedTrailingIconColor = BanyumasTheme.colors.onSurface.copy(alpha = 0.6f)
        ),
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

@Preview(showBackground = true)
@Composable
fun EmailPreview() {
    WisataBanyumasTheme {
        EmailInputField(
            onValueChange = {},
            value = "Ramada@gmail.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordPreview() {
    WisataBanyumasTheme {
        PasswordInputField(
            onValueChange = {},
            value = "Ramada@gmail.com",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UsernamePreview() {
    WisataBanyumasTheme {
        UsernameInputField(
            onValueChange = {},
            value = "Ramada@gmail.com"
        )
    }
}