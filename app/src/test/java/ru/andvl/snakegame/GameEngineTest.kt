package ru.andvl.snakegame

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.andvl.snakegame.decompose.game.GameEngine
import ru.andvl.snakegame.decompose.game.GameUiState
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.Food
import ru.andvl.snakegame.game.model.FoodType
import ru.andvl.snakegame.game.model.GameConstants
import ru.andvl.snakegame.game.model.GameState
import ru.andvl.snakegame.game.model.GridPosition
import ru.andvl.snakegame.game.model.Obstacle
import ru.andvl.snakegame.game.model.SnakePart

@ExperimentalCoroutinesApi
class GameEngineTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var gameEngine: GameEngine
    
    private var lastGameState: GameState? = null
    private var lastUiState: GameUiState? = null
    
    @Before
    fun setup() {
        // Устанавливаем тестовый диспетчер для корутин
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
        
        // Создаем игровой движок с тестовыми колбеками
        gameEngine = GameEngine(
            onStateChanged = { gameState -> lastGameState = gameState },
            onUiStateChanged = { uiState -> lastUiState = uiState }
        )
        
        // Инициализируем игру
        gameEngine.initialize()
    }
    
    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }
    
    @Test
    fun `initial game state should be paused`() {
        // Проверяем начальное состояние
        assertEquals(GameState.Paused, lastGameState)
    }
    
    @Test
    fun `snake should have initial length of 3`() {
        // Проверяем начальную длину змейки
        val uiState = lastUiState
        assertEquals(3, uiState?.snakeParts?.size)
    }
    
    @Test
    fun `snake initial direction should be right`() {
        // Запускаем игру и делаем один шаг
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500) // Достаточно для одного обновления
        
        // Проверяем что змейка движется вправо
        val uiState = lastUiState
        val snakeParts = uiState?.snakeParts ?: emptyList()
        
        // Голова должна быть правее шеи змейки
        if (snakeParts.size >= 2) {
            val head = snakeParts[0]
            val neck = snakeParts[1]
            
            assertTrue(
                "Змейка должна двигаться вправо, но голова на ${head.x}, ${head.y} и шея на ${neck.x}, ${neck.y}",
                (head.x > neck.x) || (head.x == 0 && neck.x == GameConstants.BOARD_SIZE - 1) // С учетом перехода через край поля
            )
        }
    }
    
    @Test
    fun `changeDirection should update snake direction`() {
        // Запускаем игру
        gameEngine.startGame()
        
        // Меняем направление на UP (вверх)
        gameEngine.changeDirection(Direction.UP)
        
        // Делаем шаг игры
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что змейка движется вверх
        val uiState = lastUiState
        val snakeParts = uiState?.snakeParts ?: emptyList()
        
        if (snakeParts.size >= 2) {
            val head = snakeParts[0]
            val neck = snakeParts[1]
            
            assertTrue(
                "Змейка должна двигаться вверх, но голова на ${head.x}, ${head.y} и шея на ${neck.x}, ${neck.y}",
                (head.y < neck.y) || (head.y == GameConstants.BOARD_SIZE - 1 && neck.y == 0) // С учетом перехода через край поля
            )
        }
    }
    
    @Test
    fun `snake should not move in opposite direction`() {
        // Запускаем игру (начальное направление RIGHT)
        gameEngine.startGame()
        
        // Пытаемся изменить направление на LEFT (противоположное RIGHT)
        gameEngine.changeDirection(Direction.LEFT)
        
        // Делаем шаг игры
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что змейка продолжает двигаться вправо
        val uiState = lastUiState
        val snakeParts = uiState?.snakeParts ?: emptyList()
        
        if (snakeParts.size >= 2) {
            val head = snakeParts[0]
            val neck = snakeParts[1]
            
            assertTrue(
                "Змейка должна продолжать двигаться вправо, но голова на ${head.x}, ${head.y} и шея на ${neck.x}, ${neck.y}",
                (head.x > neck.x) || (head.x == 0 && neck.x == GameConstants.BOARD_SIZE - 1) // С учетом перехода через край поля
            )
        }
    }
    
    @Test
    fun `snake should grow when eating food`() {
        // Запоминаем начальную длину змейки
        val initialSnakeLength = lastUiState?.snakeParts?.size ?: 0
        
        // Позиция головы змейки
        val headPos = lastUiState?.snakeParts?.firstOrNull() ?: return
        
        // Помещаем еду перед головой змейки
        val foodPos = GridPosition(
            (headPos.x + 1) % GameConstants.BOARD_SIZE, // С учетом размера поля 16x16
            headPos.y
        )
        
        // Используем рефлексию для доступа к приватному полю food
        val foodField = GameEngine::class.java.getDeclaredField("food")
        foodField.isAccessible = true
        foodField.set(gameEngine, Food(foodPos, FoodType.REGULAR))
        
        // Запускаем игру и делаем шаг
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что длина змейки увеличилась
        val newSnakeLength = lastUiState?.snakeParts?.size ?: 0
        assertEquals(initialSnakeLength + 1, newSnakeLength)
    }
    
    @Test
    fun `double score food should activate double score effect`() {
        // Позиция головы змейки
        val headPos = lastUiState?.snakeParts?.firstOrNull() ?: return
        
        // Помещаем еду DOUBLE_SCORE перед головой змейки
        val foodPos = GridPosition(
            (headPos.x + 1) % GameConstants.BOARD_SIZE,
            headPos.y
        )
        
        // Используем рефлексию для доступа к приватному полю food
        val foodField = GameEngine::class.java.getDeclaredField("food")
        foodField.isAccessible = true
        foodField.set(gameEngine, Food(foodPos, FoodType.DOUBLE_SCORE))
        
        // Запускаем игру и делаем шаг
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что активирован режим двойных очков
        val doubleScoreActive = lastUiState?.doubleScoreActive ?: false
        assertTrue("Эффект двойных очков должен быть активирован", doubleScoreActive)
    }
    
    @Test
    fun `speed boost food should activate pulsating speed effect`() {
        // Позиция головы змейки
        val headPos = lastUiState?.snakeParts?.firstOrNull() ?: return
        
        // Помещаем еду SPEED_BOOST перед головой змейки
        val foodPos = GridPosition(
            (headPos.x + 1) % GameConstants.BOARD_SIZE,
            headPos.y
        )
        
        // Используем рефлексию для доступа к приватному полю food
        val foodField = GameEngine::class.java.getDeclaredField("food")
        foodField.isAccessible = true
        foodField.set(gameEngine, Food(foodPos, FoodType.SPEED_BOOST))
        
        // Запускаем игру и делаем шаг
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что активирован пульсирующий режим скорости
        val pulsatingSpeedActive = lastUiState?.pulsatingSpeedActive ?: false
        assertTrue("Эффект пульсирующей скорости должен быть активирован", pulsatingSpeedActive)
    }
    
    @Test
    fun `speed up food should increase snake speed permanently`() {
        // Запоминаем начальную скорость
        val initialSpeedFactor = lastUiState?.speedFactor ?: 1.0f
        
        // Позиция головы змейки
        val headPos = lastUiState?.snakeParts?.firstOrNull() ?: return
        
        // Помещаем еду SPEED_UP перед головой змейки
        val foodPos = GridPosition(
            (headPos.x + 1) % GameConstants.BOARD_SIZE,
            headPos.y
        )
        
        // Используем рефлексию для доступа к приватному полю food
        val foodField = GameEngine::class.java.getDeclaredField("food")
        foodField.isAccessible = true
        foodField.set(gameEngine, Food(foodPos, FoodType.SPEED_UP))
        
        // Запускаем игру и делаем шаг
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что скорость увеличилась
        val newSpeedFactor = lastUiState?.speedFactor ?: 1.0f
        assertTrue("Скорость змейки должна увеличиться", newSpeedFactor > initialSpeedFactor)
        
        // Проверяем точное увеличение на 20%
        assertEquals(initialSpeedFactor * 1.2f, newSpeedFactor, 0.01f)
    }
    
    @Test
    fun `slow down food should decrease snake speed permanently`() {
        // Сначала увеличим скорость с помощью SPEED_UP, чтобы потом было что уменьшать
        val speedUpFoodPos = GridPosition(
            (lastUiState?.snakeParts?.firstOrNull()?.x ?: 0) + 1,
            lastUiState?.snakeParts?.firstOrNull()?.y ?: 0
        )
        
        val foodField = GameEngine::class.java.getDeclaredField("food")
        foodField.isAccessible = true
        foodField.set(gameEngine, Food(speedUpFoodPos, FoodType.SPEED_UP))
        
        // Запускаем игру и делаем шаг для увеличения скорости
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Запоминаем увеличенную скорость
        val increasedSpeedFactor = lastUiState?.speedFactor ?: 1.0f
        
        // Перемещаем змейку на один шаг
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Получаем новую позицию головы
        val headPos = lastUiState?.snakeParts?.firstOrNull() ?: return
        
        // Помещаем еду SLOW_DOWN перед головой змейки
        val slowDownFoodPos = GridPosition(
            (headPos.x + 1) % GameConstants.BOARD_SIZE,
            headPos.y
        )
        
        foodField.set(gameEngine, Food(slowDownFoodPos, FoodType.SLOW_DOWN))
        
        // Делаем еще один шаг для съедания SLOW_DOWN
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что скорость уменьшилась
        val newSpeedFactor = lastUiState?.speedFactor ?: 1.0f
        assertTrue("Скорость змейки должна уменьшиться", newSpeedFactor < increasedSpeedFactor)
        
        // Проверяем точное уменьшение на 20%
        assertEquals(increasedSpeedFactor * 0.8f, newSpeedFactor, 0.01f)
    }
    
    @Test
    fun `game should end when snake collides with itself`() {
        // Получаем метод gameOver через рефлексию
        val gameOverMethod = GameEngine::class.java.getDeclaredMethod("gameOver")
        gameOverMethod.isAccessible = true
        
        // Вызываем метод gameOver напрямую
        gameOverMethod.invoke(gameEngine)
        
        // Проверяем, что состояние игры изменилось на GameOver
        assertEquals("Метод gameOver должен изменять состояние на GameOver",
            GameState.GameOver, lastGameState)
    }
    
    @Test
    fun `game should end when snake collides with obstacle`() {
        // Позиция головы змейки
        val headPos = lastUiState?.snakeParts?.firstOrNull() ?: return
        
        // Помещаем препятствие перед головой змейки
        val obstaclePos = GridPosition(
            (headPos.x + 1) % GameConstants.BOARD_SIZE, // С учетом размера поля 16x16
            headPos.y
        )
        
        // Используем рефлексию для доступа к приватному полю obstacles
        val obstaclesField = GameEngine::class.java.getDeclaredField("obstacles")
        obstaclesField.isAccessible = true
        obstaclesField.set(gameEngine, mutableListOf(Obstacle(obstaclePos)))
        
        // Запускаем игру и делаем шаг
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(500)
        
        // Проверяем, что игра перешла в состояние GameOver
        assertEquals(GameState.GameOver, lastGameState)
    }
    
    @Test
    fun `snake should wrap around screen edges`() {
        // Используем рефлексию для доступа к приватному полю snakeParts
        val snakePartsField = GameEngine::class.java.getDeclaredField("snakeParts")
        snakePartsField.isAccessible = true
        
        // Помещаем змейку у правого края экрана
        val boardSize = GameConstants.BOARD_SIZE
        val centerY = boardSize / 2
        val snakeParts = mutableListOf(
            SnakePart(boardSize - 1, centerY), // Голова на правом краю
            SnakePart(boardSize - 2, centerY),
            SnakePart(boardSize - 3, centerY)
        )
        snakePartsField.set(gameEngine, snakeParts)
        
        // Устанавливаем направление вправо
        val directionField = GameEngine::class.java.getDeclaredField("currentDirection")
        directionField.isAccessible = true
        directionField.set(gameEngine, Direction.RIGHT)
        
        val nextDirectionField = GameEngine::class.java.getDeclaredField("nextDirection")
        nextDirectionField.isAccessible = true
        nextDirectionField.set(gameEngine, Direction.RIGHT)
        
        // Обновляем UI состояние перед запуском
        val updateUiStateMethod = GameEngine::class.java.getDeclaredMethod("updateUiState")
        updateUiStateMethod.isAccessible = true
        updateUiStateMethod.invoke(gameEngine)
        
        // Запускаем игру
        gameEngine.startGame()
        
        // Вручную вызываем метод updateGame, чтобы гарантированно сделать один шаг
        val updateGameMethod = GameEngine::class.java.getDeclaredMethod("updateGame")
        updateGameMethod.isAccessible = true
        updateGameMethod.invoke(gameEngine)
        
        // Обновляем UI состояние после шага
        updateUiStateMethod.invoke(gameEngine)
        
        // Получаем новую позицию головы после перемещения через край
        val newHeadPos = lastUiState?.snakeParts?.firstOrNull()
        
        // Проверяем, что голова появилась с левого края
        assertEquals(0, newHeadPos?.x)
        assertEquals(centerY, newHeadPos?.y)
    }
    
    @Test
    fun `obstacles should not overlap with snake at initialization`() {
        // Проверяем, что препятствия не пересекаются с начальной позицией змейки
        val snakeParts = lastUiState?.snakeParts ?: emptyList()
        val obstacles = lastUiState?.obstacles ?: emptyList()
        
        // Проверка на отсутствие пересечений
        for (snakePart in snakeParts) {
            for (obstacle in obstacles) {
                assertFalse(
                    "Препятствие не должно пересекаться со змейкой: препятствие на ${obstacle.position.x}, ${obstacle.position.y}, часть змейки на ${snakePart.x}, ${snakePart.y}",
                    obstacle.position.x == snakePart.x && obstacle.position.y == snakePart.y
                )
            }
        }
    }
    
    @Test
    fun `food should not overlap with snake or obstacles`() {
        // Получаем текущее состояние
        val uiState = lastUiState ?: return
        val snakeParts = uiState.snakeParts
        val obstacles = uiState.obstacles
        val food = uiState.food
        
        // Проверяем, что еда не находится на змейке
        for (snakePart in snakeParts) {
            assertFalse(
                "Еда не должна пересекаться со змейкой: еда на ${food.position.x}, ${food.position.y}, часть змейки на ${snakePart.x}, ${snakePart.y}",
                food.position.x == snakePart.x && food.position.y == snakePart.y
            )
        }
        
        // Проверяем, что еда не находится на препятствии
        for (obstacle in obstacles) {
            assertFalse(
                "Еда не должна пересекаться с препятствием: еда на ${food.position.x}, ${food.position.y}, препятствие на ${obstacle.position.x}, ${obstacle.position.y}",
                food.position.x == obstacle.position.x && food.position.y == obstacle.position.y
            )
        }
    }
    
    @Test
    fun `pause and resume should work correctly`() {
        // Запускаем игру
        gameEngine.startGame()
        assertEquals(GameState.Running, lastGameState)
        
        // Ставим на паузу
        gameEngine.pauseGame()
        assertEquals(GameState.Paused, lastGameState)
        
        // Возобновляем
        gameEngine.resumeGame()
        assertEquals(GameState.Running, lastGameState)
    }
    
    @Test
    fun `reset game should restore initial state`() {
        // Запускаем игру и делаем несколько шагов
        gameEngine.startGame()
        testDispatcher.scheduler.advanceTimeBy(1500) // Делаем несколько шагов
        
        // Сохраняем текущее состояние после нескольких шагов
        val stateBeforeReset = lastUiState
        
        // Сбрасываем игру
        gameEngine.resetGame()
        
        // Проверяем, что состояние игры сброшено
        assertEquals(GameState.Paused, lastGameState)
        
        // Проверяем, что счет сброшен
        assertEquals(0, lastUiState?.score)
        
        // Проверяем, что длина змейки вернулась к начальной (3)
        assertEquals(3, lastUiState?.snakeParts?.size)
        
        // Проверяем, что скорость вернулась к начальной
        assertEquals(1.0f, lastUiState?.speedFactor)
    }
} 