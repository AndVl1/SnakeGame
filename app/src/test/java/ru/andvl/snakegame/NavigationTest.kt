package ru.andvl.snakegame

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackHandler
import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.arkivanov.essenty.instancekeeper.InstanceKeeperDispatcher
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import ru.andvl.snakegame.data.ScoreRepository
import ru.andvl.snakegame.decompose.leaderboard.LeaderboardComponent

@ExperimentalCoroutinesApi
class NavigationTest {
    
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var storeFactory: StoreFactory
    private lateinit var componentContext: TestComponentContext
    private lateinit var lifecycle: LifecycleRegistry
    private lateinit var instanceKeeper: InstanceKeeperDispatcher
    
    // Функции для навигации в тестах
    private var gameNavigationCounter = 0
    private var settingsNavigationCounter = 0
    
    private val onStartGameClick: () -> Unit = { gameNavigationCounter++ }
    private val onSettingsClick: () -> Unit = { settingsNavigationCounter++ }
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Сбрасываем счетчики перед каждым тестом
        gameNavigationCounter = 0
        settingsNavigationCounter = 0
        
        // Создаем моки необходимых зависимостей
        scoreRepository = mockk(relaxed = true)
        storeFactory = DefaultStoreFactory()
        
        // Создаем жизненный цикл и InstanceKeeper
        lifecycle = LifecycleRegistry()
        instanceKeeper = InstanceKeeperDispatcher()
        
        // Создаем контекст компонента с жизненным циклом
        componentContext = TestComponentContext(lifecycle, instanceKeeper)
        
        // Инициализируем жизненный цикл
        lifecycle.onCreate()
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test navigation works through MVI labels`() = runTest {
        // Создаем компонент с реальными колбеками
        val component = LeaderboardComponent(
            componentContext = componentContext,
            scoreRepository = scoreRepository,
            storeFactory = storeFactory,
            onStartGameClick = onStartGameClick,
            onSettingsClick = onSettingsClick
        )
        
        // Запускаем обработку корутин, чтобы обработать init-блок компонента
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Вызываем метод, который должен отправить Intent в Store
        component.onStartGameClick()
        
        // Запускаем обработку корутин, чтобы обработать Intent и опубликовать Label
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Проверяем, что колбек был вызван ровно один раз через поток лейблов
        assert(gameNavigationCounter == 1) { "Game navigation counter should be 1, but was $gameNavigationCounter" }
        
        // Вызываем метод для навигации в настройки
        component.onSettingsClick()
        
        // Запускаем обработку корутин
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Проверяем, что колбек был вызван ровно один раз через поток лейблов
        assert(settingsNavigationCounter == 1) { "Settings navigation counter should be 1, but was $settingsNavigationCounter" }
    }
    
    @Test
    fun `test multiple navigation calls are properly handled through MVI`() = runTest {
        // Создаем компонент с реальными колбеками
        val component = LeaderboardComponent(
            componentContext = componentContext,
            scoreRepository = scoreRepository,
            storeFactory = storeFactory,
            onStartGameClick = onStartGameClick,
            onSettingsClick = onSettingsClick
        )
        
        // Запускаем обработку корутин для инициализации
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Вызываем метод для навигации несколько раз
        component.onStartGameClick()
        testDispatcher.scheduler.advanceUntilIdle()
        
        component.onStartGameClick()
        testDispatcher.scheduler.advanceUntilIdle()
        
        component.onStartGameClick()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Проверяем, что колбек был вызван ровно три раза через поток лейблов
        assert(gameNavigationCounter == 3) { "Game navigation counter should be 3, but was $gameNavigationCounter" }
    }
    
    // Тестовая реализация ComponentContext для тестов
    private class TestComponentContext(
        override val lifecycle: Lifecycle,
        override val instanceKeeper: InstanceKeeper
    ) : ComponentContext {
        override val stateKeeper: StateKeeper = mockk(relaxed = true)
        override val backHandler: BackHandler = mockk(relaxed = true)
    }
} 
