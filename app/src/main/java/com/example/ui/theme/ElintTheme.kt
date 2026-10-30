package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val ElintLightColors = lightColorScheme(
    primary = ElintBrandPrimary,
    onPrimary = Color.White,
    primaryContainer = ElintBrandPrimaryContainer,
    onPrimaryContainer = ElintBrandPrimaryPressed,
    secondary = ElintAmberIcon,
    onSecondary = Color.White,
    secondaryContainer = ElintAmberSoft,
    onSecondaryContainer = ElintAmberText,
    tertiary = ElintWfhIcon,
    onTertiary = Color.White,
    tertiaryContainer = ElintWfhInfoSurface,
    onTertiaryContainer = ElintWfhInfoText,
    background = ElintBackground,
    onBackground = ElintTextPrimary,
    surface = ElintSurface,
    onSurface = ElintTextPrimary,
    surfaceVariant = ElintSurfaceSubtle,
    onSurfaceVariant = ElintTextMuted,
    outline = ElintOutline,
    outlineVariant = ElintDivider,
    error = ElintAbsentIcon,
    onError = Color.White,
    scrim = ElintScrim,
)

/** Colors for one quick-update action (tile + sheet option). */
@Immutable
data class QuickActionColors(val label: Color, val icon: Color, val border: Color)

/** Brand colors that Material's ColorScheme has no slot for. */
@Immutable
data class ElintExtendedColors(
    val runningLate: QuickActionColors = QuickActionColors(ElintAmberText, ElintAmberIcon, ElintAmberTileBorder),
    val workFromHome: QuickActionColors = QuickActionColors(ElintWfhText, ElintWfhIcon, ElintWfhTileBorder),
    val cantCome: QuickActionColors = QuickActionColors(ElintAbsentText, ElintAbsentIcon, ElintAbsentTileBorder),
    val takeLeave: QuickActionColors = QuickActionColors(ElintLeaveText, ElintLeaveIcon, ElintLeaveTileBorder),
    val announcementSurface: Color = ElintAmberSurface,
    val announcementBorder: Color = ElintAmberBorder,
    val announcementText: Color = ElintAmberText,
    val statusLineSurface: Color = ElintAmberSoft,
    val statusLineText: Color = ElintAmberText,
    val quoteSurface: Color = ElintBrandTintSurface,
    val locationPill: Color = ElintBrandTintPill,
    val checkInRing: Color = ElintBrandPrimaryRing,
    val checkInRingEdge: Color = ElintBrandPrimaryRingEdge,
    val headingSerif: Color = ElintHeadingSerif,
    val textSecondary: Color = ElintTextSecondary,
    val inputBackground: Color = ElintInputBackground,
    val success: Color = ElintStatusSuccess,
    val neutralDot: Color = ElintStatusNeutralDot,
    val unreadDot: Color = ElintUnreadDot,
)

val LocalElintColors = staticCompositionLocalOf { ElintExtendedColors() }

/**
 * Wrap the app (or the home screen) in this. Light theme only for now;
 * a dark palette has not been designed yet.
 */
@Composable
fun ElintTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalElintColors provides ElintExtendedColors()) {
        MaterialTheme(
            colorScheme = ElintLightColors,
            typography = androidx.compose.runtime.remember { ElintTypography },
            shapes = ElintMaterialShapes,
            content = content,
        )
    }
}

/** Shortcut: ElintTheme.colors.runningLate.icon, ElintTheme.text.dailyLine */
object ElintTheme {
    val colors: ElintExtendedColors
        @Composable get() = LocalElintColors.current
    val text = ElintText
    val shapes = ElintShapes
    val dimens = ElintDimens
}
