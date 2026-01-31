package ru.andvl.snakegame.decompose.replayplayer.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.andvl.snakegame.data.GameReplay
import ru.andvl.snakegame.decompose.game.GameUiState

/**
 * Интерфейс хранилища для экрана проигрывания реплея
 */
interface ReplayPlayerStore : Store<ReplayPlayerIntent, ReplayPlayerState, ReplayPlayerLabel>

/**
 * Состояние экрана проигрывания реплея
 */
data class ReplayPlayerState(
    val replay: GameReplay? = null,
    val gameUiState: GameUiState = GameUiState(),
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val currentProgress: Float = 0f,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Намерения пользователя
 */
sealed interface ReplayPlayerIntent {
    data class LoadReplay(val replayId: String) : ReplayPlayerIntent
    data object Play : ReplayPlayerIntent
    data object Pause : ReplayPlayerIntent
    data object Resume : ReplayPlayerIntent
    data class SetPlaybackSpeed(val speed: Float) : ReplayPlayerIntent
    data object NavigateBack : ReplayPlayerIntent
}

/**
 * События (сайд-эффекты)
 */
sealed interface ReplayPlayerLabel {
    data object NavigateBack : ReplayPlayerLabel
    data class ShowMessage(val message: String) : ReplayPlayerLabel
}
