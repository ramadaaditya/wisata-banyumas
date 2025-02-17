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
import androidx.compose.material3.MaterialTheme
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
import com.banyumas.wisata.view.theme.AppTheme


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        maxLines = 1,
        value = value,
        onValueChange = onValueChange,
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
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            focusedTrailingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        modifier = modifier.fillMaxWidth(),
        label = { Text(label) }
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

@Preview(showBackground = true)
@Composable
fun EmailPreview() {
    AppTheme {
        EmailInputField(
            onValueChange = {},
            value = "Ramada@gmail.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordPreview() {
    AppTheme {
        PasswordInputField(
            onValueChange = {},
            value = "Ramada@gmail.com"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UsernamePreview() {
    AppTheme {
        UsernameInputField(
            onValueChange = {},
            value = "Ramada@gmail.com"
        )
    }
}