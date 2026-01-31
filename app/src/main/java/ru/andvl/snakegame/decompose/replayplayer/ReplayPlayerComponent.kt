package ru.andvl.snakegame.decompose.replayplayer

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.andvl.snakegame.data.ReplayRepository
import ru.andvl.snakegame.decompose.replayplayer.store.ReplayPlayerIntent
import ru.andvl.snakegame.decompose.replayplayer.store.ReplayPlayerLabel
import ru.andvl.snakegame.decompose.replayplayer.store.ReplayPlayerState
import ru.andvl.snakegame.decompose.replayplayer.store.ReplayPlayerStoreFactory
import ru.andvl.snakegame.extensions.asValue

/**
 * Компонент для экрана проигрывания реплея
 */
class ReplayPlayerComponent(
    private val componentContext: ComponentContext,
    private val replayId: String,
    private val replayRepository: ReplayRepository,
    private val storeFactory: StoreFactory,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val store = instanceKeeper.getStore {
        ReplayPlayerStoreFactory(
            storeFactory = storeFactory,
            replayRepository = replayRepository
        ).create()
    }

    val state: Value<ReplayPlayerState> = store.asValue(lifecycle)

    init {
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is ReplayPlayerLabel.NavigateBack -> onNavigateBack()
                    is ReplayPlayerLabel.ShowMessage -> {
                        // TODO: Show snackbar or toast
                    }
                }
            }
            .launchIn(scope)

        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }

        // Load replay when component is created
        store.accept(ReplayPlayerIntent.LoadReplay(replayId))
    }

    fun onPlayClick() {
        store.accept(ReplayPlayerIntent.Play)
    }

    fun onPauseClick() {
        store.accept(ReplayPlayerIntent.Pause)
    }

    fun onResumeClick() {
        store.accept(ReplayPlayerIntent.Resume)
    }

    fun onPlaybackSpeedChange(speed: Float) {
        store.accept(ReplayPlayerIntent.SetPlaybackSpeed(speed))
    }

    fun onBackClick() {
        store.accept(ReplayPlayerIntent.NavigateBack)
    }
}
