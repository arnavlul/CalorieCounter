package com.example.calorietracker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = SageGreen,
    onPrimary = Color.Black,
    primaryContainer = DarkSageContainer,
    onPrimaryContainer = OnDarkSage,
    secondary = Peach,
    onSecondary = Color.Black,
    secondaryContainer = DarkPeachContainer,
    onSecondaryContainer = OnDarkPeach,
    tertiary = Lavender,
    onTertiary = Color.White,
    tertiaryContainer = DarkLavenderContainer,
    onTertiaryContainer = OnDarkLavender,
    background = DarkBackground,
    onBackground = Color.White,
    surface = DarkSurface,
    onSurface = Color.White,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF757575)
)

private val LightColorScheme = lightColorScheme(
    primary = SageGreen,
    onPrimary = Color.White,
    primaryContainer = SageContainer,
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Peach,
    onSecondary = Color.White,
    secondaryContainer = PeachContainer,
    onSecondaryContainer = Color(0xFFE65100),
    tertiary = Lavender,
    onTertiary = Color.White,
    tertiaryContainer = LavenderContainer,
    onTertiaryContainer = Color(0xFF4A148C),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

// Monochrome (Black & Grey)
private val LightMonochromeColorScheme = lightColorScheme(
    primary = Color(0xFF212121),       // Dark Grey/Black
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0E0E0), // Light Grey
    onPrimaryContainer = Color(0xFF212121),
    secondary = Color(0xFF424242),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEEEEEE),
    onSecondaryContainer = Color(0xFF212121),
    tertiary = Color(0xFF757575),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF5F5F5),
    onTertiaryContainer = Color.Black,
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF212121),
    surface = Color.White,
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF424242),
    outline = Color(0xFF9E9E9E)
)

private val DarkMonochromeColorScheme = darkColorScheme(
    primary = Color(0xFFE0E0E0),      // Light Grey
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2B2B2B), // Charcoal/Dark Grey
    onPrimaryContainer = Color(0xFFE0E0E0),
    secondary = Color(0xFFB0B0B0),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1E1E1E),
    onSecondaryContainer = Color(0xFFB0B0B0),
    tertiary = Color(0xFF888888),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF141414),
    onTertiaryContainer = Color(0xFF888888),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1C1C1C),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF757575)
)

// Midnight Blue
private val LightMidnightColorScheme = lightColorScheme(
    primary = Color(0xFF1A237E),       // Navy
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC5CAE9), // Light Indigo
    onPrimaryContainer = Color(0xFF1A237E),
    secondary = Color(0xFF0D47A1),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBBDEFB),
    onSecondaryContainer = Color(0xFF0D47A1),
    tertiary = Color(0xFF006064),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2EBF2),
    onTertiaryContainer = Color(0xFF006064),
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF1A237E),
    surface = Color.White,
    onSurface = Color(0xFF1A237E),
    surfaceVariant = Color(0xFFE8EAF6),
    onSurfaceVariant = Color(0xFF3F51B5),
    outline = Color(0xFF3F51B5)
)

private val DarkMidnightColorScheme = darkColorScheme(
    primary = Color(0xFF9FA8DA),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1A237E),
    onPrimaryContainer = Color(0xFFC5CAE9),
    secondary = Color(0xFF90CAF9),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0D47A1),
    onSecondaryContainer = Color(0xFFBBDEFB),
    tertiary = Color(0xFF80DEEA),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF006064),
    background = Color(0xFF0B0E14),
    onBackground = Color(0xFFE8EAF6),
    surface = Color(0xFF121620),
    onSurface = Color(0xFFE8EAF6),
    surfaceVariant = Color(0xFF1C2230),
    onSurfaceVariant = Color(0xFF9FA8DA),
    outline = Color(0xFF5C6BC0)
)

// Forest Green
private val LightForestColorScheme = lightColorScheme(
    primary = Color(0xFF1B5E20),       // Forest Green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC8E6C9), // Mint Container
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF2E7D32),
    tertiary = Color(0xFF004D40),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFB2DFDB),
    onTertiaryContainer = Color(0xFF004D40),
    background = Color(0xFFF1F8E9),
    onBackground = Color(0xFF1B5E20),
    surface = Color.White,
    onSurface = Color(0xFF1B5E20),
    surfaceVariant = Color(0xFFDCEDC8),
    onSurfaceVariant = Color(0xFF33691E),
    outline = Color(0xFF558B2F)
)

private val DarkForestColorScheme = darkColorScheme(
    primary = Color(0xFFA5D6A7),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondary = Color(0xFF81C784),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF004D40),
    background = Color(0xFF080F09),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF0D1C0F),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF162D1A),
    onSurfaceVariant = Color(0xFFA5D6A7),
    outline = Color(0xFF4CAF50)
)

@Composable
fun CalorieTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeName: String = "classic",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeName) {
        "monochrome" -> if (darkTheme) DarkMonochromeColorScheme else LightMonochromeColorScheme
        "midnight" -> if (darkTheme) DarkMidnightColorScheme else LightMidnightColorScheme
        "forest" -> if (darkTheme) DarkForestColorScheme else LightForestColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
