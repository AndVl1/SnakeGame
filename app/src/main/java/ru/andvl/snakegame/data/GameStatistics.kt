package ru.andvl.snakegame.data

import kotlinx.serialization.Serializable

/**
 * Модель для хранения статистики игры
 */
@Serializable
data class GameStatistics(
    // Общие показатели
    val totalGamesPlayed: Int = 0,
    val totalScore: Int = 0,
    val totalPlayTimeSeconds: Long = 0,

    // Рекорды
    val highestScore: Int = 0,
    val longestSnake: Int = 0,
    val fastestSpeed: Float = 1.0f,

    // Статистика еды
    val regularFoodEaten: Int = 0,
    val specialFoodEaten: Int = 0,

    // Статистика смертей
    val totalDeaths: Int = 0,

    // Средняя продолжительность игры в секундах
    val averageGameDuration: Long = 0
)
