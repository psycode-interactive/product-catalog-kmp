package com.psycodeinteractive.productcatalog.design.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val LocalProductCatalogSpacing = staticCompositionLocalOf { Spacing() }

@Immutable
data class Spacing(
    val half: Dp = Dp.Unspecified,
    val full: Dp = Dp.Unspecified,
    val double: Dp = Dp.Unspecified,
)

fun productCatalogSpacing() = Spacing(
    half = 8.dp,
    full = 16.dp,
    double = 32.dp,
)
