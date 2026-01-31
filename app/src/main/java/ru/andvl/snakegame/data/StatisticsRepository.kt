package ru.andvl.snakegame.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Экземпляр DataStore привязан к контексту приложения
private val Context.statisticsDataStore: DataStore<Preferences> by preferencesDataStore(name = "statistics")

/**
 * Репозиторий для работы со статистикой игры
 */
class StatisticsRepository(private val context: Context) {

    companion object {
        private val TOTAL_GAMES_KEY = intPreferencesKey("total_games")
        private val TOTAL_SCORE_KEY = intPreferencesKey("total_score")
        private val TOTAL_PLAY_TIME_KEY = longPreferencesKey("total_play_time")
        private val HIGHEST_SCORE_KEY = intPreferencesKey("highest_score")
        private val LONGEST_SNAKE_KEY = intPreferencesKey("longest_snake")
        private val FASTEST_SPEED_KEY = floatPreferencesKey("fastest_speed")
        private val REGULAR_FOOD_KEY = intPreferencesKey("regular_food")
        private val SPECIAL_FOOD_KEY = intPreferencesKey("special_food")
        private val TOTAL_DEATHS_KEY = intPreferencesKey("total_deaths")
        private val AVERAGE_DURATION_KEY = longPreferencesKey("average_duration")
    }

    /**
     * Получает статистику игры как Flow
     */
    val statistics: Flow<GameStatistics> = context.statisticsDataStore.data.map { preferences ->
        GameStatistics(
            totalGamesPlayed = preferences[TOTAL_GAMES_KEY] ?: 0,
            totalScore = preferences[TOTAL_SCORE_KEY] ?: 0,
            totalPlayTimeSeconds = preferences[TOTAL_PLAY_TIME_KEY] ?: 0L,
            highestScore = preferences[HIGHEST_SCORE_KEY] ?: 0,
            longestSnake = preferences[LONGEST_SNAKE_KEY] ?: 0,
            fastestSpeed = preferences[FASTEST_SPEED_KEY] ?: 1.0f,
            regularFoodEaten = preferences[REGULAR_FOOD_KEY] ?: 0,
            specialFoodEaten = preferences[SPECIAL_FOOD_KEY] ?: 0,
            totalDeaths = preferences[TOTAL_DEATHS_KEY] ?: 0,
            averageGameDuration = preferences[AVERAGE_DURATION_KEY] ?: 0L
        )
    }

    /**
     * Обновляет статистику после завершения игры
     */
    suspend fun updateAfterGame(
        score: Int,
        snakeLength: Int,
        speedFactor: Float,
        gameTimeSeconds: Long,
        regularFoodCount: Int,
        specialFoodCount: Int
    ) {
        context.statisticsDataStore.edit { preferences ->
            // Увеличиваем счетчик игр
            val totalGames = (preferences[TOTAL_GAMES_KEY] ?: 0) + 1
            preferences[TOTAL_GAMES_KEY] = totalGames

            // Обновляем общую статистику
            preferences[TOTAL_SCORE_KEY] = (preferences[TOTAL_SCORE_KEY] ?: 0) + score
            preferences[TOTAL_PLAY_TIME_KEY] = (preferences[TOTAL_PLAY_TIME_KEY] ?: 0L) + gameTimeSeconds
            preferences[TOTAL_DEATHS_KEY] = (preferences[TOTAL_DEATHS_KEY] ?: 0) + 1

            // Обновляем еду
            preferences[REGULAR_FOOD_KEY] = (preferences[REGULAR_FOOD_KEY] ?: 0) + regularFoodCount
            preferences[SPECIAL_FOOD_KEY] = (preferences[SPECIAL_FOOD_KEY] ?: 0) + specialFoodCount

            // Обновляем рекорды
            val currentHighest = preferences[HIGHEST_SCORE_KEY] ?: 0
            if (score > currentHighest) {
                preferences[HIGHEST_SCORE_KEY] = score
            }

            val currentLongest = preferences[LONGEST_SNAKE_KEY] ?: 0
            if (snakeLength > currentLongest) {
                preferences[LONGEST_SNAKE_KEY] = snakeLength
            }

            val currentFastest = preferences[FASTEST_SPEED_KEY] ?: 1.0f
            if (speedFactor > currentFastest) {
                preferences[FASTEST_SPEED_KEY] = speedFactor
            }

            // Вычисляем среднюю продолжительность
            val totalTime = (preferences[TOTAL_PLAY_TIME_KEY] ?: 0L)
            preferences[AVERAGE_DURATION_KEY] = totalTime / totalGames
        }
    }

    /**
     * Увеличивает счетчик сыгранных игр
     */
    suspend fun incrementGamesPlayed() {
        context.statisticsDataStore.edit { preferences ->
            preferences[TOTAL_GAMES_KEY] = (preferences[TOTAL_GAMES_KEY] ?: 0) + 1
        }
    }

    /**
     * Сбрасывает всю статистику
     */
    suspend fun resetStatistics() {
        context.statisticsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
