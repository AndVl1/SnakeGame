package ru.andvl.sample.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.random.Random

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class SnakePart(val x: Int, val y: Int)

// Типы специальной еды
enum class FoodType {
    REGULAR,    // Обычная еда
    SPEED_UP,   // Увеличивает скорость
    DOUBLE_SCORE, // Двойные очки
    SLOW_DOWN   // Замедление
}

// Данные о еде
data class Food(
    val position: Offset,
    val type: FoodType,
    val points: Int = 1,
    val expiresAt: Long = 0 // 0 означает без истечения срока
)

// Препятствие на поле
data class Obstacle(val x: Int, val y: Int)

class SnakeGame(
    val boardSize: Int = 16,
    private val initialSnakeLength: Int = 3,
) {
    var snake by mutableStateOf(listOf<SnakePart>())
        private set
    
    var food by mutableStateOf(Food(Offset(0f, 0f), FoodType.REGULAR))
        private set
    
    var obstacles by mutableStateOf(listOf<Obstacle>())
        private set
    
    var direction by mutableStateOf(Direction.RIGHT)
        private set
    
    var isGameOver by mutableStateOf(false)
        private set
    
    var score by mutableStateOf(0)
        private set
        
    var speedFactor by mutableStateOf(1.0f)
        private set
    
    // Эффекты от еды
    var doubleScoreActive by mutableStateOf(false)
        private set
    
    var pulsatingSpeedActive by mutableStateOf(false)
        private set
    
    var pulsatingPhase by mutableStateOf(0f)
        private set
    
    // Базовая скорость перед активацией пульсации
    private var baseSpeedBeforePulsation: Float = 1.0f
    
    // Максимальное количество препятствий
    private var maxObstacles = 3
    
    // Счетчик шагов для обновления игры
    private var stepCounter = 0
    
    // Время действия специальных эффектов
    private var doubleScoreUntil = 0L
    private var pulsatingSpeedUntil = 0L
    
    init {
        resetGame()
    }
    
    fun resetGame() {
        // Создаем змейку в центре
        val centerPos = boardSize / 2
        val initialSnake = mutableListOf<SnakePart>()
        for (i in 0 until initialSnakeLength) {
            initialSnake.add(SnakePart(centerPos - i, centerPos))
        }
        snake = initialSnake
        direction = Direction.RIGHT
        isGameOver = false
        score = 0
        speedFactor = 1.0f
        baseSpeedBeforePulsation = 1.0f
        doubleScoreActive = false
        pulsatingSpeedActive = false
        pulsatingPhase = 0f
        stepCounter = 0
        
        // Создаем препятствия
        generateObstacles()
        
        // Генерируем еду
        spawnFood()
    }
    
    fun changeDirection(newDirection: Direction) {
        // Предотвращаем движение в противоположную сторону
        direction = when(newDirection) {
            Direction.UP -> if (direction != Direction.DOWN) newDirection else direction
            Direction.DOWN -> if (direction != Direction.UP) newDirection else direction
            Direction.LEFT -> if (direction != Direction.RIGHT) newDirection else direction
            Direction.RIGHT -> if (direction != Direction.LEFT) newDirection else direction
        }
    }
    
    fun update() {
        if (isGameOver) return
        
        // Обновляем счетчик шагов
        stepCounter++
        
        // Обновляем эффекты от еды
        updateEffects()
        
        // Обновляем пульсацию скорости
        if (pulsatingSpeedActive) {
            pulsatingPhase = (pulsatingPhase + 0.05f) % 1f
            // Скорость колеблется между 0.8 и 1.3 от базовой
            // Используем сохраненную базовую скорость для расчетов
            val pulseFactor = 0.5f * kotlin.math.sin(pulsatingPhase * 2 * Math.PI.toFloat()) + 1.05f
            speedFactor = baseSpeedBeforePulsation * pulseFactor
        }
        
        val head = snake.first()
        
        // Вычисляем новую позицию головы
        var newX = when (direction) {
            Direction.LEFT -> head.x - 1
            Direction.RIGHT -> head.x + 1
            else -> head.x
        }
        
        var newY = when (direction) {
            Direction.UP -> head.y - 1
            Direction.DOWN -> head.y + 1
            else -> head.y
        }
        
        // Обработка прохода через стены (появление с другой стороны)
        if (newX < 0) newX = boardSize - 1
        if (newX >= boardSize) newX = 0
        if (newY < 0) newY = boardSize - 1
        if (newY >= boardSize) newY = 0
        
        val newHead = SnakePart(newX, newY)
        
        // Проверка на столкновение с самой собой
        if (snake.contains(newHead)) {
            isGameOver = true
            return
        }
        
        // Проверка на столкновение с препятствием
        if (obstacles.any { it.x == newHead.x && it.y == newHead.y }) {
            isGameOver = true
            return
        }
        
        // Проверка, съела ли змейка еду
        val ateFood = newHead.x == food.position.x.toInt() && newHead.y == food.position.y.toInt()
        
        // Создание новой змейки с новой головой
        val newSnake = mutableListOf(newHead)
        newSnake.addAll(if (ateFood) snake else snake.dropLast(1))
        snake = newSnake
        
        if (ateFood) {
            handleFoodConsumption()
        }
        
        // Каждые 30 шагов пробуем добавить новое препятствие
        if (stepCounter % 30 == 0 && obstacles.size < maxObstacles) {
            addRandomObstacle()
        }
        
        // Проверяем, не истек ли срок действия текущей еды
        if (food.expiresAt > 0 && System.currentTimeMillis() > food.expiresAt) {
            spawnFood() // Создаем новую еду, если время действия истекло
        }
    }
    
    private fun handleFoodConsumption() {
        when (food.type) {
            FoodType.REGULAR -> {
                // Обычная еда: +1 очко
                score += if (doubleScoreActive) 2 else 1
            }
            FoodType.SPEED_UP -> {
                // Ускорение: +1 очко и +20% к скорости
                score += if (doubleScoreActive) 2 else 1
                speedFactor *= 1.2f
            }
            FoodType.DOUBLE_SCORE -> {
                // Двойные очки: +1 очко и активация удвоения на 10 секунд
                score += if (doubleScoreActive) 2 else 1
                doubleScoreActive = true
                doubleScoreUntil = System.currentTimeMillis() + 10000
            }
            FoodType.SLOW_DOWN -> {
                // Замедление: +1 очко и замедление на -15% от текущей скорости
                score += if (doubleScoreActive) 2 else 1
                
                // Сохраняем текущую базовую скорость перед активацией пульсации
                if (!pulsatingSpeedActive) {
                    baseSpeedBeforePulsation = speedFactor
                }
                
                speedFactor *= 0.85f
                
                // Активируем пульсирующую скорость на 15 секунд
                pulsatingSpeedActive = true
                pulsatingSpeedUntil = System.currentTimeMillis() + 15000
            }
        }
        
        // Увеличиваем скорость после каждых 5 съеденных точек
        if (score % 5 == 0 && food.type == FoodType.REGULAR) {
            speedFactor *= 1.1f
        }
        
        // Добавляем препятствие, если съедена специальная еда и змейка достаточно длинная
        if (food.type != FoodType.REGULAR && snake.size > 5) {
            addRandomObstacle()
        }
        
        spawnFood()
    }
    
    private fun updateEffects() {
        val currentTime = System.currentTimeMillis()
        
        // Проверяем, не истек ли срок удвоения очков
        if (doubleScoreActive && currentTime > doubleScoreUntil) {
            doubleScoreActive = false
        }
        
        // Проверяем, не истек ли срок пульсирующей скорости
        if (pulsatingSpeedActive && currentTime > pulsatingSpeedUntil) {
            pulsatingSpeedActive = false
            
            // Восстанавливаем базовую скорость
            speedFactor = baseSpeedBeforePulsation
        }
    }
    
    private fun generateObstacles() {
        val newObstacles = mutableListOf<Obstacle>()
        
        // Сначала очищаем существующие препятствия
        obstacles = emptyList()
        
        // На начальном этапе добавляем только 1-2 препятствия
        val obstacleCount = 1 + Random.nextInt(2)
        
        for (i in 0 until obstacleCount) {
            addRandomObstacle()
        }
    }
    
    private fun addRandomObstacle() {
        val currentObstacles = obstacles.toMutableList()
        
        // Пытаемся найти подходящее место для препятствия
        var attempts = 0
        while (attempts < 20) {
            val obstacleX = Random.nextInt(boardSize)
            val obstacleY = Random.nextInt(boardSize)
            val newObstacle = Obstacle(obstacleX, obstacleY)
            
            // Проверяем, что препятствие не на змейке и не на еде
            val isOnSnake = snake.any { it.x == obstacleX && it.y == obstacleY }
            val isOnFood = food.position.x.toInt() == obstacleX && food.position.y.toInt() == obstacleY
            val isOnExistingObstacle = obstacles.any { it.x == obstacleX && it.y == obstacleY }
            
            if (!isOnSnake && !isOnFood && !isOnExistingObstacle) {
                currentObstacles.add(newObstacle)
                obstacles = currentObstacles
                return
            }
            
            attempts++
        }
    }
    
    private fun spawnFood() {
        // Выбираем тип еды с разной вероятностью
        val foodTypeRoll = Random.nextFloat()
        val foodType = when {
            foodTypeRoll < 0.7f -> FoodType.REGULAR
            foodTypeRoll < 0.8f -> FoodType.SPEED_UP
            foodTypeRoll < 0.9f -> FoodType.DOUBLE_SCORE
            else -> FoodType.SLOW_DOWN
        }
        
        // Срок действия для специальной еды
        val expiryTime = if (foodType != FoodType.REGULAR) {
            System.currentTimeMillis() + 8000 // 8 секунд на съедение специальной еды
        } else {
            0L // Обычная еда не имеет срока действия
        }
        
        // Генерируем случайную позицию для еды, которая не находится на змейке или препятствиях
        var newFoodPos: Offset
        do {
            newFoodPos = Offset(
                Random.nextInt(boardSize).toFloat(),
                Random.nextInt(boardSize).toFloat()
            )
            
            val isOnSnake = snake.any { it.x == newFoodPos.x.toInt() && it.y == newFoodPos.y.toInt() }
            val isOnObstacle = obstacles.any { it.x == newFoodPos.x.toInt() && it.y == newFoodPos.y.toInt() }
            
        } while (isOnSnake || isOnObstacle)
        
        food = Food(
            position = newFoodPos,
            type = foodType,
            points = if (foodType == FoodType.DOUBLE_SCORE) 2 else 1,
            expiresAt = expiryTime
        )
    }
}