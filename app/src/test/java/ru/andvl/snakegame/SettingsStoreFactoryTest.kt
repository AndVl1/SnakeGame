package ru.andvl.snakegame

import android.content.Context
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import ru.andvl.snakegame.data.SettingsRepository
import ru.andvl.snakegame.decompose.settings.store.SettingsIntent
import ru.andvl.snakegame.decompose.settings.store.SettingsStoreFactory
import ru.andvl.snakegame.game.model.GameSettings

@ExperimentalCoroutinesApi
class SettingsStoreFactoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var storeFactory: StoreFactory
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var context: Context
    private lateinit var settingsStoreFactory: SettingsStoreFactory
    private val settingsFlow = MutableStateFlow(GameSettings())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        storeFactory = DefaultStoreFactory()
        settingsRepository = mockk(relaxed = true) {
            coEvery { settings } returns settingsFlow
        }
        context = mockk(relaxed = true)
        settingsStoreFactory = SettingsStoreFactory(storeFactory, settingsRepository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `initial state should be correct`() {
        // Создаем хранилище
        val store = settingsStoreFactory.create()
        
        // Проверяем начальное состояние
        assertFalse(store.state.isLoading)
        assertFalse(store.state.isDarkTheme)
        assertEquals("", store.state.appLocale)
        assertEquals(null, store.state.error)
    }
    
    @Test
    fun `LoadSettings intent should be processed correctly`() {
        // Создаем хранилище
        val store = settingsStoreFactory.create()
        
        // Загружаем настройки
        store.accept(SettingsIntent.LoadSettings)
        
        // Проверяем только обработку интента без ошибок
    }
    
    @Test
    fun `ToggleTheme intent should be processed correctly`() {
        // Создаем хранилище
        val store = settingsStoreFactory.create()
        
        // Переключаем тему
        store.accept(SettingsIntent.ToggleTheme)
        
        // Проверяем только обработку интента без ошибок
    }
    
    @Test
    fun `SelectLocale intent should be processed correctly`() {
        // Создаем хранилище
        val store = settingsStoreFactory.create()
        
        // Выбираем язык
        val newLocale = "ru"
        store.accept(SettingsIntent.SelectLocale(newLocale))
        
        // Проверяем только обработку интента без ошибок
    }
    
    @Test
    fun `ClickBack intent should be processed correctly`() {
        // Создаем хранилище
        val store = settingsStoreFactory.create()
        
        // Кликаем на кнопку "назад"
        store.accept(SettingsIntent.ClickBack)
        
        // Проверяем только обработку интента без ошибок
    }
} 