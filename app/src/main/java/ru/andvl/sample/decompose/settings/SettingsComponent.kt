package ru.andvl.sample.decompose.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.andvl.sample.data.Settings
import ru.andvl.sample.data.SettingsRepository
import ru.andvl.sample.game.model.GameSettings

/**
 * Компонент для экрана настроек
 */
class SettingsComponent(
    componentContext: ComponentContext,
    private val settingsRepository: SettingsRepository,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    
    private val _state = MutableValue(State())
    val state: Value<State> = _state
    
    init {
        observeSettings()
    }
    
    private fun observeSettings() {
        settingsRepository.settings
            .onEach { gameSettings ->
                _state.value = State(
                    isDarkTheme = gameSettings.isDarkTheme
                )
            }
            .launchIn(scope)
    }
    
    fun onBackClicked() {
        onNavigateBack()
    }
    
    fun onThemeToggled() {
        scope.launch {
            val currentSettings = settingsRepository.settings.first()
            
            // Создаем объект GameSettings из текущих настроек
            val updatedGameSettings = GameSettings(
                difficulty = currentSettings.difficulty,
                boardSize = currentSettings.boardSize,
                isDarkTheme = !currentSettings.isDarkTheme,  // Инвертируем текущее состояние темы
                vibrationsEnabled = currentSettings.vibrationsEnabled,
                soundsEnabled = currentSettings.soundsEnabled,
                initialSpeedFactor = currentSettings.initialSpeedFactor,
                maxObstacles = currentSettings.maxObstacles,
                specialFoodFrequency = currentSettings.specialFoodFrequency
            )
            
            settingsRepository.updateSettings(updatedGameSettings)
        }
    }
    
    data class State(
        val isDarkTheme: Boolean = false
    )
} 