package ru.andvl.sample.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.andvl.sample.game.model.GameSettings

// Экземпляр DataStore привязан к контексту приложения
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Репозиторий для работы с настройками игры
 */
class SettingsRepository(private val context: Context) {
    
    /**
     * Получает настройки игры как Flow
     */
    val settings: Flow<GameSettings> = context.settingsDataStore.data.map { preferences ->
        GameSettings(
            difficulty = preferences[GameSettings.DIFFICULTY_KEY] ?: GameSettings().difficulty,
            boardSize = preferences[GameSettings.BOARD_SIZE_KEY] ?: GameSettings().boardSize,
            isDarkTheme = preferences[GameSettings.DARK_THEME_KEY] ?: GameSettings().isDarkTheme,
            vibrationsEnabled = preferences[GameSettings.VIBRATIONS_KEY] ?: GameSettings().vibrationsEnabled,
            soundsEnabled = preferences[GameSettings.SOUNDS_KEY] ?: GameSettings().soundsEnabled,
            initialSpeedFactor = preferences[GameSettings.INITIAL_SPEED_KEY] ?: GameSettings().initialSpeedFactor,
            maxObstacles = preferences[GameSettings.MAX_OBSTACLES_KEY] ?: GameSettings().maxObstacles,
            specialFoodFrequency = preferences[GameSettings.SPECIAL_FOOD_FREQUENCY_KEY] ?: GameSettings().specialFoodFrequency,
            appLocale = preferences[GameSettings.APP_LOCALE_KEY] ?: GameSettings().appLocale
        )
    }
    
    /**
     * Обновляет настройки игры
     */
    suspend fun updateSettings(gameSettings: GameSettings) {
        context.settingsDataStore.edit { preferences ->
            preferences[GameSettings.DIFFICULTY_KEY] = gameSettings.difficulty
            preferences[GameSettings.BOARD_SIZE_KEY] = gameSettings.boardSize
            preferences[GameSettings.DARK_THEME_KEY] = gameSettings.isDarkTheme
            preferences[GameSettings.VIBRATIONS_KEY] = gameSettings.vibrationsEnabled
            preferences[GameSettings.SOUNDS_KEY] = gameSettings.soundsEnabled
            preferences[GameSettings.INITIAL_SPEED_KEY] = gameSettings.initialSpeedFactor
            preferences[GameSettings.MAX_OBSTACLES_KEY] = gameSettings.maxObstacles
            preferences[GameSettings.SPECIAL_FOOD_FREQUENCY_KEY] = gameSettings.specialFoodFrequency
            preferences[GameSettings.APP_LOCALE_KEY] = gameSettings.appLocale
        }
    }
    
    /**
     * Сбрасывает настройки к значениям по умолчанию
     */
    suspend fun resetSettings() {
        updateSettings(GameSettings())
    }
    
    /**
     * Обновление отдельной настройки темы
     */
    suspend fun updateDarkThemeEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[GameSettings.DARK_THEME_KEY] = enabled
        }
    }
    
    /**
     * Обновление отдельной настройки вибрации
     */
    suspend fun updateVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[GameSettings.VIBRATIONS_KEY] = enabled
        }
    }
    
    /**
     * Обновление отдельной настройки звуков
     */
    suspend fun updateSoundEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[GameSettings.SOUNDS_KEY] = enabled
        }
    }
    
    /**
     * Обновление отдельной настройки сложности
     */
    suspend fun updateDifficulty(difficulty: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[GameSettings.DIFFICULTY_KEY] = difficulty.coerceIn(1, 5)
        }
    }
    
    /**
     * Обновление локали приложения
     */
    suspend fun updateAppLocale(locale: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[GameSettings.APP_LOCALE_KEY] = locale
        }
    }
} 