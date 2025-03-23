package ru.andvl.snakegame.decompose.game.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.Food
import ru.andvl.snakegame.game.model.GridPosition
import ru.andvl.snakegame.game.model.Obstacle
import ru.andvl.snakegame.game.model.GameState as GameStateEnum

/**
 * Интерфейс хранилища для Game, реализующий контракт MVI
 */
interface GameStore : Store<GameIntent, GameState, GameLabel>

/**
 * Состояние игры
 */
data class GameState(
    val snakeParts: List<GridPosition> = emptyList(),
    val food: Food? = null,
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val speedFactor: Float = 1.0f,
    val doubleScoreActive: Boolean = false,
    val pulsatingSpeedActive: Boolean = false,
    val deathAnimationActive: Boolean = false,
    val showInstructions: Boolean = false,
    val gameState: GameStateEnum = GameStateEnum.Paused,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Намерения пользователя для взаимодействия с игрой
 */
sealed interface GameIntent {
    object Initialize : GameIntent
    object PlayPause : GameIntent
    object Restart : GameIntent
    data class ChangeDirection(val direction: Direction) : GameIntent
    object ShowInstructions : GameIntent
    object DismissInstructions : GameIntent
    object BackPressed : GameIntent
    data class SaveScore(val name: String) : GameIntent
    object DismissSaveScore : GameIntent
    object HandleGameOver : GameIntent
}

/**
 * События (сайд-эффекты), которые происходят в игре
 */
sealed interface GameLabel {
    object NavigateBack : GameLabel
    data class NavigateToLeaderboard(val score: Int, val speedFactor: Float, val playerName: String?) : GameLabel
    object ShowSaveScoreDialog : GameLabel
    data class ShowMessage(val message: String) : GameLabel
}
