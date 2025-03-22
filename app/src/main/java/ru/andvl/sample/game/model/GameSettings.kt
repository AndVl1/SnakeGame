package ru.andvl.sample.game.model

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.serialization.Serializable

/**
 * Модель настроек игры
 */
@Serializable
data class GameSettings(
    // Сложность игры (от 1 до 5)
    val difficulty: Int = 3,
    
    // Размер игрового поля (от 10 до 20)
    val boardSize: Int = 15,
    
    // Включена ли темная тема
    val isDarkTheme: Boolean = false,
    
    // Включены ли вибрации
    val vibrationsEnabled: Boolean = true,
    
    // Включены ли звуки
    val soundsEnabled: Boolean = true,
    
    // Начальная скорость змейки (1.0f - нормальная)
    val initialSpeedFactor: Float = 1.0f,
    
    // Максимальное количество препятствий
    val maxObstacles: Int = 5,
    
    // Частота появления специальной еды (1-10, где 10 - наиболее часто)
    val specialFoodFrequency: Int = 5
) {
    companion object {
        // Ключи для DataStore
        val DIFFICULTY_KEY = intPreferencesKey("difficulty")
        val BOARD_SIZE_KEY = intPreferencesKey("board_size")
        val DARK_THEME_KEY = booleanPreferencesKey("dark_theme")
        val VIBRATIONS_KEY = booleanPreferencesKey("vibrations_enabled")
        val SOUNDS_KEY = booleanPreferencesKey("sounds_enabled")
        val INITIAL_SPEED_KEY = floatPreferencesKey("initial_speed")
        val MAX_OBSTACLES_KEY = intPreferencesKey("max_obstacles")
        val SPECIAL_FOOD_FREQUENCY_KEY = intPreferencesKey("special_food_frequency")
    }
} 