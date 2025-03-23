package ru.andvl.snakegame.decompose.game

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.Food
import ru.andvl.snakegame.game.model.FoodType
import ru.andvl.snakegame.game.model.GameState
import ru.andvl.snakegame.game.model.GridPosition
import ru.andvl.snakegame.game.model.Obstacle
import ru.andvl.snakegame.game.model.SnakePart
import kotlin.random.Random

/**
 * Класс, представляющий состояние UI игры
 */
data class GameUiState(
    val snakeParts: List<SnakePart> = emptyList(),
    val food: Food = Food(GridPosition(0, 0), FoodType.REGULAR),
    val obstacles: List<Obstacle> = emptyList(),
    val score: Int = 0,
    val speedFactor: Float = 1.0f,
    val doubleScoreActive: Boolean = false,
    val pulsatingSpeedActive: Boolean = false,
    val deathAnimationActive: Boolean = false,
    val showInstructions: Boolean = true
)

/**
 * Игровой движок, который управляет логикой игры
 */
class GameEngine(
    private val onStateChanged: (GameState) -> Unit,
    private val onUiStateChanged: (GameUiState) -> Unit
) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var gameUpdateJob: Job? = null
    private var deathAnimationJob: Job? = null
    
    // Константы игры
    private val boardSize = 16
    private val initialSnakeLength = 3
    private val initialUpdateDelay = 200L
    private val speedBoostMultiplier = 0.8f
    private val doubleScoreDuration = 5000L
    private val speedBoostDuration = 5000L
    private val maxObstacles = 5
    private val CELL_SIZE = 16f
    
    // Игровое состояние
    private var gameState = GameState.Paused
    private var currentUpdateDelay = initialUpdateDelay
    private var snakeParts = mutableListOf<SnakePart>()
    private var currentDirection = Direction.RIGHT
    private var nextDirection = Direction.RIGHT
    private var food = Food(GridPosition(0, 0), FoodType.REGULAR)
    private var obstacles = mutableListOf<Obstacle>()
    private var score = 0
    private var speedFactor = 1.0f
    private var baseSpeedFactor = 1.0f // Базовый множитель скорости без временных эффектов
    private var foodEatenCount = 0 // Счетчик съеденных точек
    
    // Флаги эффектов
    private var doubleScoreActive = false
    private var pulsatingSpeedActive = false
    private var pulsatingPhase = 0f
    private var doubleScoreJob: Job? = null
    private var pulsatingSpeedJob: Job? = null
    
    // Флаги для анимации
    private var deathAnimationActive = false
    private var showInstructions = true
    
    /**
     * Инициализация движка
     */
    fun initialize() {
        resetGame()
    }
    
    /**
     * Запуск игры
     */
    fun startGame() {
        if (gameState != GameState.Running) {
            gameState = GameState.Running
            onStateChanged(gameState)
            startGameLoop()
        }
    }
    
    /**
     * Перезапуск игры
     */
    fun resetGame() {
        // Отменяем существующие задачи
        gameUpdateJob?.cancel()
        doubleScoreJob?.cancel()
        pulsatingSpeedJob?.cancel()
        deathAnimationJob?.cancel()
        
        // Сбрасываем состояние
        gameState = GameState.Paused
        currentUpdateDelay = initialUpdateDelay
        currentDirection = Direction.RIGHT
        nextDirection = Direction.RIGHT
        score = 0
        speedFactor = 1.0f
        baseSpeedFactor = 1.0f // Сбрасываем базовую скорость
        foodEatenCount = 0 // Сбрасываем счетчик съеденных точек
        doubleScoreActive = false
        pulsatingSpeedActive = false
        deathAnimationActive = false
        
        // Создаем змейку в центре экрана
        val centerY = boardSize / 2
        val startX = boardSize / 2
        snakeParts.clear()
        for (i in 0 until initialSnakeLength) {
            snakeParts.add(SnakePart(startX - i, centerY))
        }
        
        // Размещаем еду и препятствия
        spawnObstacles()
        spawnFood()
        
        // Уведомляем о смене состояния
        onStateChanged(gameState)
        updateUiState()
    }
    
    /**
     * Приостановка игры
     */
    fun pauseGame() {
        if (gameState == GameState.Running) {
            gameState = GameState.Paused
            gameUpdateJob?.cancel()
            onStateChanged(gameState)
        }
    }
    
    /**
     * Возобновление игры
     */
    fun resumeGame() {
        if (gameState == GameState.Paused) {
            gameState = GameState.Running
            onStateChanged(gameState)
            startGameLoop()
        }
    }
    
    /**
     * Изменение направления движения
     */
    fun changeDirection(direction: Direction) {
        // Предотвращаем движение в противоположную сторону
        nextDirection = when(direction) {
            Direction.UP -> if (currentDirection != Direction.DOWN) direction else currentDirection
            Direction.DOWN -> if (currentDirection != Direction.UP) direction else currentDirection
            Direction.LEFT -> if (currentDirection != Direction.RIGHT) direction else currentDirection
            Direction.RIGHT -> if (currentDirection != Direction.LEFT) direction else currentDirection
        }
    }
    
    /**
     * Показать инструкции
     */
    fun showInstructions() {
        showInstructions = true
        updateUiState()
    }
    
    /**
     * Скрыть инструкции
     */
    fun dismissInstructions() {
        showInstructions = false
        updateUiState()
    }
    
    /**
     * Запуск игрового цикла
     */
    private fun startGameLoop() {
        gameUpdateJob?.cancel()
        gameUpdateJob = scope.launch {
            while (gameState == GameState.Running) {
                updateGame()
                val delay = calculateUpdateDelay()
                delay(delay)
            }
        }
    }
    
    /**
     * Рассчитывает задержку между обновлениями
     */
    private fun calculateUpdateDelay(): Long {
        val baseDelay = (initialUpdateDelay / speedFactor).toLong()
        
        return if (pulsatingSpeedActive) {
            // Расчет пульсирующей скорости
            val pulseFactor = 0.5f + 0.5f * kotlin.math.sin(System.currentTimeMillis() / 200.0)
            (baseDelay * (1.0f + pulseFactor * 0.5f)).toLong()
        } else {
            baseDelay
        }
    }
    
    /**
     * Обновление игры
     */
    private fun updateGame() {
        if (gameState != GameState.Running) return
        
        // Обновление направления
        currentDirection = nextDirection
        
        // Получение текущей головы
        val head = snakeParts.first()
        
        // Вычисление новой позиции головы с корректной обработкой краев поля
        val newHead = when (currentDirection) {
            Direction.UP -> SnakePart(head.x, if (head.y > 0) head.y - 1 else boardSize - 1)
            Direction.DOWN -> SnakePart(head.x, (head.y + 1) % boardSize)
            Direction.LEFT -> SnakePart(if (head.x > 0) head.x - 1 else boardSize - 1, head.y)
            Direction.RIGHT -> SnakePart((head.x + 1) % boardSize, head.y)
        }
        
        // Проверка на столкновение с самим собой
        if (snakeParts.any { it.x == newHead.x && it.y == newHead.y }) {
            gameOver()
            return
        }
        
        // Проверка на столкновение с препятствием
        if (obstacles.any { it.position.x == newHead.x && it.position.y == newHead.y }) {
            gameOver()
            return
        }
        
        // Добавление новой головы
        snakeParts.add(0, newHead)
        
        // Координаты головы змейки
        val headX = newHead.x
        val headY = newHead.y
        
        // Координаты точки
        val foodX = food.position.x
        val foodY = food.position.y
        
        // Проверка точного совпадения координат
        val foodEaten = (headX == foodX && headY == foodY)
        
        if (foodEaten) {
            // Если съели точку
            // Начисляем очки и активируем эффекты
            processEatenFood()
            
            // Создаем новую точку
            spawnFood()
        } else {
            // Если точку не съели, удаляем хвост змейки
            if (snakeParts.size > 0) {
                snakeParts.removeAt(snakeParts.size - 1)
            }
        }
        
        // Обновление UI
        updateUiState()
    }
    
    /**
     * Обработка съеденной точки
     */
    private fun processEatenFood() {
        // Начисляем очки
        val points = if (doubleScoreActive) 2 else 1
        score += points
        
        // Обрабатываем логику увеличения скорости для обычных точек
        if (food.type == FoodType.REGULAR) {
            // Увеличиваем счетчик съеденных точек
            foodEatenCount++
            
            // Каждые 3 съеденные точки увеличиваем скорость на 0.1
            if (foodEatenCount % 3 == 0) {
                baseSpeedFactor += 0.1f
                speedFactor = baseSpeedFactor // Обновляем текущую скорость
                updateUiState() // Обновляем UI для отображения новой скорости
            }
        }
        
        // Применяем эффекты в зависимости от типа точки
        when (food.type) {
            FoodType.REGULAR -> {
                // Обработка происходит выше
            }
            FoodType.DOUBLE_SCORE -> {
                activateDoubleScore()
            }
            FoodType.SPEED_BOOST -> {
                activateSpeedBoost()
            }
        }
    }
    
    /**
     * Активация двойных очков
     */
    private fun activateDoubleScore() {
        // Активируем эффект
        doubleScoreActive = true
        
        // Обновляем UI немедленно
        updateUiState()
        
        // Отменяем предыдущую задачу, если она была
        doubleScoreJob?.cancel()
        
        // Запускаем новую задачу для отключения эффекта через заданное время
        doubleScoreJob = scope.launch {
            delay(doubleScoreDuration)
            doubleScoreActive = false
            updateUiState()
        }
    }
    
    /**
     * Активация ускорения
     */
    private fun activateSpeedBoost() {
        // Сохраняем текущую базовую скорость для восстановления позже
        val savedBaseSpeedFactor = baseSpeedFactor
        val savedFoodEatenCount = foodEatenCount
        
        // Применяем временный эффект замедления (умножаем на коэффициент < 1.0 для замедления)
        speedFactor = baseSpeedFactor * speedBoostMultiplier
        
        // Обновляем UI немедленно, чтобы отобразить изменение скорости
        updateUiState()
        
        // Отменяем предыдущую задачу, если она была
        pulsatingSpeedJob?.cancel()
        
        // Активируем пульсирующий эффект
        pulsatingSpeedActive = true
        
        // Запускаем новую задачу для отключения эффекта через заданное время
        pulsatingSpeedJob = scope.launch {
            delay(speedBoostDuration)
            
            // Отключаем визуальный эффект пульсации
            pulsatingSpeedActive = false
            
            // Возвращаем сохраненную скорость
            baseSpeedFactor = savedBaseSpeedFactor
            speedFactor = baseSpeedFactor
            foodEatenCount = savedFoodEatenCount
            
            // Обновляем UI после изменений
            updateUiState()
        }
    }
    
    /**
     * Обработка конца игры
     */
    private fun gameOver() {
        gameState = GameState.GameOver
        gameUpdateJob?.cancel()
        
        // Запускаем анимацию смерти
        startDeathAnimation()
        
        // Уведомляем о смене состояния
        onStateChanged(gameState)
        updateUiState()
    }
    
    /**
     * Запуск анимации смерти
     */
    private fun startDeathAnimation() {
        deathAnimationActive = true
        updateUiState()
        
        deathAnimationJob = scope.launch {
            delay(1500) // Длительность анимации
            deathAnimationActive = false
            updateUiState()
        }
    }
    
    /**
     * Создание точки в случайном месте
     */
    private fun spawnFood() {
        // Список всех доступных позиций (не занятых змейкой или препятствиями)
        val availablePositions = mutableListOf<GridPosition>()
        
        // Проходим по всей сетке и находим свободные позиции
        for (x in 0 until boardSize) {
            for (y in 0 until boardSize) {
                val position = GridPosition(x, y)
                
                // Проверяем что позиция не занята змейкой или препятствием
                val positionFree = !snakeParts.any { it.x == position.x && it.y == position.y } && 
                               !obstacles.any { it.position.x == position.x && it.position.y == position.y }
                
                if (positionFree) {
                    availablePositions.add(position)
                }
            }
        }
        
        // Если есть доступные позиции, выбираем случайную
        if (availablePositions.isNotEmpty()) {
            val randomPosition = availablePositions.random()
            
            // Выбираем случайный тип точки
            val foodType = when {
                Random.nextFloat() < 0.1f -> FoodType.DOUBLE_SCORE // 10% вероятность
                Random.nextFloat() < 0.2f -> FoodType.SPEED_BOOST  // 20% вероятность
                else -> FoodType.REGULAR                           // 70% вероятность
            }
            
            // Создаем новую точку
            food = Food(randomPosition, foodType)
        }
    }
    
    /**
     * Создание препятствий
     */
    private fun spawnObstacles() {
        obstacles.clear()
        
        // Добавляем случайное количество препятствий
        val obstacleCount = Random.nextInt(1, maxObstacles + 1)
        
        for (i in 0 until obstacleCount) {
            val availablePositions = mutableListOf<GridPosition>()
            
            // Собираем все свободные позиции на полном поле (включая последний столбец и строку)
            for (x in 0 until boardSize) {
                for (y in 0 until boardSize) {
                    val position = GridPosition(x, y)
                    if (!snakeParts.any { it.x == position.x && it.y == position.y } && 
                        position != food.position && 
                        !obstacles.any { it.position == position }) {
                        availablePositions.add(position)
                    }
                }
            }
            
            if (availablePositions.isNotEmpty()) {
                val position = availablePositions.random()
                obstacles.add(Obstacle(position))
            }
        }
    }
    
    /**
     * Обновляет состояние UI
     */
    private fun updateUiState() {
        onUiStateChanged(
            GameUiState(
                snakeParts = snakeParts.toList(),
                food = food,
                obstacles = obstacles,
                score = score,
                speedFactor = speedFactor,
                doubleScoreActive = doubleScoreActive,
                pulsatingSpeedActive = pulsatingSpeedActive,
                deathAnimationActive = deathAnimationActive,
                showInstructions = showInstructions
            )
        )
    }
} 
