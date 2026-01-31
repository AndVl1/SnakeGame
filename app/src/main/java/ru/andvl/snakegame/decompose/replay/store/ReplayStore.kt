package ru.andvl.snakegame.decompose.replay.store

import com.arkivanov.mvikotlin.core.store.Store
import ru.andvl.snakegame.data.GameReplay

/**
 * Интерфейс хранилища для экрана реплеев
 */
interface ReplayStore : Store<ReplayIntent, ReplayState, ReplayLabel>

/**
 * Состояние экрана реплеев
 */
data class ReplayState(
    val replays: List<GameReplay> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Намерения пользователя
 */
sealed interface ReplayIntent {
    data object LoadReplays : ReplayIntent
    data class DeleteReplay(val id: String) : ReplayIntent
    data class WatchReplay(val id: String) : ReplayIntent
    data object NavigateBack : ReplayIntent
}

/**
 * События (сайд-эффекты)
 */
sealed interface ReplayLabel {
    data class NavigateToPlayer(val replayId: String) : ReplayLabel
    data object NavigateBack : ReplayLabel
    data class ShowMessage(val message: String) : ReplayLabel
}
