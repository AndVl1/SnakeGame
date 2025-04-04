package ru.andvl.snakegame.decompose.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
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
import ru.andvl.snakegame.decompose.game.store.GameIntent
import ru.andvl.snakegame.decompose.game.store.GameLabel
import ru.andvl.snakegame.decompose.game.store.GameState
import ru.andvl.snakegame.decompose.game.store.GameStoreFactory
import ru.andvl.snakegame.extensions.asValue
import ru.andvl.snakegame.game.model.Direction

/**
 * Компонент для игрового экрана с использованием MVIKotlin
 */
class GameComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val onNavigateToLeaderboard: (score: Int, speedFactor: Float, playerName: String?) -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    // Используем SupervisorJob для устойчивости к ошибкам в корутинах
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Создаем и сохраняем store в instanceKeeper
    private val store = instanceKeeper.getStore {
        GameStoreFactory(storeFactory).create()
    }

    // Экспонируем state как Value для Decompose с привязкой к lifecycle
    val state: Value<GameState> = store.asValue(lifecycle)

    // Флаг для отображения диалога сохранения счета
    private val _showSaveScoreDialog = MutableValue(false)
    val showSaveScoreDialog: Value<Boolean> = _showSaveScoreDialog

    init {
        // Слушаем лейблы из стора
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is GameLabel.NavigateToLeaderboard -> onNavigateToLeaderboard(
                        label.score,
                        label.speedFactor,
                        label.playerName
                    )
                    is GameLabel.NavigateBack -> onBack()
                    is GameLabel.ShowSaveScoreDialog -> _showSaveScoreDialog.value = true
                    is GameLabel.ShowMessage -> { /* Реализация показа сообщения */ }
                }
            }
            .launchIn(scope)

        // Отменяем корутины при уничтожении компонента
        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }
    }

    // Методы для взаимодействия с игрой

    fun onDirectionChange(direction: Direction) {
        store.accept(GameIntent.ChangeDirection(direction))
    }

    fun onPlayPauseClick() {
        store.accept(GameIntent.PlayPause)
    }

    fun onRestartClick() {
        store.accept(GameIntent.Restart)
    }

    fun onShowInstructionsClick() {
        store.accept(GameIntent.ShowInstructions)
    }

    fun onDismissInstructions() {
        store.accept(GameIntent.DismissInstructions)
    }

    fun onSaveScore(playerName: String) {
        store.accept(GameIntent.SaveScore(playerName))
        _showSaveScoreDialog.value = false
    }

    fun onDismissSaveScore() {
        store.accept(GameIntent.DismissSaveScore)
        _showSaveScoreDialog.value = false
    }

    fun onBackPressed() {
        store.accept(GameIntent.BackPressed)
    }

    fun handleGameOver() {
        store.accept(GameIntent.HandleGameOver)
    }
}
