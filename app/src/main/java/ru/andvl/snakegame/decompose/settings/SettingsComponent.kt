package ru.andvl.snakegame.decompose.settings

import android.content.Context
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.andvl.snakegame.data.SettingsRepository

/**
 * Компонент для экрана настроек
 */
class SettingsComponent(
    componentContext: ComponentContext,
    private val settingsRepository: SettingsRepository,
    private val onNavigateBack: () -> Unit,
    private val context: Context
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
                    isDarkTheme = gameSettings.isDarkTheme,
                    appLocale = gameSettings.appLocale
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
            val updatedGameSettings = currentSettings.copy(
                isDarkTheme = !currentSettings.isDarkTheme  // Инвертируем текущее состояние темы
            )
            
            settingsRepository.updateSettings(updatedGameSettings)
        }
    }
    
    fun onAppLocaleSelected(localeCode: String) {
        scope.launch {
            // Сохраняем выбранную локаль в настройках
            settingsRepository.updateAppLocale(localeCode)
            
            // Предупреждаем пользователя, что для применения локали нужен перезапуск
            // (Это можно реализовать через Toast или другие UI-элементы)
        }
    }
    
    data class State(
        val isDarkTheme: Boolean = false,
        val appLocale: String = ""
    )
} 
