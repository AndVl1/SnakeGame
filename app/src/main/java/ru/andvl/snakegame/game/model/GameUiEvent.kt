package ru.andvl.snakegame.game.model

import ru.andvl.snakegame.game.Direction

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
