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

@Composable
fun SplashScreen(
    viewModel: UserViewModel = hiltViewModel(),
    navigateToLogin: () -> Unit,
    navigateToHome: (User) -> Unit,
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
        viewModel.checkLoginStatus()
    }

    LaunchedEffect(authState) {
        Log.d("SPLASH", "authState: $authState")
        when (val state = authState) {
            is UiState.Success -> {
                val user = state.data
                Log.d("SPLASH", "Navigating to home with user ${user.id}")
                navigateToHome(user)
            }

            is UiState.Error -> {
                Log.d("SPLASH", "Navigating to login due to error")
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
