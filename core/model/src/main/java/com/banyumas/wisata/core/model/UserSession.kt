package com.banyumas.wisata.core.model

import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal untuk menyediakan data User secara implisit ke seluruh
 * hierarki Composable di bawah provider-nya.
 * Default-nya adalah null, menandakan tidak ada user yang login.
 */
val LocalUser = compositionLocalOf<User?> { null }