package ru.andvl.snakegame.decompose.game.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.Food
import ru.andvl.snakegame.game.model.FoodType
import ru.andvl.snakegame.game.model.GameConstants
import ru.andvl.snakegame.game.model.GridPosition
import ru.andvl.snakegame.game.model.Obstacle
import kotlin.random.Random
import ru.andvl.snakegame.game.model.GameState as GameStateEnum

/**
 * Фабрика для создания GameStore, содержащего логику игры
 */
class GameStoreFactory(
    private val storeFactory: StoreFactory
) {
    companion object {
        // Используем константу из единого источника
        val BOARD_SIZE = GameConstants.BOARD_SIZE
    }

    /**
     * Создать экземпляр GameStore
     */
    fun create(): GameStore =
        object : GameStore, Store<GameIntent, GameState, GameLabel> by storeFactory.create(
            name = "GameStore",
            initialState = GameState(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { createExecutor() },
            reducer = ReducerImpl
        ) {}

    private fun createExecutor() = GameExecutor()

    private inner class GameExecutor : CoroutineExecutor<GameIntent, Unit, GameState, Result, GameLabel>(
        Dispatchers.Main
    ) {
        private var gameLoop: Job? = null
        private var direction = Direction.RIGHT
        private var nextDirection = Direction.RIGHT
        private var gridWidth = BOARD_SIZE
        private var gridHeight = BOARD_SIZE
        private var gameSpeed = 150L  // Начальная скорость

        // Флаги для различных эффектов
        private var doubleScoreActive = false
        private var pulsatingSpeedActive = false
        private var doubleScoreJob: Job? = null
        private var pulsatingSpeedJob: Job? = null

        init {
            // Инициализируем игру при создании
            initGame()
        }

        override fun executeAction(action: Unit, getState: () -> GameState) {
            // Действие при создании уже выполнено в init
        }

        override fun executeIntent(intent: GameIntent, getState: () -> GameState) {
            when (intent) {
                is GameIntent.Initialize -> initGame()
                is GameIntent.PlayPause -> togglePlayPause(getState)
                is GameIntent.Restart -> restartGame()
                is GameIntent.ChangeDirection -> changeDirection(intent.direction)
                is GameIntent.ShowInstructions -> dispatch(Result.ShowInstructions(true))
                is GameIntent.DismissInstructions -> dispatch(Result.ShowInstructions(false))
                is GameIntent.BackPressed -> publish(GameLabel.NavigateBack)
                is GameIntent.SaveScore -> handleSaveScore(intent.name, getState)
                is GameIntent.DismissSaveScore -> handleDismissSaveScore(getState)
                is GameIntent.HandleGameOver -> handleGameOver(getState)
            }
        }

        private fun initGame() {
            scope.launch {
                dispatch(Result.Loading)
                try {
                    val initialSnake = listOf(
                        GridPosition(4, 4),
                        GridPosition(3, 4),
                        GridPosition(2, 4)
                    )
                    val initialFood = generateFood(initialSnake, emptyList())
                    val initialObstacles = generateObstacles(initialSnake, initialFood)

                    // Сброс скорости к начальному значению
                    dispatch(Result.SpeedFactorChanged(1.0f))

                    dispatch(Result.GameInitialized(
                        snakeParts = initialSnake,
                        food = initialFood,
                        obstacles = initialObstacles,
                        gameState = GameStateEnum.Paused,
                        showInstructions = true  // Показываем инструкции при первом запуске
                    ))
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Ошибка инициализации игры"))
                }
            }
        }

        private fun togglePlayPause(getState: () -> GameState) {
            val currentState = getState()
            when(currentState.gameState) {
                GameStateEnum.Running -> pauseGame()
                GameStateEnum.Paused -> resumeGame(getState)
                GameStateEnum.GameOver -> restartGame()
            }
        }

        private fun pauseGame() {
            gameLoop?.cancel()
            dispatch(Result.GameStateChanged(GameStateEnum.Paused))
        }

        private fun resumeGame(getState: () -> GameState) {
            if (getState().gameState != GameStateEnum.Running) {
                dispatch(Result.GameStateChanged(GameStateEnum.Running))
                startGameLoop(getState)
            }
        }

        private fun restartGame() {
            // Отменяем текущую логику
            gameLoop?.cancel()
            doubleScoreJob?.cancel()
            pulsatingSpeedJob?.cancel()

            // Сбрасываем состояние
            direction = Direction.RIGHT
            nextDirection = Direction.RIGHT
            gameSpeed = 150L
            doubleScoreActive = false
            pulsatingSpeedActive = false

            // Сбрасываем значение speedFactor к начальному
            dispatch(Result.SpeedFactorChanged(1.0f))

            // Инициализируем новую игру
            initGame()
        }

        private fun changeDirection(newDirection: Direction) {
            // Проверяем, что новое направление не противоположно текущему
            val isOpposite = (direction == Direction.UP && newDirection == Direction.DOWN) ||
                    (direction == Direction.DOWN && newDirection == Direction.UP) ||
                    (direction == Direction.LEFT && newDirection == Direction.RIGHT) ||
                    (direction == Direction.RIGHT && newDirection == Direction.LEFT)

            if (!isOpposite) {
                nextDirection = newDirection
            }
        }

        private fun startGameLoop(getState: () -> GameState) {
            gameLoop?.cancel()
            gameLoop = scope.launch {
                while (isActive) {
                    delay(gameSpeed)
                    moveSnake(getState)
                }
            }
        }

        private fun moveSnake(getState: () -> GameState) {
            val currentState = getState()
            val currentSnake = currentState.snakeParts.toMutableList()

            // Применить следующее направление
            direction = nextDirection

            // Вычислить новую позицию головы
            val head = currentSnake.firstOrNull() ?: return
            val newHead = when (direction) {
                Direction.UP -> GridPosition(head.x, (head.y - 1 + gridHeight) % gridHeight)
                Direction.DOWN -> GridPosition(head.x, (head.y + 1 + gridHeight) % gridHeight)
                Direction.LEFT -> GridPosition((head.x - 1 + gridWidth) % gridWidth, head.y)
                Direction.RIGHT -> GridPosition((head.x + 1 + gridWidth) % gridWidth, head.y)
            }

            // Проверить столкновение с самим собой или препятствием
            if (isCollision(newHead, currentSnake, currentState.obstacles)) {
                handleCollision(getState)
                return
            }

            // Добавить новую голову
            currentSnake.add(0, newHead)

            // Проверить столкновение с едой
            val food = currentState.food
            if (food != null && newHead.x == food.position.x && newHead.y == food.position.y) {
                handleFoodCollision(food, currentSnake, getState)
            } else {
                // Удалить хвост, если не была съедена еда
                if (currentSnake.size > 0) {
                    currentSnake.removeAt(currentSnake.size - 1)
                }

                // Обновить состояние змейки
                dispatch(Result.SnakeMoved(currentSnake))
            }
        }

        private fun isCollision(head: GridPosition, snake: List<GridPosition>, obstacles: List<Obstacle>): Boolean {
            // Проверка столкновения с телом змеи (не с головой)
            if (snake.size > 1 && snake.subList(1, snake.size).any { it.x == head.x && it.y == head.y }) {
                return true
            }

            // Проверка столкновения с препятствиями
            return obstacles.any { it.position.x == head.x && it.position.y == head.y }
        }

        private fun handleCollision(getState: () -> GameState) {
            dispatch(Result.StartDeathAnimation)

            // Отменить игровой цикл
            gameLoop?.cancel()

            // Запустить анимацию смерти
            scope.launch {
                delay(1000) // Длительность анимации
                dispatch(Result.DeathAnimationComplete)
                dispatch(Result.GameStateChanged(GameStateEnum.GameOver))

                // Показать диалог сохранения счета
                val currentState = getState()
                if (currentState.score > 0) {
                    publish(GameLabel.ShowSaveScoreDialog)
                }
            }
        }

        private fun handleFoodCollision(food: Food, snake: MutableList<GridPosition>, getState: () -> GameState) {
            var scoreIncrease = 1
            var newFood: Food? = null

            // Обработка в зависимости от типа еды
            when (food.type) {
                FoodType.REGULAR -> {
                    scoreIncrease = if (doubleScoreActive) 2 else 1

                    // Увеличиваем счетчик съеденных обычных точек и обновляем скорость
                    val currentScore = getState().score
                    val newScore = currentScore + scoreIncrease

                    // Каждые 3 очка увеличиваем скорость на 0.1
                    if (newScore > 0 && newScore % 3 == 0) {
                        val currentSpeedFactor = getState().speedFactor
                        val newSpeedFactor = currentSpeedFactor + 0.1f
                        dispatch(Result.SpeedFactorChanged(newSpeedFactor))
                    }

                    newFood = generateFood(snake, getState().obstacles)
                }
                FoodType.SPEED_UP -> {
                    scoreIncrease = if (doubleScoreActive) 4 else 2
                    gameSpeed = (gameSpeed * 0.8).toLong() // Увеличиваем скорость

                    // Рассчитываем новый speedFactor
                    val baseSpeed = 150f
                    val newSpeedFactor = baseSpeed / gameSpeed

                    dispatch(Result.SpeedFactorChanged(newSpeedFactor))
                    newFood = generateFood(snake, getState().obstacles)
                }
                FoodType.DOUBLE_SCORE -> {
                    scoreIncrease = if (doubleScoreActive) 3 else 2
                    activateDoubleScore()
                    newFood = generateFood(snake, getState().obstacles)
                }
                FoodType.SLOW_DOWN -> {
                    scoreIncrease = if (doubleScoreActive) 3 else 1
                    gameSpeed = (gameSpeed * 1.2).toLong() // Уменьшаем скорость

                    // Не даем скорости стать слишком низкой
                    if (gameSpeed > 250) gameSpeed = 250

                    // Рассчитываем новый speedFactor
                    val baseSpeed = 150f
                    val newSpeedFactor = baseSpeed / gameSpeed

                    dispatch(Result.SpeedFactorChanged(newSpeedFactor))
                    newFood = generateFood(snake, getState().obstacles)
                }
                FoodType.SPEED_BOOST -> {
                    // Обработка для нового типа еды
                    scoreIncrease = if (doubleScoreActive) 3 else 2
                    newFood = generateFood(snake, getState().obstacles)
                }
            }

            // Увеличиваем счет
            val newScore = getState().score + scoreIncrease

            // Обновляем состояние
            dispatch(Result.FoodCollected(snake, newFood, newScore))
        }

        private fun activateDoubleScore() {
            doubleScoreJob?.cancel()
            doubleScoreActive = true
            dispatch(Result.DoubleScoreChanged(true))

            doubleScoreJob = scope.launch {
                delay(10000) // 10 секунд двойного счета
                doubleScoreActive = false
                dispatch(Result.DoubleScoreChanged(false))
            }
        }

        private fun generateFood(snake: List<GridPosition>, obstacles: List<Obstacle>): Food {
            val availablePositions = mutableListOf<GridPosition>()

            // Собираем все свободные позиции
            for (x in 0 until gridWidth) {
                for (y in 0 until gridHeight) {
                    val pos = GridPosition(x, y)
                    val isSnakePart = snake.any { it.x == pos.x && it.y == pos.y }
                    val isObstacle = obstacles.any { it.position.x == pos.x && it.position.y == pos.y }

                    if (!isSnakePart && !isObstacle) {
                        availablePositions.add(pos)
                    }
                }
            }

            // Выбираем случайную позицию
            val position = if (availablePositions.isNotEmpty()) {
                availablePositions.random()
            } else {
                // Если нет свободных позиций, используем крайнее решение
                GridPosition(0, 0)
            }

            // Выбираем тип еды с разными вероятностями
            val foodTypeRandom = Random.nextFloat()
            val foodType = when {
                foodTypeRandom < 0.05f -> FoodType.DOUBLE_SCORE // 5% вероятность
                foodTypeRandom < 0.1f -> FoodType.SPEED_BOOST   // 5% вероятность
                foodTypeRandom < 0.15f -> FoodType.SPEED_UP     // 5% вероятность
                foodTypeRandom < 0.2f -> FoodType.SLOW_DOWN     // 5% вероятность
                else -> FoodType.REGULAR                        // 80% вероятность
            }

            return Food(position, foodType)
        }

        private fun generateObstacles(snake: List<GridPosition>, food: Food): List<Obstacle> {
            val obstacles = mutableListOf<Obstacle>()
            val obstacleCount = 5 // Начинаем с 5 препятствий

            // Создаем случайные препятствия
            while (obstacles.size < obstacleCount) {
                val x = Random.nextInt(gridWidth)
                val y = Random.nextInt(gridHeight)
                val position = GridPosition(x, y)

                // Проверяем, что препятствие не накладывается на змею или еду
                val isOverlappingSnake = snake.any { it.x == position.x && it.y == position.y }
                val isOverlappingFood = food.position.x == position.x && food.position.y == position.y
                val isOverlappingObstacle = obstacles.any { it.position.x == position.x && it.position.y == position.y }

                if (!isOverlappingSnake && !isOverlappingFood && !isOverlappingObstacle) {
                    obstacles.add(Obstacle(position))
                }
            }

            return obstacles
        }

        private fun handleGameOver(getState: () -> GameState) {
            val currentState = getState()
            if (currentState.gameState == GameStateEnum.GameOver && !currentState.deathAnimationActive) {
                publish(GameLabel.ShowSaveScoreDialog)
            }
        }

        private fun handleSaveScore(name: String, getState: () -> GameState) {
            val currentState = getState()
            publish(GameLabel.NavigateToLeaderboard(
                score = currentState.score,
                speedFactor = currentState.speedFactor,
                playerName = name
            ))
        }

        private fun handleDismissSaveScore(getState: () -> GameState) {
            val currentState = getState()
            publish(GameLabel.NavigateToLeaderboard(
                score = currentState.score,
                speedFactor = currentState.speedFactor,
                playerName = null
            ))
        }
    }

    /**
     * Результаты операций для обновления состояния
     */
    private sealed interface Result {
        object Loading : Result
        data class GameInitialized(
            val snakeParts: List<GridPosition>,
            val food: Food,
            val obstacles: List<Obstacle>,
            val gameState: GameStateEnum,
            val showInstructions: Boolean
        ) : Result
        data class GameStateChanged(val gameState: GameStateEnum) : Result
        data class SnakeMoved(val newSnakeParts: List<GridPosition>) : Result
        data class FoodCollected(
            val newSnakeParts: List<GridPosition>,
            val newFood: Food?,
            val newScore: Int
        ) : Result
        data class SpeedFactorChanged(val speedFactor: Float) : Result
        data class DoubleScoreChanged(val active: Boolean) : Result
        object StartDeathAnimation : Result
        object DeathAnimationComplete : Result
        data class ShowInstructions(val show: Boolean) : Result
        data class Error(val message: String) : Result
    }

    /**
     * Редуктор для обновления состояния на основе результатов
     */
    private object ReducerImpl : Reducer<GameState, Result> {
        override fun GameState.reduce(result: Result): GameState =
            when (result) {
                is Result.Loading -> copy(isLoading = true, error = null)
                is Result.GameInitialized -> copy(
                    snakeParts = result.snakeParts,
                    food = result.food,
                    obstacles = result.obstacles,
                    gameState = result.gameState,
                    showInstructions = result.showInstructions,
                    isLoading = false,
                    error = null
                )
                is Result.GameStateChanged -> copy(gameState = result.gameState)
                is Result.SnakeMoved -> copy(snakeParts = result.newSnakeParts)
                is Result.FoodCollected -> copy(
                    snakeParts = result.newSnakeParts,
                    food = result.newFood,
                    score = result.newScore
                )
                is Result.SpeedFactorChanged -> copy(speedFactor = result.speedFactor)
                is Result.DoubleScoreChanged -> copy(doubleScoreActive = result.active)
                is Result.StartDeathAnimation -> copy(deathAnimationActive = true)
                is Result.DeathAnimationComplete -> copy(deathAnimationActive = false)
                is Result.ShowInstructions -> copy(showInstructions = result.show)
                is Result.Error -> copy(isLoading = false, error = result.message)
            }
    }
}
