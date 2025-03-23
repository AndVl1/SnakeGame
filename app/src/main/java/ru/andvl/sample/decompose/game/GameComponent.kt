package ru.andvl.sample.decompose.game

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.andvl.sample.game.SnakePart
import ru.andvl.sample.game.model.Direction
import ru.andvl.sample.game.model.Food
import ru.andvl.sample.game.model.FoodType
import ru.andvl.sample.game.model.GameModelConverter
import ru.andvl.sample.game.model.GameState
import ru.andvl.sample.game.model.GridPosition
import ru.andvl.sample.game.model.Obstacle

/**
 * Компонент для игрового экрана
 */
class GameComponent(
    componentContext: ComponentContext,
    private val onNavigateToLeaderboard: (score: Int, speedFactor: Float, playerName: String?) -> Unit,
    private val onBack: () -> Unit
) : ComponentContext by componentContext {

    private val scope = CoroutineScope(Dispatchers.Main)
    
    private val _state = MutableValue(State())
    val state: Value<State> = _state
    
    // Для сохранения логики из оригинального ViewModel создадим GameEngine
    private val gameEngine = GameEngine(
        onStateChanged = { gameState ->
            _state.value = _state.value.copy(gameState = gameState)
        },
        onUiStateChanged = { uiState ->
            // Конвертация SnakePart в GridPosition
            val snakeParts = uiState.snakeParts.map { snakePart ->
                GridPosition(snakePart.x, snakePart.y)
            }
            
            // Логирование для отладки проблемы с коллизией
            println("DEBUG GameComponent: Обновление состояния UI")
            println("DEBUG GameComponent: Еда в GameEngine: ${uiState.food.position.x}, ${uiState.food.position.y}")
            
            _state.value = _state.value.copy(
                snakeParts = snakeParts,
                food = uiState.food,
                obstacles = uiState.obstacles,
                score = uiState.score,
                speedFactor = uiState.speedFactor,
                doubleScoreActive = uiState.doubleScoreActive,
                pulsatingSpeedActive = uiState.pulsatingSpeedActive,
                deathAnimationActive = uiState.deathAnimationActive,
                showInstructions = uiState.showInstructions
            )
        }
    )
    
    // Флаг для отслеживания диалога сохранения счета
    private var saveScoreDialogHandled = false
    private val _showSaveScoreDialog = MutableValue(false)
    val showSaveScoreDialog: Value<Boolean> = _showSaveScoreDialog
    
    private var playerName = MutableStateFlow<String?>(null)
    val playerNameFlow = playerName.asStateFlow()
    
    init {
        // Инициализация компонента
        gameEngine.initialize()
    }
    
    fun onDirectionChange(direction: Direction) {
        gameEngine.changeDirection(direction)
    }
    
    fun onPlayPauseClick() {
        when (_state.value.gameState) {
            GameState.Running -> gameEngine.pauseGame()
            GameState.Paused -> gameEngine.resumeGame()
            GameState.GameOver -> gameEngine.resetGame()
        }
    }
    
    fun onRestartClick() {
        gameEngine.resetGame()
    }
    
    fun onShowInstructionsClick() {
        gameEngine.showInstructions()
    }
    
    fun onDismissInstructions() {
        gameEngine.dismissInstructions()
    }
    
    fun onBackPressed() {
        // Остановить игру и вызвать onBack
        gameEngine.pauseGame()
        onBack()
    }
    
    fun onSaveScore(name: String) {
        _showSaveScoreDialog.value = false
        playerName.value = name
        onNavigateToLeaderboard(_state.value.score, _state.value.speedFactor, name)
    }
    
    fun onDismissSaveScore() {
        _showSaveScoreDialog.value = false
        onNavigateToLeaderboard(_state.value.score, _state.value.speedFactor, null)
    }
    
    // Обработка завершения игры
    fun handleGameOver() {
        if (_state.value.gameState == GameState.GameOver && 
            !_state.value.deathAnimationActive && 
            !saveScoreDialogHandled) {
            saveScoreDialogHandled = true
            _showSaveScoreDialog.value = true
        }
    }
    
    data class State(
        val snakeParts: List<GridPosition> = emptyList(),
        val food: Food = Food(GridPosition(0, 0), FoodType.REGULAR),
        val obstacles: List<Obstacle> = emptyList(),
        val score: Int = 0,
        val speedFactor: Float = 1.0f,
        val doubleScoreActive: Boolean = false,
        val pulsatingSpeedActive: Boolean = false,
        val deathAnimationActive: Boolean = false,
        val showInstructions: Boolean = false,
        val gameState: GameState = GameState.Paused
    )
} 