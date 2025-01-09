package com.banyumas.wisata.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.banyumas.wisata.view.navigation.Screen
import com.banyumas.wisata.view.theme.AppTheme

@Composable
fun SplashScreen(
    navController: NavController,
    checkLoginStatus: () -> Boolean,
    checkGoogleSignInStatus: () -> Boolean,
    getUserRole: suspend () -> String
) {
    // Mulai logika navigasi dengan efek side effect
    LaunchedEffect(Unit) {
        // Simulasi delay untuk Splash Screen (contoh 2 detik)
        kotlinx.coroutines.delay(2000)

        val isLoggedIn = checkLoginStatus()
        val isGoogleSignIn = checkGoogleSignInStatus()

        when {
            isLoggedIn || isGoogleSignIn -> {
                //cek role user
                val role = getUserRole()
                when (role) {
                    "ADMIN" -> {
                        // Navigasi ke AdminScreen jika role admin
                        navController.navigate(Screen.DashboardScreen.route) {
                            popUpTo(Screen.SplashScreen.route) { inclusive = true }
                        }
                    }

                    "USER" -> {
                        // Navigasi ke UserScreen jika role user
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.SplashScreen.route) { inclusive = true }
                        }
                    }
                }
            }

            else -> {
                // Navigasi ke Login jika belum login
                navController.navigate(Screen.LoginScreen.route) {
                    popUpTo(Screen.SplashScreen.route) { inclusive = true }
                }
            }
        }
    }

    // UI untuk SplashScreen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Splash Screen", style = MaterialTheme.typography.titleLarge)
    }
}