package ru.andvl.snakegame.decompose.achievements.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andvl.snakegame.data.Achievement
import ru.andvl.snakegame.data.AchievementRepository

/**
 * Factory for creating AchievementStore
 */
class AchievementStoreFactory(
    private val storeFactory: StoreFactory,
    private val achievementRepository: AchievementRepository
) {
    /**
     * Create instance of AchievementStore
     */
    fun create(): AchievementStore =
        object : AchievementStore, Store<AchievementIntent, AchievementState, AchievementLabel> by storeFactory.create(
            name = "AchievementStore",
            initialState = AchievementState(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { createExecutor() },
            reducer = ReducerImpl
        ) {}

    private fun createExecutor() = object : CoroutineExecutor<AchievementIntent, Unit, AchievementState, Result, AchievementLabel>(
        Dispatchers.Main
    ) {
        init {
            loadAchievements()
        }

        override fun executeAction(action: Unit, getState: () -> AchievementState) {
            // No actions to process in this store
        }

        override fun executeIntent(intent: AchievementIntent, getState: () -> AchievementState) {
            when (intent) {
                is AchievementIntent.LoadAchievements -> loadAchievements()
                is AchievementIntent.NavigateBack -> publish(AchievementLabel.NavigateToLeaderboard)
            }
        }

        private fun loadAchievements() {
            scope.launch {
                dispatch(Result.Loading)
                try {
                    achievementRepository.getAchievements().collect { achievementProgress ->
                        val sortedAchievements = achievementProgress.achievements.values.toList()
                            .sortedWith(
                                compareByDescending<Achievement> { it.isUnlocked }
                                    .thenByDescending { it.progress }
                            )
                        dispatch(Result.AchievementsLoaded(sortedAchievements))
                    }
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Failed to load achievements"))
                    publish(AchievementLabel.ShowMessage(e.message ?: "Failed to load achievements"))
                }
            }
        }
    }

    /**
     * Results of operations for state updates
     */
    private sealed interface Result {
        data object Loading : Result
        data class AchievementsLoaded(val achievements: List<Achievement>) : Result
        data class Error(val message: String) : Result
    }

    /**
     * Reducer for updating state based on results
     */
    private object ReducerImpl : Reducer<AchievementState, Result> {
        override fun AchievementState.reduce(result: Result): AchievementState =
            when (result) {
                is Result.Loading -> copy(isLoading = true, error = null)
                is Result.AchievementsLoaded -> copy(
                    achievements = result.achievements,
                    isLoading = false,
                    error = null
                )
                is Result.Error -> copy(error = result.message, isLoading = false)
            }
    }
}
