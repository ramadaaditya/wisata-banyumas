package com.banyumas.wisata

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.banyumas.wisata.core.designsystem.theme.WisataBanyumasTheme
import com.banyumas.wisata.ui.WbApp
import com.banyumas.wisata.ui.rememberWbAppState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainActivityViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        Timber.d("MainActivity onCreate : Calling checkLoginStatus")
        splashScreen.setKeepOnScreenCondition {
            viewModel.uiState.value is MainActivityUiState.Loading
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            )
        )
        setContent {
            val appState = rememberWbAppState()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            Timber.tag(TAG).d("Status pengguna : $uiState")
            WisataBanyumasTheme {
                when (val state = uiState) {
                    is MainActivityUiState.Loading -> {
//                        TODO("Fill with Loading Screen")
                    }

                    is MainActivityUiState.Success -> {
                        CompositionLocalProvider(LocalUser provides state.user) {
                            WbApp(appState)
                        }
                    }

                    MainActivityUiState.Error -> {
                        CompositionLocalProvider(LocalUser provides null) {
                            WbApp(appState)
                        }
                    }
                }
            }
        }
    }
}