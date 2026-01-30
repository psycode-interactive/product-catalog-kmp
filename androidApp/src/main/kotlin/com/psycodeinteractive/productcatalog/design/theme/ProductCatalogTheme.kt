package com.psycodeinteractive.productcatalog.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006A6A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF6FF7F6),
    onPrimaryContainer = Color(0xFF002020),
    secondary = Color(0xFF4A6363),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF006A9B),
    inversePrimary = Color(0xFF4EDADA),
    surface = Color(0xFFF7FAFA),
    surfaceVariant = Color(0xFFE1ECEC),
    surfaceContainer = Color(0xFFEEF4F4),
    outlineVariant = Color(0x1A000000),
    error = Color(0xFFFF0000),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4EDADA),
    onPrimary = Color(0xFF003737),
    primaryContainer = Color(0xFF004F4F),
    onPrimaryContainer = Color(0xFF6FF7F6),
    inversePrimary = Color(0xFF006A6A),
    surface = Color(0xFF0E1414),
    surfaceVariant = Color(0xFF1C2222),
    surfaceContainer = Color(0xFF232A2A),
    outlineVariant = Color(0x1AFFFFFF),
    error = Color(0xFFFF0000)
)

object ProductCatalogTheme {
    val typography: Typography
        @Composable
        get() = LocalProductCatalogTypography.current

    val spacing: Spacing
        @Composable
        get() = LocalProductCatalogSpacing.current
}

@Composable
fun ProductCatalogTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (useDarkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(
        LocalProductCatalogTypography provides productCatalogTypography(),
        LocalProductCatalogSpacing provides productCatalogSpacing(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}
