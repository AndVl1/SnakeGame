package ru.andvl.snakegame.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PlayerScore(
    val playerName: String,
    val score: Int,
    val speedFactor: Float,
    val timestamp: Long = System.currentTimeMillis()
)

class ScoreRepository(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "snake_scores")
        private val SCORES_KEY = stringPreferencesKey("scores")
        private val json = Json { ignoreUnknownKeys = true }
    }

    // Получить все сохраненные результаты
    val scores: Flow<List<PlayerScore>> = context.dataStore.data.map { preferences ->
        val scoresJson = preferences[SCORES_KEY] ?: "[]"
        try {
            json.decodeFromString<List<PlayerScore>>(scoresJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Получить топ результатов
    suspend fun getTopScores(): List<PlayerScore> {
        val preferences = context.dataStore.data.first()
        val scoresJson = preferences[SCORES_KEY] ?: "[]"

        return try {
            json.decodeFromString<List<PlayerScore>>(scoresJson)
                .sortedByDescending { it.score }
                .take(10)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Сохранить новый результат
    suspend fun addScore(playerScore: PlayerScore) {
        context.dataStore.edit { preferences ->
            val currentScores = preferences[SCORES_KEY]?.let {
                try {
                    json.decodeFromString<List<PlayerScore>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            // Добавляем новый результат и сортируем по убыванию очков
            val updatedScores = (currentScores + playerScore)
                .sortedByDescending { it.score }
                .take(10) // Оставляем только 10 лучших результатов

            preferences[SCORES_KEY] = json.encodeToString(updatedScores)
        }
    }

    // Очистить все результаты
    suspend fun clearAllScores() {
        context.dataStore.edit { preferences ->
            preferences.remove(SCORES_KEY)
        }
    }
}
