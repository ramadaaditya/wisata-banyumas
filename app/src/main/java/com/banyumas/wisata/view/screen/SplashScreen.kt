package com.banyumas.wisata.view.screen

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.R
import com.banyumas.wisata.model.User
import com.banyumas.wisata.utils.UiState
import com.banyumas.wisata.viewmodel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: UserViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
) {
    var isVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    val alphaAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "Splash Animation"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        delay(2000)
        viewModel.checkLoginStatus()
    }

    LaunchedEffect(authState) {
        when (authState) {
            is UiState.Success -> {
                val user = (authState as UiState.Success).data
                Log.d("SplashScreen", "User yang login: $user")
                navigateToHome()
            }

            is UiState.Error -> {
                Log.e("SplashScreen", "Error saat login: ${(authState as UiState.Error).message}")
                navigateToLogin()
            }

            else -> {}
        }
    }

    SplashScreenContent(alphaAnim = alphaAnim)
}


@Composable
fun SplashScreenContent(
    alphaAnim: Float = 1f,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.icon_splash),
            contentDescription = "Splash Logo",
            modifier = Modifier
                .size(250.dp)
                .alpha(alphaAnim)
        )
    }
}
