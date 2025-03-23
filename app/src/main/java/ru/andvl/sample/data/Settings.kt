package ru.andvl.sample.data

import kotlinx.serialization.Serializable

/**
 * Модель настроек приложения
 */
@Serializable
data class Settings(
    // Визуальные настройки
    val isDarkTheme: Boolean = false,
    
    // Настройки звука и вибрации
    val soundsEnabled: Boolean = true,
    val vibrationsEnabled: Boolean = true,
    
    // Настройки игры
    val difficulty: Int = 3,
    val boardSize: Int = 15,
    val initialSpeedFactor: Float = 1.0f,
    val maxObstacles: Int = 5,
    val specialFoodFrequency: Int = 5
) 