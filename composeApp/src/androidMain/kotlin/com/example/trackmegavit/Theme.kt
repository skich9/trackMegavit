package com.example.trackmegavit

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

private val LabDarkColorScheme = darkColorScheme(
    primary = Color(0xFF7FD39F),
    onPrimary = Color(0xFF00391A),
    primaryContainer = Color(0xFF0F5C33),
    onPrimaryContainer = Color(0xFF9CF0BA),

    secondary = Color(0xFFB8C8DA),
    onSecondary = Color(0xFF22313F),
    secondaryContainer = Color(0xFF3A4A58),
    onSecondaryContainer = Color(0xFFD3E3F6),

    tertiary = Color(0xFFE6C663),
    onTertiary = Color(0xFF3B2F00),
    tertiaryContainer = Color(0xFF5B4700),

    background = Color(0xFF101515),
    onBackground = Color(0xFFE1E3E3),

    surface = Color(0xFF101515),
    onSurface = Color(0xFFE1E3E3),
    onSurfaceVariant = Color(0xFFC1C7C8),
    surfaceVariant = Color(0xFF434A4B),

    outline = Color(0xFF8A9293),
    outlineVariant = Color(0xFF434A4B),

    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),

    surfaceContainerLowest = Color(0xFF0B0F10),
    surfaceContainerLow = Color(0xFF161B1C),
    surfaceContainer = Color(0xFF1A1F20),
    surfaceContainerHigh = Color(0xFF252B2C),
    surfaceContainerHighest = Color(0xFF303637),
)

@Composable
fun MdmTrackProTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) LabDarkColorScheme else LabColorScheme,
        content = content,
    )
}
