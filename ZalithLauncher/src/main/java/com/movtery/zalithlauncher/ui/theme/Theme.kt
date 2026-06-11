/*
 * Apex Launcher
 * Theme Manager
 */

package com.movtery.zalithlauncher.ui.theme

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme
import com.movtery.zalithlauncher.ui.theme.components.activeMaskView
import com.movtery.zalithlauncher.utils.festival.Festival
import com.movtery.zalithlauncher.utils.festival.LocalFestivals
import com.movtery.zalithlauncher.viewmodel.BackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel

// ==========================================
// ☀️ APEX LIGHT SCHEME
// ==========================================
private val apexLight = lightColorScheme(
    primary = primaryLight.embermire,
    onPrimary = onPrimaryLight.embermire,
    primaryContainer = primaryContainerLight.embermire,
    onPrimaryContainer = onPrimaryContainerLight.embermire,
    secondary = secondaryLight.embermire,
    onSecondary = onSecondaryLight.embermire,
    secondaryContainer = secondaryContainerLight.embermire,
    onSecondaryContainer = onSecondaryContainerLight.embermire,
    tertiary = tertiaryLight.embermire,
    onTertiary = onTertiaryLight.embermire,
    tertiaryContainer = tertiaryContainerLight.embermire,
    onTertiaryContainer = onTertiaryContainerLight.embermire,
    error = errorLight.embermire,
    onError = onErrorLight.embermire,
    errorContainer = errorContainerLight.embermire,
    onErrorContainer = onErrorContainerLight.embermire,
    background = backgroundLight.embermire,
    onBackground = onBackgroundLight.embermire,
    surface = surfaceLight.embermire,
    onSurface = onSurfaceLight.embermire,
    surfaceVariant = surfaceVariantLight.embermire,
    onSurfaceVariant = onSurfaceVariantLight.embermire,
    outline = outlineLight.embermire,
    outlineVariant = outlineVariantLight.embermire,
    scrim = scrimLight.embermire,
    inverseSurface = inverseSurfaceLight.embermire,
    inverseOnSurface = inverseOnSurfaceLight.embermire,
    inversePrimary = inversePrimaryLight.embermire,
    surfaceDim = surfaceDimLight.embermire,
    surfaceBright = surfaceBrightLight.embermire,
    surfaceContainerLowest = surfaceContainerLowestLight.embermire,
    surfaceContainerLow = surfaceContainerLowLight.embermire,
    surfaceContainer = surfaceContainerLight.embermire,
    surfaceContainerHigh = surfaceContainerHighLight.embermire,
    surfaceContainerHighest = surfaceContainerHighestLight.embermire,
)

// ==========================================
// 🌙 APEX DARK SCHEME (Deep Space Black & Neon Purple)
// ==========================================
private val apexDark = darkColorScheme(
    primary = primaryDark.embermire,
    onPrimary = onPrimaryDark.embermire,
    primaryContainer = primaryContainerDark.embermire,
    onPrimaryContainer = onPrimaryContainerDark.embermire,
    secondary = secondaryDark.embermire,
    onSecondary = onSecondaryDark.embermire,
    secondaryContainer = secondaryContainerDark.embermire,
    onSecondaryContainer = onSecondaryContainerDark.embermire,
    tertiary = tertiaryDark.embermire,
    onTertiary = onTertiaryDark.embermire,
    tertiaryContainer = tertiaryContainerDark.embermire,
    onTertiaryContainer = onTertiaryContainerDark.embermire,
    error = errorDark.embermire,
    onError = onErrorDark.embermire,
    errorContainer = errorContainerDark.embermire,
    onErrorContainer = onErrorContainerDark.embermire,
    background = backgroundDark.embermire,
    onBackground = onBackgroundDark.embermire,
    surface = surfaceDark.embermire,
    onSurface = onSurfaceDark.embermire,
    surfaceVariant = surfaceVariantDark.embermire,
    onSurfaceVariant = onSurfaceVariantDark.embermire,
    outline = outlineDark.embermire,
    outlineVariant = outlineVariantDark.embermire,
    scrim = scrimDark.embermire,
    inverseSurface = inverseSurfaceDark.embermire,
    inverseOnSurface = inverseOnSurfaceDark.embermire,
    inversePrimary = inversePrimaryDark.embermire,
    surfaceDim = surfaceDimDark.embermire,
    surfaceBright = surfaceBrightDark.embermire,
    surfaceContainerLowest = surfaceContainerLowestDark.embermire,
    surfaceContainerLow = surfaceContainerLowDark.embermire,
    surfaceContainer = surfaceContainerDark.embermire,
    surfaceContainerHigh = surfaceContainerHighDark.embermire,
    surfaceContainerHighest = surfaceContainerHighestDark.embermire,
)

@Composable
fun ZalithLauncherTheme(
    darkTheme: Boolean = isLauncherInDarkTheme(),
    // 🔥 FORCIBLY DISABLED DYNAMIC COLOR TO ENSURE APEX THEME ALWAYS SHOWS
    dynamicColor: Boolean = false, 
    backgroundViewModel: BackgroundViewModel? = null,
    festivals: List<Festival> = emptyList(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // 🔥 ALWAYS USE APEX THEME (Dynamic Colors and multiple palettes are removed)
    val targetColorScheme = remember(darkTheme) {
        if (darkTheme) apexDark else apexLight
    }

    var currentDarkTheme by remember { mutableStateOf(darkTheme) }
    var currentDisplayScheme by remember { mutableStateOf(targetColorScheme) }

    LaunchedEffect(darkTheme, targetColorScheme) {
        if (darkTheme != currentDarkTheme) {
            context.activeMaskView(
                maskComplete = {
                    currentDarkTheme = darkTheme
                    currentDisplayScheme = targetColorScheme
                },
                maskAnimFinish = {

                }
            )
        } else {
            currentDarkTheme = darkTheme
            currentDisplayScheme = targetColorScheme
        }
    }

    CompositionLocalProvider(
        LocalBackgroundViewModel provides backgroundViewModel,
        LocalFestivals provides festivals
    ) {
        MaterialExpressiveTheme(
            colorScheme = currentDisplayScheme,
            motionScheme = MotionScheme.expressive(),
            typography = AppTypography,
            content = content
        )
    }
}
