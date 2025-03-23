package ru.andvl.snakegame.decompose.leaderboard.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andvl.snakegame.data.ScoreRepository

/**
 * Фабрика для создания LeaderboardStore
 */
class LeaderboardStoreFactory(
    private val storeFactory: StoreFactory,
    private val scoreRepository: ScoreRepository
) {
    /**
     * Создать экземпляр LeaderboardStore
     */
    fun create(): LeaderboardStore =
        object : LeaderboardStore, Store<LeaderboardIntent, LeaderboardState, LeaderboardLabel> by storeFactory.create(
            name = "LeaderboardStore",
            initialState = LeaderboardState(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { createExecutor() },
            reducer = ReducerImpl
        ) {}
    
    private fun createExecutor() = object : CoroutineExecutor<LeaderboardIntent, Unit, LeaderboardState, Result, LeaderboardLabel>(
        Dispatchers.Main
    ) {
        init {
            loadScores()
        }
        
        override fun executeAction(action: Unit, getState: () -> LeaderboardState) {
            // Нет действий для обработки в этом сторе
        }
        
        override fun executeIntent(intent: LeaderboardIntent, getState: () -> LeaderboardState) {
            when (intent) {
                is LeaderboardIntent.LoadScores -> loadScores()
                is LeaderboardIntent.StartGame -> publish(LeaderboardLabel.NavigateToGame)
                is LeaderboardIntent.OpenSettings -> publish(LeaderboardLabel.NavigateToSettings)
                is LeaderboardIntent.BackPressed -> publish(LeaderboardLabel.NavigateBack)
            }
        }
        
        private fun loadScores() {
            scope.launch {
                dispatch(Result.Loading)
                try {
                    val scores = withContext(Dispatchers.IO) {
                        scoreRepository.getTopScores()
                    }
                    dispatch(Result.ScoresLoaded(scores))
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Произошла ошибка при загрузке результатов"))
                    publish(LeaderboardLabel.ShowMessage(e.message ?: "Произошла ошибка при загрузке результатов"))
                }
            }
        }
    }
    
    /**
     * Результаты операций для обновления состояния
     */
    private sealed interface Result {
        object Loading : Result
        data class ScoresLoaded(val scores: List<ru.andvl.snakegame.data.PlayerScore>) : Result
        data class Error(val message: String) : Result
    }
    
    /**
     * Редуктор для обновления состояния на основе результатов
     */
    private object ReducerImpl : Reducer<LeaderboardState, Result> {
        override fun LeaderboardState.reduce(result: Result): LeaderboardState =
            when (result) {
                is Result.Loading -> copy(isLoading = true, error = null)
                is Result.ScoresLoaded -> copy(topScores = result.scores, isLoading = false, error = null)
                is Result.Error -> copy(error = result.message, isLoading = false)
            }
    }
} 
