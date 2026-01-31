package ru.andvl.snakegame.decompose.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.snakegame.decompose.settings.SettingsComponent
import ru.andvl.snakegame.ui.SettingsScreen

/**
 * UI для экрана настроек
 */
@Composable
fun SettingsContent(
    component: SettingsComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()

    SettingsScreen(
        isDarkTheme = state.isDarkTheme,
        soundsEnabled = state.soundsEnabled,
        vibrationsEnabled = state.vibrationsEnabled,
        difficulty = state.difficulty,
        swipeSensitivity = state.swipeSensitivity,
        onThemeToggled = { component.onThemeToggled() },
        onSoundsToggled = { component.onSoundsToggled() },
        onVibrationsToggled = { component.onVibrationsToggled() },
        onDifficultySelected = { component.onDifficultySelected(it) },
        onSwipeSensitivityChanged = { component.onSwipeSensitivitySelected(it) },
        onBackClicked = { component.onBackClicked() },
        modifier = modifier
    )
}
