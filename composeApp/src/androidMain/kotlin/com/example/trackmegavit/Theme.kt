package com.example.trackmegavit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Lab / Emerald theme tokens ────────────────────────────────────────────────
object MdmColors {
    val Primary               = Color(0xFF2C7A4D)
    val OnPrimary             = Color(0xFFFFFFFF)
    val PrimaryContainer      = Color(0xFFE8F5E9)
    val OnPrimaryContainer    = Color(0xFF00210C)

    val Secondary             = Color(0xFF50606F)
    val OnSecondary           = Color(0xFFFFFFFF)
    val SecondaryContainer    = Color(0xFFD1E1F4)
    val OnSecondaryContainer  = Color(0xFF556474)

    val Tertiary              = Color(0xFF735C00)   // Gold
    val OnTertiary            = Color(0xFFFFFFFF)
    val TertiaryContainer     = Color(0xFFCCA830)
    val TertiaryFixed         = Color(0xFFFFE088)
    val OnTertiaryFixed       = Color(0xFF241A00)

    val Background            = Color(0xFFF7FAFC)
    val OnBackground          = Color(0xFF181C1E)

    val Surface               = Color(0xFFF7FAFC)
    val OnSurface             = Color(0xFF181C1E)
    val OnSurfaceVariant      = Color(0xFF43474D)
    val SurfaceVariant        = Color(0xFFE0E3E5)

    val Outline               = Color(0xFF74777E)
    val OutlineVariant        = Color(0xFFC4C6CE)

    // Tonal surface layers (light → dark)
    val SurfaceContainerLowest  = Color(0xFFFFFFFF)
    val SurfaceContainerLow     = Color(0xFFF1F4F6)
    val SurfaceContainer        = Color(0xFFEBEEF0)
    val SurfaceContainerHigh    = Color(0xFFE5E9EB)
    val SurfaceContainerHighest = Color(0xFFE0E3E5)

    val Error          = Color(0xFFBA1A1A)
    val ErrorContainer = Color(0xFFFFDAD6)

    // Local accent
    val EmeraldLight = Color(0xFFEBF5EF)
}

private val LabColorScheme = lightColorScheme(
    primary               = MdmColors.Primary,
    onPrimary             = MdmColors.OnPrimary,
    primaryContainer      = MdmColors.PrimaryContainer,
    onPrimaryContainer    = MdmColors.OnPrimaryContainer,

    secondary             = MdmColors.Secondary,
    onSecondary           = MdmColors.OnSecondary,
    secondaryContainer    = MdmColors.SecondaryContainer,
    onSecondaryContainer  = MdmColors.OnSecondaryContainer,

    tertiary              = MdmColors.Tertiary,
    onTertiary            = MdmColors.OnTertiary,
    tertiaryContainer     = MdmColors.TertiaryContainer,

    background            = MdmColors.Background,
    onBackground          = MdmColors.OnBackground,

    surface               = MdmColors.Surface,
    onSurface             = MdmColors.OnSurface,
    onSurfaceVariant      = MdmColors.OnSurfaceVariant,
    surfaceVariant        = MdmColors.SurfaceVariant,

    outline               = MdmColors.Outline,
    outlineVariant        = MdmColors.OutlineVariant,

    error                 = MdmColors.Error,
    errorContainer        = MdmColors.ErrorContainer,

    surfaceContainerLowest  = MdmColors.SurfaceContainerLowest,
    surfaceContainerLow     = MdmColors.SurfaceContainerLow,
    surfaceContainer        = MdmColors.SurfaceContainer,
    surfaceContainerHigh    = MdmColors.SurfaceContainerHigh,
    surfaceContainerHighest = MdmColors.SurfaceContainerHighest,
)

@Composable
fun MdmTrackProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LabColorScheme,
        content = content,
    )
}
