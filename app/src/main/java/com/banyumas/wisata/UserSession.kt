package com.banyumas.wisata

import androidx.compose.runtime.compositionLocalOf
import com.banyumas.wisata.core.model.User

/**
 * CompositionLocal untuk menyediakan data User secara implisit ke seluruh
 * hierarki Composable di bawah provider-nya.
 * Default-nya adalah null, menandakan tidak ada user yang login.
 */
val LocalUser = compositionLocalOf<User?> { null }