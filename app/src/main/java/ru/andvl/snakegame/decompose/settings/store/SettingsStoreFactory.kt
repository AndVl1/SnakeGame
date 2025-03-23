package ru.andvl.snakegame.decompose.settings.store

import android.content.Context
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andvl.snakegame.data.SettingsRepository
import ru.andvl.snakegame.game.model.GameSettings

/**
 * Интерфейс намерений для экрана настроек
 */
sealed interface SettingsIntent {
    object LoadSettings : SettingsIntent
    object ToggleTheme : SettingsIntent
    data class SelectLocale(val localeCode: String) : SettingsIntent
    object ClickBack : SettingsIntent
}

/**
 * Состояние экрана настроек
 */
data class SettingsState(
    val isDarkTheme: Boolean = false,
    val appLocale: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * События навигации или сообщения
 */
sealed interface SettingsLabel {
    object NavigateBack : SettingsLabel
    data class ShowMessage(val message: String) : SettingsLabel
}

/**
 * Интерфейс хранилища настроек
 */
interface SettingsStore : Store<SettingsIntent, SettingsState, SettingsLabel>

/**
 * Фабрика для создания хранилища настроек
 */
class SettingsStoreFactory(
    private val storeFactory: StoreFactory,
    private val settingsRepository: SettingsRepository,
    private val context: Context
) {
    
    /**
     * Создать экземпляр хранилища SettingsStore
     */
    fun create(): SettingsStore =
        object : SettingsStore, Store<SettingsIntent, SettingsState, SettingsLabel> by storeFactory.create(
            name = "SettingsStore",
            initialState = SettingsState(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { SettingsExecutor() },
            reducer = ReducerImpl
        ) {}
    
    /**
     * Исполнитель обрабатывает намерения, загружает данные и публикует результаты
     */
    private inner class SettingsExecutor : CoroutineExecutor<SettingsIntent, Unit, SettingsState, Result, SettingsLabel>(
        Dispatchers.Main
    ) {
        init {
            loadSettings()
        }
        
        override fun executeAction(action: Unit, getState: () -> SettingsState) {
            loadSettings()
        }
        
        override fun executeIntent(intent: SettingsIntent, getState: () -> SettingsState) {
            when (intent) {
                is SettingsIntent.LoadSettings -> loadSettings()
                is SettingsIntent.ToggleTheme -> toggleTheme()
                is SettingsIntent.SelectLocale -> selectLocale(intent.localeCode)
                is SettingsIntent.ClickBack -> publish(SettingsLabel.NavigateBack)
            }
        }
        
        private fun loadSettings() {
            scope.launch {
                dispatch(Result.Loading)
                try {
                    val settings = withContext(Dispatchers.IO) {
                        settingsRepository.settings.firstOrNull() ?: GameSettings()
                    }
                    dispatch(Result.SettingsLoaded(settings))
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Произошла ошибка при загрузке настроек"))
                }
            }
        }
        
        private fun toggleTheme() {
            scope.launch {
                try {
                    val currentSettings = withContext(Dispatchers.IO) {
                        settingsRepository.settings.firstOrNull() ?: GameSettings()
                    }
                    
                    val updatedSettings = currentSettings.copy(
                        isDarkTheme = !currentSettings.isDarkTheme
                    )
                    
                    withContext(Dispatchers.IO) {
                        settingsRepository.updateSettings(updatedSettings)
                    }
                    
                    // Обновляем UI с новыми настройками
                    dispatch(Result.ThemeToggled(updatedSettings.isDarkTheme))
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Ошибка при изменении темы"))
                }
            }
        }
        
        private fun selectLocale(localeCode: String) {
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        settingsRepository.updateAppLocale(localeCode)
                    }
                    
                    dispatch(Result.LocaleSelected(localeCode))
                    publish(SettingsLabel.ShowMessage("Язык будет изменен при следующем запуске"))
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Ошибка при изменении языка"))
                }
            }
        }
    }
    
    /**
     * Результаты операций, которые изменяют состояние
     */
    private sealed interface Result {
        object Loading : Result
        data class SettingsLoaded(val settings: GameSettings) : Result
        data class ThemeToggled(val isDarkTheme: Boolean) : Result
        data class LocaleSelected(val localeCode: String) : Result
        data class Error(val message: String) : Result
    }
    
    /**
     * Редуктор обновляет состояние на основе результатов
     */
    private object ReducerImpl : Reducer<SettingsState, Result> {
        override fun SettingsState.reduce(result: Result): SettingsState =
            when (result) {
                is Result.Loading -> copy(isLoading = true, error = null)
                is Result.SettingsLoaded -> copy(
                    isDarkTheme = result.settings.isDarkTheme,
                    appLocale = result.settings.appLocale,
                    isLoading = false,
                    error = null
                )
                is Result.ThemeToggled -> copy(isDarkTheme = result.isDarkTheme, isLoading = false, error = null)
                is Result.LocaleSelected -> copy(appLocale = result.localeCode, isLoading = false, error = null)
                is Result.Error -> copy(isLoading = false, error = result.message)
            }
    }
} 
