package ru.andvl.snakegame.decompose.leaderboard.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.andvl.snakegame.data.PlayerScore

/**
 * Интерфейс хранилища для экрана лидеров, реализующий контракт MVI
 */
interface LeaderboardStore : Store<LeaderboardIntent, LeaderboardState, LeaderboardLabel>

/**
 * Состояние экрана лидеров
 */
data class LeaderboardState(
    val topScores: List<PlayerScore> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Намерения пользователя для взаимодействия с экраном лидеров
 */
sealed interface LeaderboardIntent {
    object LoadScores : LeaderboardIntent
    object StartGame : LeaderboardIntent
    object OpenSettings : LeaderboardIntent
}

/**
 * События (сайд-эффекты), которые происходят на экране лидеров
 */
sealed interface LeaderboardLabel {
    object NavigateToGame : LeaderboardLabel
    object NavigateToSettings : LeaderboardLabel
    data class ShowMessage(val message: String) : LeaderboardLabel
} 
