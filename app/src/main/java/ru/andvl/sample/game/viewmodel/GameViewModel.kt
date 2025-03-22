package ru.andvl.sample.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.andvl.sample.game.Food
import ru.andvl.sample.game.SnakeGame
import ru.andvl.sample.game.model.GameFood
import ru.andvl.sample.game.model.GameState
import ru.andvl.sample.game.model.GameUiEvent
import ru.andvl.sample.game.model.GameUiState
import kotlin.math.roundToLong

class GameViewModel : ViewModel() {
    
    // Модель логики игры
    private val snakeGame = SnakeGame()
    
    // StateFlow для UI-состояния
    private val _uiState = MutableStateFlow(GameUiState(gameState = GameState.Paused))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    // Job для игрового цикла
    private var gameLoopJob: Job? = null
    
    // Job для анимации смерти
    private var deathAnimationJob: Job? = null
    
    init {
        // Инициализация состояния UI
        updateUiStateFromGameModel()
    }
    
    /**
     * Обработка UI-событий
     */
    fun handleUiEvent(event: GameUiEvent) {
        when (event) {
            is GameUiEvent.StartGame -> startGame()
            is GameUiEvent.PauseGame -> pauseGame()
            is GameUiEvent.ResumeGame -> resumeGame()
            is GameUiEvent.RestartGame -> restartGame()
            is GameUiEvent.ChangeDirection -> snakeGame.changeDirection(event.direction)
            is GameUiEvent.DismissInstructions -> _uiState.update { it.copy(showInstructions = false) }
            is GameUiEvent.ShowInstructions -> _uiState.update { it.copy(showInstructions = true) }
        }
    }
    
    /**
     * Запуск игры
     */
    private fun startGame() {
        if (!isGameLoopRunning()) {
            snakeGame.resetGame()
            updateUiStateFromGameModel()
            _uiState.update { it.copy(gameState = GameState.Running) }
            startGameLoop()
        }
    }
    
    /**
     * Пауза игры
     */
    private fun pauseGame() {
        gameLoopJob?.cancel()
        gameLoopJob = null
        _uiState.update { it.copy(gameState = GameState.Paused) }
    }
    
    /**
     * Возобновление игры
     */
    private fun resumeGame() {
        if (!isGameLoopRunning() && uiState.value.gameState != GameState.GameOver) {
            _uiState.update { it.copy(gameState = GameState.Running) }
            startGameLoop()
        }
    }
    
    /**
     * Перезапуск игры
     */
    private fun restartGame() {
        gameLoopJob?.cancel()
        deathAnimationJob?.cancel()
        
        snakeGame.resetGame()
        updateUiStateFromGameModel()
        
        _uiState.update { 
            it.copy(
                gameState = GameState.Running,
                deathAnimationActive = false
            )
        }
        
        startGameLoop()
    }
    
    /**
     * Запуск игрового цикла в корутине
     */
    private fun startGameLoop() {
        gameLoopJob?.cancel()
        
        gameLoopJob = viewModelScope.launch {
            while (true) {
                // Вычисляем задержку на основе текущей скорости
                val delayTime = (300 / snakeGame.speedFactor).roundToLong()
                delay(delayTime)
                
                // Обновляем состояние игры
                snakeGame.update()
                updateUiStateFromGameModel()
                
                // Проверяем окончание игры
                if (snakeGame.isGameOver) {
                    _uiState.update { it.copy(gameState = GameState.GameOver) }
                    startDeathAnimation()
                    break
                }
            }
        }
    }
    
    /**
     * Запускает анимацию смерти
     */
    private fun startDeathAnimation() {
        deathAnimationJob?.cancel()
        
        deathAnimationJob = viewModelScope.launch {
            _uiState.update { it.copy(deathAnimationActive = true) }
            delay(1500) // Длительность анимации смерти
            _uiState.update { it.copy(deathAnimationActive = false) }
        }
    }
    
    /**
     * Обновление UI-состояния на основе модели игры
     */
    private fun updateUiStateFromGameModel() {
        _uiState.update { state ->
            state.copy(
                snakeParts = snakeGame.snake,
                food = mapFoodToGameFood(snakeGame.food),
                obstacles = snakeGame.obstacles,
                score = snakeGame.score,
                speedFactor = snakeGame.speedFactor,
                doubleScoreActive = snakeGame.doubleScoreActive,
                pulsatingSpeedActive = snakeGame.pulsatingSpeedActive,
                pulsatingPhase = snakeGame.pulsatingPhase
            )
        }
    }
    
    /**
     * Преобразование модели Food в GameFood для UI
     */
    private fun mapFoodToGameFood(food: Food): GameFood {
        return GameFood(
            position = food.position,
            type = food.type
        )
    }
    
    /**
     * Проверка запущен ли игровой цикл
     */
    private fun isGameLoopRunning(): Boolean {
        return gameLoopJob?.isActive == true
    }
    
    /**
     * Очистка ресурсов при уничтожении ViewModel
     */
    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        deathAnimationJob?.cancel()
    }
} 