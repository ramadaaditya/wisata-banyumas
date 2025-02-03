package com.banyumas.wisata.view.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.sp
import com.banyumas.wisata.R
import com.banyumas.wisata.view.theme.AppTheme
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
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colorScheme.primary),
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
                text = "Wisata Banyumas",
                style = AppTheme.typography.titleLarge.copy(fontSize = 30.sp),
                color = AppTheme.colorScheme.onPrimary,
                modifier = Modifier.alpha(alphaAnim)
            )
        }
    }
}