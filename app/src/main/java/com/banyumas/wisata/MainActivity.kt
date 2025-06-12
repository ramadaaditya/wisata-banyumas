package com.banyumas.wisata

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import com.banyumas.wisata.core.model.LocalUser


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity onCreate : Calling checkLoginStatus")
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is MainActivityUiState.Loading
        }
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            WisataBanyumasTheme {
                when (val state = uiState) {
                    is MainActivityUiState.Loading -> {}
                    is MainActivityUiState.Success -> {
                        CompositionLocalProvider(LocalUser provides state.user) {
                            AppNavigation()
                        }
                    }
                }
            }
        }
    }
}