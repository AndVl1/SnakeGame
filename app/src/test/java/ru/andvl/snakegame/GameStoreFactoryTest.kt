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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.andvl.snakegame.decompose.game.store.GameStoreFactory
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
} 
