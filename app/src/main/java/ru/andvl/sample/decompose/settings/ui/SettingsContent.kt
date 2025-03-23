package ru.andvl.sample.decompose.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.sample.decompose.settings.SettingsComponent
import ru.andvl.sample.ui.SettingsScreen

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
        onThemeToggled = { component.onThemeToggled() },
        onBackClicked = { component.onBackClicked() },
        modifier = modifier
    )
} 