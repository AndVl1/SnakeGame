package ru.andvl.snakegame

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.Timeout
import ru.andvl.snakegame.decompose.game.GameEngine
import ru.andvl.snakegame.decompose.game.GameUiState
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.GameState
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class GameEngineTest {

    // Устанавливаем глобальный таймаут для всех тестов в 10 секунд
    @get:Rule
    val testTimeout: Timeout = Timeout(10, TimeUnit.SECONDS)

    // Используем StandardTestDispatcher для тестирования корутин
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var gameEngine: GameEngine
    
    // Вместо var переменных создадим списки для хранения истории состояний
    private val gameStates = mutableListOf<GameState>()
    private val uiStates = mutableListOf<GameUiState>()
    
    @Before
    fun setup() {
        // Устанавливаем тестовый диспетчер для корутин
        Dispatchers.setMain(testDispatcher)
        
        // Очищаем списки состояний перед каждым тестом
        gameStates.clear()
        uiStates.clear()
        
        // Создаем игровой движок с колбеками, которые добавляют состояния в списки
        gameEngine = GameEngine(
            onStateChanged = { gameState -> gameStates.add(gameState) },
            onUiStateChanged = { uiState -> uiStates.add(uiState) }
        )
        
        // Инициализируем игру
        gameEngine.initialize()
        
        // Завершаем все отложенные задачи
        testDispatcher.scheduler.runCurrent()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun initialGameStateShouldBePaused() {
        // Проверяем начальное состояние
        assertTrue("Должно быть хотя бы одно обновление состояния", gameStates.isNotEmpty())
        assertEquals("Начальное состояние игры должно быть Paused", GameState.Paused, gameStates.last())
    }
    
    @Test
    fun snakeShouldHaveInitialLengthOf3() {
        // Проверяем начальную длину змейки
        assertTrue("Должно быть хотя бы одно обновление UI", uiStates.isNotEmpty())
        val lastUiState = uiStates.last()
        assertEquals("Начальная длина змейки должна быть 3", 3, lastUiState.snakeParts.size)
    }
    
    @Test
    fun gameOverMethodShouldChangeStateToGameOver() {
        // Получаем метод gameOver через рефлексию
        val gameOverMethod = GameEngine::class.java.getDeclaredMethod("gameOver")
        gameOverMethod.isAccessible = true
        
        // Вызываем метод gameOver напрямую
        gameOverMethod.invoke(gameEngine)
        
        // Завершаем все отложенные задачи
        testDispatcher.scheduler.runCurrent()
        
        // Проверяем, что состояние игры изменилось на GameOver
        assertTrue("Состояние должно быть обновлено после gameOver", gameStates.isNotEmpty())
        assertEquals("Метод gameOver должен изменять состояние на GameOver",
            GameState.GameOver, gameStates.last())
    }
    
    @Test
    fun obstaclesShouldNotOverlapWithSnakeAtInitialization() {
        // Проверяем, что препятствия не пересекаются с начальной позицией змейки
        assertTrue("Должно быть хотя бы одно обновление UI", uiStates.isNotEmpty())
        val lastUiState = uiStates.last()
        val snakeParts = lastUiState.snakeParts
        val obstacles = lastUiState.obstacles
        
        assertTrue("Змейка должна состоять минимум из одной части", snakeParts.isNotEmpty())
        
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
    fun foodShouldNotOverlapWithSnakeOrObstacles() {
        // Получаем текущее состояние
        assertTrue("Должно быть хотя бы одно обновление UI", uiStates.isNotEmpty())
        val lastUiState = uiStates.last()
        
        val snakeParts = lastUiState.snakeParts
        val obstacles = lastUiState.obstacles
        val food = lastUiState.food
        
        assertTrue("Змейка должна состоять минимум из одной части", snakeParts.isNotEmpty())
        
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
    fun pauseAndResumeShouldWorkCorrectly() {
        // Запускаем игру
        gameEngine.startGame()
        testDispatcher.scheduler.runCurrent()
        
        assertTrue("Состояния должны обновляться после запуска игры", gameStates.size >= 2)
        assertEquals("После запуска игра должна быть в состоянии Running", GameState.Running, gameStates.last())
        
        // Ставим игру на паузу
        gameEngine.pauseGame()
        testDispatcher.scheduler.runCurrent()
        
        assertTrue("Состояния должны обновляться после паузы", gameStates.size >= 3)
        assertEquals("После паузы игра должна быть в состоянии Paused", GameState.Paused, gameStates.last())
        
        // Возобновляем игру
        gameEngine.resumeGame()
        testDispatcher.scheduler.runCurrent()
        
        assertTrue("Состояния должны обновляться после возобновления", gameStates.size >= 4)
        assertEquals("После возобновления игра должна быть в состоянии Running", GameState.Running, gameStates.last())
    }
    
    @Test
    fun resetGameShouldRestoreInitialState() {
        // Запоминаем начальное состояние UI
        val initialUiState = if (uiStates.isNotEmpty()) uiStates.last() else null
        assertTrue("Должно быть начальное состояние UI", initialUiState != null)
        
        // Запускаем игру
        gameEngine.startGame()
        testDispatcher.scheduler.runCurrent()
        
        // Сбрасываем игру
        gameEngine.resetGame()
        testDispatcher.scheduler.runCurrent()
        
        // Получаем обновленное состояние после сброса
        val resetUiState = uiStates.last()
        
        // Проверяем, что состояние игры сброшено к Paused
        assertEquals("После сброса игра должна быть в состоянии Paused", GameState.Paused, gameStates.last())
        
        // Проверяем, что счет сброшен к 0
        assertEquals("После сброса счет должен быть равен 0", 0, resetUiState.score)
        
        // Проверяем, что длина змейки вернулась к начальной (3)
        assertEquals("После сброса длина змейки должна быть равна 3", 3, resetUiState.snakeParts.size)
        
        // Проверяем, что скорость вернулась к начальной
        assertEquals("После сброса скорость должна быть равна 1.0f", 1.0f, resetUiState.speedFactor)
    }
    
    @Test
    fun changeDirectionShouldUpdateSnakeDirection() {
        // Запускаем игру
        gameEngine.startGame()
        testDispatcher.scheduler.runCurrent()
        
        // Меняем направление на DOWN
        gameEngine.changeDirection(Direction.DOWN)
        testDispatcher.scheduler.runCurrent()
        
        // Запускаем один цикл игры, чтобы направление применилось
        val updateGameMethod = GameEngine::class.java.getDeclaredMethod("updateGame")
        updateGameMethod.isAccessible = true
        updateGameMethod.invoke(gameEngine)
        testDispatcher.scheduler.runCurrent()
        
        // Проверяем изменение позиции головы
        val initialHead = uiStates[uiStates.size - 2].snakeParts[0]
        val newHead = uiStates.last().snakeParts[0]
        
        // Проверяем, что голова сдвинулась вниз (y увеличилось)
        // Но учитываем возможное заворачивание (если голова была на нижней границе)
        val boardSize = GameEngine::class.java.getDeclaredField("boardSize")
        boardSize.isAccessible = true
        val size = boardSize.get(gameEngine) as Int
        
        val expectedY = (initialHead.y + 1) % size
        assertEquals("Голова должна сдвинуться вниз при направлении DOWN", expectedY, newHead.y)
    }
}
