package ru.andvl.snakegame.decompose.replay

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
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
import ru.andvl.snakegame.decompose.replay.store.ReplayIntent
import ru.andvl.snakegame.decompose.replay.store.ReplayLabel
import ru.andvl.snakegame.decompose.replay.store.ReplayState
import ru.andvl.snakegame.decompose.replay.store.ReplayStoreFactory
import ru.andvl.snakegame.extensions.asValue

/**
 * Компонент для экрана списка реплеев
 */
class ReplayComponent(
    private val componentContext: ComponentContext,
    private val replayRepository: ReplayRepository,
    private val storeFactory: StoreFactory,
    private val onWatchReplay: (String) -> Unit,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val store = instanceKeeper.getStore {
        ReplayStoreFactory(
            storeFactory = storeFactory,
            replayRepository = replayRepository
        ).create()
    }

    val state: Value<ReplayState> = store.asValue(lifecycle)

    init {
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is ReplayLabel.NavigateToPlayer -> onWatchReplay(label.replayId)
                    is ReplayLabel.NavigateBack -> onNavigateBack()
                    is ReplayLabel.ShowMessage -> {
                        // TODO: Show snackbar or toast
                    }
                }
            }
            .launchIn(scope)

        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }

        store.accept(ReplayIntent.LoadReplays)

        lifecycle.subscribe(object : Lifecycle.Callbacks {
            override fun onResume() {
                store.accept(ReplayIntent.LoadReplays)
            }
        })
    }

    fun onWatchReplayClick(replayId: String) {
        store.accept(ReplayIntent.WatchReplay(replayId))
    }

    fun onDeleteReplayClick(replayId: String) {
        store.accept(ReplayIntent.DeleteReplay(replayId))
    }

    fun onBackClick() {
        store.accept(ReplayIntent.NavigateBack)
    }

    fun refreshReplays() {
        store.accept(ReplayIntent.LoadReplays)
    }
}
