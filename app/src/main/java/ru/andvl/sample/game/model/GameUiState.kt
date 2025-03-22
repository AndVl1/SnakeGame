package ru.andvl.sample.game.model

import androidx.compose.ui.geometry.Offset
import ru.andvl.sample.game.Direction
import ru.andvl.sample.game.FoodType
import ru.andvl.sample.game.Obstacle
import ru.andvl.sample.game.SnakePart

/**
 * UI-состояние игрового экрана
 */
data class GameUiState(
    val gameState: GameState = GameState.Running,
    val snakeParts: List<SnakePart> = emptyList(),
    val food: GameFood = GameFood(),
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val speedFactor: Float = 1.0f,
    val doubleScoreActive: Boolean = false,
    val pulsatingSpeedActive: Boolean = false,
    val pulsatingPhase: Float = 0f,
    val showInstructions: Boolean = true,
    val deathAnimationActive: Boolean = false
)

/**
 * Модель данных о еде для UI
 */
data class GameFood(
    val position: Offset = Offset(0f, 0f),
    val type: FoodType = FoodType.REGULAR
)

/**
 * Состояния игры
 */
enum class GameState {
    Running,
    Paused,
    GameOver
}

/**
 * События пользовательского интерфейса
 */
sealed class GameUiEvent {
    data object StartGame : GameUiEvent()
    data object PauseGame : GameUiEvent()
    data object ResumeGame : GameUiEvent()
    data object RestartGame : GameUiEvent()
    data object DismissInstructions : GameUiEvent()
    data object ShowInstructions : GameUiEvent()
    data class ChangeDirection(val direction: Direction) : GameUiEvent()
} 