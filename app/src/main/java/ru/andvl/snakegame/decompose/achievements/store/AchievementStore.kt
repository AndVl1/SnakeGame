package ru.andvl.snakegame.decompose.achievements.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.andvl.snakegame.data.Achievement

/**
 * Interface for Achievement Store implementing MVI contract
 */
interface AchievementStore : Store<AchievementIntent, AchievementState, AchievementLabel>

/**
 * State of the achievements screen
 */
data class AchievementState(
    val achievements: List<Achievement> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * User intents for interacting with achievements
 */
sealed interface AchievementIntent {
    data object LoadAchievements : AchievementIntent
    data object NavigateBack : AchievementIntent
}

/**
 * Events (side effects) from the achievements screen
 */
sealed interface AchievementLabel {
    data object NavigateToLeaderboard : AchievementLabel
    data class ShowMessage(val message: String) : AchievementLabel
}
