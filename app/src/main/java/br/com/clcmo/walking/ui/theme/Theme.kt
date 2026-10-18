package br.com.clcmo.walking.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = Marinho,
    onPrimary = Color.White,
    primaryContainer = AzulCeu,
    onPrimaryContainer = Marinho,
    secondary = AzulMedio,
    onSecondary = Marinho,
    secondaryContainer = AzulCeu,
    onSecondaryContainer = Marinho,
    background = Color.White,
    onBackground = Marinho,
    surface = Color.White,
    onSurface = Marinho,
    surfaceVariant = Nevoa,
    onSurfaceVariant = Marinho,
    outline = AzulMedio,
)

private val EsquemaEscuro = darkColorScheme(
    primary = AzulCeu,
    onPrimary = Marinho,
    primaryContainer = MarinhoClaro,
    onPrimaryContainer = AzulCeu,
    secondary = AzulMedio,
    onSecondary = Marinho,
    secondaryContainer = MarinhoClaro,
    onSecondaryContainer = AzulCeu,
    background = NoiteAzul,
    onBackground = Nevoa,
    surface = NoiteAzul,
    onSurface = Nevoa,
    surfaceVariant = NoiteSuperficie,
    onSurfaceVariant = Nevoa,
    outline = AzulMedio,
)

/** Sem cores dinâmicas de propósito: a identidade vem da logo. */
@Composable
fun WalkingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaEscuro else EsquemaClaro,
        typography = Tipografia,
        content = content,
    )
}
