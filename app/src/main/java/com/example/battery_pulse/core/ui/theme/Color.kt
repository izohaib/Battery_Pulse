package com.example.battery_pulse.core.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

// --- Primary (Green-based) ---
// ===================== LIGHT THEME =====================
// Style: Pure white bg, vivid green primary, light mint containers
// Reference: Google Maps, Duolingo light theme

// ===================== LIGHT THEME =====================

val light_primary = Color(0xFF1B8A34)
val light_onPrimary = Color(0xFFFFFFFF)
val light_primaryContainer = Color(0xFFA8DFB2)   // darker mint — was D6F5DC
val light_onPrimaryContainer = Color(0xFF00210A)

val light_secondary = Color(0xFF4A7C59)
val light_onSecondary = Color(0xFFFFFFFF)
val light_secondaryContainer = Color(0xFFA8D4B4)  // darker sage — was CCE DD6
val light_onSecondaryContainer = Color(0xFF072113)

val light_tertiary = Color(0xFF6B5778)
val light_onTertiary = Color(0xFFFFFFFF)
val light_tertiaryContainer = Color(0xFFD9BFED)   // slightly darker — was F2DAFF
val light_onTertiaryContainer = Color(0xFF251431)

val light_error = Color(0xFFBA1A1A)
val light_onError = Color(0xFFFFFFFF)
val light_errorContainer = Color(0xFFFFDAD6)
val light_onErrorContainer = Color(0xFF410002)

val light_background = Color(0xFFFFFFFF)
val light_onBackground = Color(0xFF111411)
val light_surface = Color(0xFFFFFFFF)
val light_onSurface = Color(0xFF111411)
val light_surfaceVariant = Color(0xFFC8D8CC)      // darker — was DCE ADF
val light_onSurfaceVariant = Color(0xFF3D4E41)
val light_outline = Color(0xFF6C7E6F)
val light_outlineVariant = Color(0xFFA3B5A6)      // darker — was BACABD
val light_scrim = Color(0xFF000000)
val light_inverseSurface = Color(0xFF282E29)
val light_inverseOnSurface = Color(0xFFEDF2EE)
val light_inversePrimary = Color(0xFF7FD48D)
val light_surfaceTint = Color(0xFF1B8A34)

// Containers: now clearly visible steps
val light_surfaceContainerLowest = Color(0xFFF5FAF6) // background level
val light_surfaceContainerLow = Color(0xFFE8F0EA)    // subtle card
val light_surfaceContainer = Color(0xFFDAE8DD)       // main cards — clearly visible
val light_surfaceContainerHigh = Color(0xFFCCDFD0)   // elevated cards
val light_surfaceContainerHighest = Color(0xFFBED6C3) // most prominent

val light_glowColor = Color(0xFFFFC107)

// ===================== DARK THEME =====================

val dark_primary = Color(0xFF8DD99A)
val dark_onPrimary = Color(0xFF003913)
val dark_primaryContainer = Color(0xFF00521E)
val dark_onPrimaryContainer = Color(0xFFE8F5E9)

// --- Secondary ---
val dark_secondary = Color(0xFFB4CCBA)
val dark_onSecondary = Color(0xFF203528)
val dark_secondaryContainer = Color(0xFF364B3D)
val dark_onSecondaryContainer = Color(0xFFD0E8D8)

// --- Tertiary ---
val dark_tertiary = Color(0xFFD6BEE4)
val dark_onTertiary = Color(0xFF3B2948)
val dark_tertiaryContainer = Color(0xFF523F5F)
val dark_onTertiaryContainer = Color(0xFFF2DAFF)

// --- Error ---
val dark_error = Color(0xFFFFB4AB)
val dark_onError = Color(0xFF690005)
val dark_errorContainer = Color(0xFF93000A)
val dark_onErrorContainer = Color(0xFFFFDAD6)

// --- Background & Surface ---
val dark_background = Color(0xFF0D1B10)
val dark_onBackground = Color(0xFFE1E3DF)
val dark_surface = Color(0xFF0D1B10)
val dark_onSurface = Color(0xFFE1E3DF)
val dark_surfaceVariant = Color(0xFF404943)
val dark_onSurfaceVariant = Color(0xFFC0C9C1)
val dark_outline = Color(0xFF8A938B)
val dark_outlineVariant = Color(0xFF404943)
val dark_scrim = Color(0xFF000000)
val dark_inverseSurface = Color(0xFFE1E3DF)
val dark_inverseOnSurface = Color(0xFF2D3330)
val dark_inversePrimary = Color(0xFF1A6B2A)
val dark_surfaceTint = Color(0xFF8DD99A)

val dark_surfaceContainerLowest = Color(0xFF081209)
val dark_surfaceContainerLow = Color(0xFF0D1B10)
val dark_surfaceContainer = Color(0xFF111F14)
val dark_surfaceContainerHigh = Color(0xFF1B2A1E)
val dark_surfaceContainerHighest = Color(0xFF253328)

val ColorScheme.glow: Color
    get() = Color(0xFFFFC107)