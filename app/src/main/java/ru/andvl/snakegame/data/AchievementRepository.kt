package ru.andvl.snakegame.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository for managing achievements persistence using DataStore
 */
class AchievementRepository(private val context: Context) {

    companion object {
        private val Context.achievementsDataStore: DataStore<Preferences> by preferencesDataStore(
            name = "achievements"
        )
    }

    private val dataStore = context.achievementsDataStore

    /**
     * Get all achievements with current progress
     */
    fun getAchievements(): Flow<AchievementProgress> = dataStore.data.map { preferences ->
        val achievementsMap = Achievement.getAllAchievements().associateBy { it.type }.toMutableMap()

        achievementsMap.forEach { (type, baseAchievement) ->
            val isUnlocked = preferences[booleanPreferencesKey("${type.name}_unlocked")] ?: false
            val unlockedAt = preferences[longPreferencesKey("${type.name}_unlocked_at")]
            val progress = preferences[intPreferencesKey("${type.name}_progress")] ?: 0

            achievementsMap[type] = baseAchievement.copy(
                isUnlocked = isUnlocked,
                unlockedAt = unlockedAt,
                progress = progress
            )
        }

        AchievementProgress(
            achievements = achievementsMap,
            totalGamesPlayed = preferences[intPreferencesKey("total_games_played")] ?: 0,
            specialFoodEaten = preferences[intPreferencesKey("special_food_eaten")] ?: 0
        )
    }

    /**
     * Unlock achievement
     */
    suspend fun unlockAchievement(type: AchievementType) {
        dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("${type.name}_unlocked")] = true
            preferences[longPreferencesKey("${type.name}_unlocked_at")] = System.currentTimeMillis()
        }
    }

    /**
     * Update progress for an achievement
     */
    suspend fun updateProgress(type: AchievementType, progress: Int) {
        dataStore.edit { preferences ->
            preferences[intPreferencesKey("${type.name}_progress")] = progress
        }
    }

    /**
     * Increment total games played
     */
    suspend fun incrementGamesPlayed() {
        dataStore.edit { preferences ->
            val current = preferences[intPreferencesKey("total_games_played")] ?: 0
            preferences[intPreferencesKey("total_games_played")] = current + 1
        }
    }

    /**
     * Increment special food eaten
     */
    suspend fun incrementSpecialFoodEaten() {
        dataStore.edit { preferences ->
            val current = preferences[intPreferencesKey("special_food_eaten")] ?: 0
            preferences[intPreferencesKey("special_food_eaten")] = current + 1
        }
    }

    /**
     * Track achievement progress and unlock if threshold reached
     * Returns true if achievement was just unlocked
     */
    suspend fun trackProgress(type: AchievementType, currentValue: Int): Boolean {
        var wasUnlocked = false

        dataStore.edit { preferences ->
            val isAlreadyUnlocked = preferences[booleanPreferencesKey("${type.name}_unlocked")] ?: false

            if (!isAlreadyUnlocked) {
                preferences[intPreferencesKey("${type.name}_progress")] = currentValue

                val achievement = Achievement.getAllAchievements().find { it.type == type }
                if (achievement != null && currentValue >= achievement.maxProgress) {
                    preferences[booleanPreferencesKey("${type.name}_unlocked")] = true
                    preferences[longPreferencesKey("${type.name}_unlocked_at")] = System.currentTimeMillis()
                    wasUnlocked = true
                }
            }
        }

        return wasUnlocked
    }

    /**
     * Reset all achievements (for testing purposes)
     */
    suspend fun resetAll() {
        dataStore.edit { it.clear() }
    }
}
