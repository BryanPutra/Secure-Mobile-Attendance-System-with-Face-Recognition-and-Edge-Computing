package com.example.Thesis_Project

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


data class Spacing(
    val default: Dp = 0.dp,
    val spaceXXSmall: Dp = 2.dp,
    val spaceExtraSmall: Dp = 4.dp,
    val spaceSmall: Dp = 8.dp,
    val spaceMedium: Dp = 16.dp,
    val spaceLarge: Dp = 24.dp,
    val spaceExtraLarge: Dp = 32.dp,
    val spaceXXLarge: Dp = 42.dp,
    val spaceXXXLarge: Dp = 128.dp,

    val borderRadiusDefault: Dp = 0.dp,
    val borderRadiusSmall: Dp = 6.dp,
    val borderRadiusMedium: Dp = 12.dp,
    val borderRadiusLarge: Dp = 24.dp,
    val borderRadiusExtraLarge: Dp = 60.dp,
    val borderRadiusXXLarge: Dp = 192.dp,

    val iconExtraSmall: Dp = 18.dp,
    val iconSmall: Dp = 24.dp,
    val iconMedium: Dp = 28.dp,
    val iconLarge: Dp = 42.dp,
    val iconExtraLarge: Dp = 128.dp,
    val iconXXLarge: Dp = 144.dp,
    )

val LocalSpacing = compositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current