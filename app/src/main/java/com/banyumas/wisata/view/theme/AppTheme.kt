package com.banyumas.wisata.view.theme

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val darkColorScheme = AppColorScheme(
    background = Color(0xFF121212),
    onBackground = Color(0xFF121212),
    primary = Color(0xFFBB86FC),
    onPrimary = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF3700B3),
)

private val lightColorScheme = AppColorScheme(
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    primary = Color(0xFFC05E2B),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF7D848D),
    onSecondary = Color(0xFF000000),
)

private val typography = AppTypography(
    titleLarge = TextStyle(
        fontFamily = OpenSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = OpenSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    body = TextStyle(
        fontFamily = OpenSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = OpenSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
    ),
    labelNormal = TextStyle(
        fontFamily = OpenSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = OpenSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
    ),
)

private val shape = AppShape(
    container = RoundedCornerShape(16.dp),
    button = RoundedCornerShape(16.dp),
)

private val size = AppSize(
    large = 24.dp,
    medium = 16.dp,
    normal = 12.dp,
    small = 8.dp,
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkTheme) darkColorScheme else lightColorScheme
    val rippleIndication = rememberRipple()

    CompositionLocalProvider(
        LocalAppColorScheme provides colorScheme,
        LocalAppTypography provides typography,
        LocalAppShape provides shape,
        LocalAppSize provides size,
        LocalIndication provides rippleIndication,
        content = content
    )
}

object AppTheme {
    val colorScheme: AppColorScheme
        @Composable get() = LocalAppColorScheme.current

    val typography: AppTypography
        @Composable get() = LocalAppTypography.current

    val shape: AppShape
        @Composable get() = LocalAppShape.current

    val size: AppSize
        @Composable get() = LocalAppSize.current
}
