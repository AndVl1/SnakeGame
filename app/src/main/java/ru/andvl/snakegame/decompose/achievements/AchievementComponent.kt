package ru.andvl.snakegame.decompose.achievements

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
import ru.andvl.snakegame.data.AchievementRepository
import ru.andvl.snakegame.decompose.achievements.store.AchievementIntent
import ru.andvl.snakegame.decompose.achievements.store.AchievementLabel
import ru.andvl.snakegame.decompose.achievements.store.AchievementState
import ru.andvl.snakegame.decompose.achievements.store.AchievementStoreFactory
import ru.andvl.snakegame.extensions.asValue

/**
 * Component for achievements screen using MVIKotlin
 */
class AchievementComponent(
    private val componentContext: ComponentContext,
    private val achievementRepository: AchievementRepository,
    private val storeFactory: StoreFactory,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext {

    // Use SupervisorJob for resilience to coroutine errors
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Create and store in instanceKeeper
    private val store = instanceKeeper.getStore {
        AchievementStoreFactory(
            storeFactory = storeFactory,
            achievementRepository = achievementRepository
        ).create()
    }

    // Expose state as Value for Decompose with lifecycle binding
    val state: Value<AchievementState> = store.asValue(lifecycle)

    init {
        // Subscribe to labels from the store
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is AchievementLabel.ShowMessage -> {
                        // TODO: Show message to user (could use Toast or Snackbar)
                    }
                    AchievementLabel.NavigateToLeaderboard -> {
                        onNavigateBack.invoke()
                    }
                }
            }
            .launchIn(scope)

        // Cancel coroutines on component destruction
        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }

        // Load achievements on init
        store.accept(AchievementIntent.LoadAchievements)
    }

    // Public methods for UI

    fun onBackClick() {
        store.accept(AchievementIntent.NavigateBack)
    }
}
