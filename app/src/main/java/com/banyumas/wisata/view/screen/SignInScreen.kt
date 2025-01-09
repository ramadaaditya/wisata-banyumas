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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.banyumas.wisata.R
import com.banyumas.wisata.view.components.EmailTextField
import com.banyumas.wisata.view.components.PasswordTextField
import com.banyumas.wisata.view.theme.WisataBanyumasTheme

@Composable
fun LoginScreen(
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
                text = "Login to Wisata Banyumas",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Login to your account to continue",
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

            // Password TextField
            PasswordTextField(
                value = password,
                onValueChange = { password = it }
            )

            // Forget Password
            TextButton(onClick = onForgotPasswordClick, modifier = Modifier.align(Alignment.End)) {
                Text(text = "Forget Password?", color = MaterialTheme.colorScheme.primary)
            }
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
                Text(text = "Sign In", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Don't have an account?")
                TextButton(onClick = onSignUpCLick) {
                    Text(text = "Sign up", color = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Or connect",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Google Sign-In
            IconButton(onClick = onGoogleSignInClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google Sign-In",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}


//@Composable
//fun GoogleSignInButton() {
//    val coroutineScope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    val onClick: () -> Unit = {
//        val credentialManager = CredentialManager.create(context)
//
//        // Generate a nonce and hash it with sha-256
//        // Providing a nonce is optional but recommended
//        val rawNonce = UUID.randomUUID()
//            .toString() // Generate a random String. UUID should be sufficient, but can also be any other random string.
//        val bytes = rawNonce.toByteArray()
//        val md = MessageDigest.getInstance("SHA-256")
//        val digest = md.digest(bytes)
//        val hashedNonce =
//            digest.fold("") { str, it -> str + "%02x".format(it) } // Hashed nonce to be passed to Google sign-in
//
//        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
//            .setServerClientId("")
//            .setNonce(hashedNonce) // Provide the nonce if you have one
//            .build()
//
//        val request: GetCredentialRequest = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        coroutineScope.launch {
//            try {
//                val result = credentialManager.getCredential(
//                    request = request,
//                    context = context,
//                )
//
//                val googleIdTokenCredential = GoogleIdTokenCredential
//                    .createFrom(result.credential.data)
//
//                val googleIdToken = googleIdTokenCredential.idToken
////
////                supabase.auth.signInWith(IDToken) {
////                    idToken = googleIdToken
////                    provider = Google
////                    nonce = rawNonce
////                }
//
//                // Handle successful sign-in
//            } catch (e: GetCredentialException) {
//                // Handle GetCredentialException thrown by `credentialManager.getCredential()`
//            } catch (e: GoogleIdTokenParsingException) {
//                // Handle GoogleIdTokenParsingException thrown by `GoogleIdTokenCredential.createFrom()`
//            } catch (e: RestException) {
//                // Handle RestException thrown by Supabase
//            } catch (e: Exception) {
//                // Handle unknown exceptions
//            }
//        }
//    }
//
//    Button(
//        onClick = onClick,
//    ) {
//        Text("Sign in with Google")
//    }
//}


@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    WisataBanyumasTheme {
        LoginScreen(
            onSignInClick = {},
            onSignUpCLick = {},
            onForgotPasswordClick = {},
            onGoogleSignInClick = {}
        )
    }
}