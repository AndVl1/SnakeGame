package ru.andvl.snakegame.decompose.replay.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andvl.snakegame.data.GameReplay
import ru.andvl.snakegame.data.ReplayRepository

/**
 * Фабрика для создания ReplayStore
 */
class ReplayStoreFactory(
    private val storeFactory: StoreFactory,
    private val replayRepository: ReplayRepository
) {
    fun create(): ReplayStore =
        object : ReplayStore, Store<ReplayIntent, ReplayState, ReplayLabel> by storeFactory.create(
            name = "ReplayStore",
            initialState = ReplayState(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { createExecutor() },
            reducer = ReducerImpl
        ) {}

    private fun createExecutor() = object : CoroutineExecutor<ReplayIntent, Unit, ReplayState, Result, ReplayLabel>(
        Dispatchers.Main
    ) {
        init {
            loadReplays()
        }

        override fun executeAction(action: Unit, getState: () -> ReplayState) {
            // No actions to handle
        }

        override fun executeIntent(intent: ReplayIntent, getState: () -> ReplayState) {
            when (intent) {
                is ReplayIntent.LoadReplays -> loadReplays()
                is ReplayIntent.DeleteReplay -> deleteReplay(intent.id)
                is ReplayIntent.WatchReplay -> publish(ReplayLabel.NavigateToPlayer(intent.id))
                is ReplayIntent.NavigateBack -> publish(ReplayLabel.NavigateBack)
            }
        }

        private fun loadReplays() {
            scope.launch {
                dispatch(Result.Loading)
                try {
                    val replays = withContext(Dispatchers.IO) {
                        replayRepository.getAllReplays()
                    }
                    dispatch(Result.ReplaysLoaded(replays))
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Failed to load replays"))
                    publish(ReplayLabel.ShowMessage(e.message ?: "Failed to load replays"))
                }
            }
        }

        private fun deleteReplay(id: String) {
            scope.launch {
                try {
                    withContext(Dispatchers.IO) {
                        replayRepository.deleteReplay(id)
                    }
                    loadReplays()
                    publish(ReplayLabel.ShowMessage("Replay deleted"))
                } catch (e: Exception) {
                    publish(ReplayLabel.ShowMessage(e.message ?: "Failed to delete replay"))
                }
            }
        }
    }

    private sealed interface Result {
        data object Loading : Result
        data class ReplaysLoaded(val replays: List<GameReplay>) : Result
        data class Error(val message: String) : Result
    }

    private object ReducerImpl : Reducer<ReplayState, Result> {
        override fun ReplayState.reduce(result: Result): ReplayState =
            when (result) {
                is Result.Loading -> copy(isLoading = true, error = null)
                is Result.ReplaysLoaded -> copy(replays = result.replays, isLoading = false, error = null)
                is Result.Error -> copy(error = result.message, isLoading = false)
            }
    }
}
