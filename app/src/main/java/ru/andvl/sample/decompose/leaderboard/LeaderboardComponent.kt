package ru.andvl.sample.decompose.leaderboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.andvl.sample.data.PlayerScore
import ru.andvl.sample.data.ScoreRepository

/**
 * Компонент для экрана лидеров
 */
class LeaderboardComponent(
    componentContext: ComponentContext,
    private val scoreRepository: ScoreRepository,
    private val onStartGameClick: () -> Unit,
    private val onSettingsClick: () -> Unit
) : ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main.immediate)
    
    private val _state = MutableValue(State())
    val state: Value<State> = _state
    
    init {
        observeScores()
    }
    
    private fun observeScores() {
        scoreRepository.scores
            .onEach { scores ->
                _state.value = _state.value.copy(scores = scores)
            }
            .launchIn(scope)
    }
    
    fun onStartGameClicked() {
        onStartGameClick()
    }
    
    fun onSettingsClicked() {
        onSettingsClick()
    }
    
    data class State(
        val scores: List<PlayerScore> = emptyList()
    )
} 