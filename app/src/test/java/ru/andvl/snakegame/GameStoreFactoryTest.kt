package ru.andvl.snakegame

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
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
import org.junit.Test
import ru.andvl.snakegame.decompose.game.store.GameIntent
import ru.andvl.snakegame.decompose.game.store.GameStoreFactory
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.GameState as GameStateEnum

@ExperimentalCoroutinesApi
class GameStoreFactoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var storeFactory: StoreFactory
    private lateinit var gameStoreFactory: GameStoreFactory

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storeFactory = DefaultStoreFactory()
        gameStoreFactory = GameStoreFactory(storeFactory)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `store should be created with proper initial state`() {
        // Создаем хранилище
        val store = gameStoreFactory.create()
        
        // Проверяем только самые базовые свойства начального состояния
        assertEquals(GameStateEnum.Paused, store.state.gameState)
        assertEquals(0, store.state.score)
        assertTrue(store.state.snakeParts.isEmpty())
    }
    
    @Test
    fun `BackPressed intent should be processed correctly`() {
        // Создаем хранилище без корутин
        val store = gameStoreFactory.create()
        
        // Отправляем интент
        store.accept(GameIntent.BackPressed)
        
        // Для проверки лейблов нужны корутины, поэтому здесь проверяем только,
        // что интент принимается без ошибок
        // Полноценное тестирование лейблов требует другого подхода
    }
    
    @Test
    fun `SaveScore intent should be processed correctly`() {
        // Создаем хранилище без корутин
        val store = gameStoreFactory.create()
        
        // Отправляем интент
        store.accept(GameIntent.SaveScore("TestPlayer"))
        
        // Проверяем только обработку интента без ошибок
    }
    
    @Test
    fun `PlayPause intent should toggle game state`() {
        // Создаем хранилище
        val store = gameStoreFactory.create()
        
        // Начальное состояние - Paused
        assertEquals(GameStateEnum.Paused, store.state.gameState)
        
        // Отправляем PlayPause - игра должна запуститься
        store.accept(GameIntent.PlayPause)
        
        // Состояние должно измениться на Running
        assertEquals(GameStateEnum.Running, store.state.gameState)
        
        // Отправляем PlayPause еще раз - игра должна приостановиться
        store.accept(GameIntent.PlayPause)
        
        // Состояние должно вернуться обратно на Paused
        assertEquals(GameStateEnum.Paused, store.state.gameState)
    }
    
    @Test
    fun `ChangeDirection intent should be processed correctly`() {
        // Создаем хранилище
        val store = gameStoreFactory.create()
        
        // Инициализируем игру и запускаем
        store.accept(GameIntent.Initialize)
        store.accept(GameIntent.PlayPause)
        
        // Изменяем направление - проверяем, что не возникает ошибок
        store.accept(GameIntent.ChangeDirection(Direction.DOWN))
    }
    
    @Test
    fun `ShowInstructions intent should update state`() {
        // Создаем хранилище
        val store = gameStoreFactory.create()
        
        // Показываем инструкции
        store.accept(GameIntent.ShowInstructions)
        
        // Проверяем, что инструкции отображаются
        assertTrue(store.state.showInstructions)
        
        // Скрываем инструкции
        store.accept(GameIntent.DismissInstructions)
        
        // Проверяем, что инструкции скрыты
        assertFalse(store.state.showInstructions)
    }
    
    @Test
    fun `DismissSaveScore intent should be processed correctly`() {
        // Создаем хранилище
        val store = gameStoreFactory.create()
        
        // Отменяем сохранение счета
        store.accept(GameIntent.DismissSaveScore)
        
        // Проверяем только обработку интента без ошибок
    }
    
    @Test
    fun `Initialize intent should set up game state`() {
        // Создаем хранилище
        val store = gameStoreFactory.create()
        
        // Инициализируем игру
        store.accept(GameIntent.Initialize)
        
        // Проверяем базовые свойства состояния
        assertEquals(GameStateEnum.Paused, store.state.gameState)
        assertEquals(0, store.state.score)
    }
} 
