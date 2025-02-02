package com.banyumas.wisata.view.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banyumas.wisata.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }

    // Animasi fade-in
    val alphaAnim by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000),
        label = "Splash Animation"
    )

    LaunchedEffect(Unit) {
        isVisible = true
        delay(2000) // Durasi splash screen (2 detik)
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.splash_icon), // Ganti dengan logo aplikasi
                contentDescription = "Splash Logo",
                modifier = Modifier
                    .size(150.dp)
                    .alpha(alphaAnim)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Banyumas Wisata",
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 24.sp),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}