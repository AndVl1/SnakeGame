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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ReplayRepository(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "snake_replays")
        private val REPLAYS_KEY = stringPreferencesKey("replays")
        private val json = Json { ignoreUnknownKeys = true }
        private const val MAX_REPLAYS = 10
    }

    // Получить все сохраненные реплеи
    val replays: Flow<List<GameReplay>> = context.dataStore.data.map { preferences ->
        val replaysJson = preferences[REPLAYS_KEY] ?: "[]"
        try {
            json.decodeFromString<List<GameReplay>>(replaysJson)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Получить реплей по ID
    suspend fun getReplay(id: String): GameReplay? {
        val allReplays = replays.first()
        return allReplays.find { it.id == id }
    }

    // Получить все реплеи
    suspend fun getAllReplays(): List<GameReplay> {
        return replays.first()
            .sortedByDescending { it.timestamp }
    }

    // Сохранить новый реплей
    suspend fun saveReplay(replay: GameReplay) {
        context.dataStore.edit { preferences ->
            val currentReplays = preferences[REPLAYS_KEY]?.let {
                try {
                    json.decodeFromString<List<GameReplay>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            // Добавляем новый реплей и сортируем по времени (новые первые)
            // Оставляем только MAX_REPLAYS последних реплеев
            val updatedReplays = (currentReplays + replay)
                .sortedByDescending { it.timestamp }
                .take(MAX_REPLAYS)

            preferences[REPLAYS_KEY] = json.encodeToString(updatedReplays)
        }
    }

    // Удалить реплей
    suspend fun deleteReplay(id: String) {
        context.dataStore.edit { preferences ->
            val currentReplays = preferences[REPLAYS_KEY]?.let {
                try {
                    json.decodeFromString<List<GameReplay>>(it)
                } catch (e: Exception) {
                    emptyList()
                }
            } ?: emptyList()

            val updatedReplays = currentReplays.filter { it.id != id }
            preferences[REPLAYS_KEY] = json.encodeToString(updatedReplays)
        }
    }

    // Очистить все реплеи
    suspend fun clearAllReplays() {
        context.dataStore.edit { preferences ->
            preferences.remove(REPLAYS_KEY)
        }
    }
}
