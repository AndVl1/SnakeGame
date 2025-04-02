package ru.andvl.snakegame

import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.mockk.mockk
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
import ru.andvl.snakegame.data.ScoreRepository
import ru.andvl.snakegame.decompose.leaderboard.store.LeaderboardIntent
import ru.andvl.snakegame.decompose.leaderboard.store.LeaderboardStoreFactory

@ExperimentalCoroutinesApi
class LeaderboardStoreFactoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var storeFactory: StoreFactory
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var leaderboardStoreFactory: LeaderboardStoreFactory

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storeFactory = DefaultStoreFactory()
        scoreRepository = mockk(relaxed = true)
        leaderboardStoreFactory = LeaderboardStoreFactory(storeFactory, scoreRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be correct`() {
        // Создаем хранилище
        val store = leaderboardStoreFactory.create()
        
        // Проверяем начальное состояние
        assertFalse(store.state.isLoading)
        assertTrue(store.state.topScores.isEmpty())
        assertEquals(null, store.state.error)
    }
    
    @Test
    fun `LoadScores intent should be processed correctly`() {
        // Создаем хранилище
        val store = leaderboardStoreFactory.create()
        
        // Загружаем счета
        store.accept(LeaderboardIntent.LoadScores)
        
        // Проверяем только, что интент принимается без ошибок
    }
    
    @Test
    fun `StartGame intent should be processed correctly`() {
        // Создаем хранилище
        val store = leaderboardStoreFactory.create()
        
        // Отправляем intent для навигации
        store.accept(LeaderboardIntent.StartGame)
        
        // Проверяем только, что интент принимается без ошибок
    }
    
    @Test
    fun `OpenSettings intent should be processed correctly`() {
        // Создаем хранилище
        val store = leaderboardStoreFactory.create()
        
        // Отправляем intent для навигации
        store.accept(LeaderboardIntent.OpenSettings)
        
        // Проверяем только, что интент принимается без ошибок
    }
} 
