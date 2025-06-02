package com.banyumas.wisata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.banyumas.wisata.core.common.UiState
import com.banyumas.wisata.navigation.AppNavigation
import com.banyumas.wisata.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity onCreate : Calling checkLoginStatus")
        splashScreen.setKeepOnScreenCondition {
            val currentState = viewModel.authState.value
            Timber.d("setKeepOnScreenCondition: Current authState is $currentState")
            when (currentState) {
                is UiState.Empty -> true
                is UiState.Loading -> true
                else -> false
            }
        }
        enableEdgeToEdge()
        setContent {
            AppNavigation(userViewModel = viewModel)
        }
    }
}