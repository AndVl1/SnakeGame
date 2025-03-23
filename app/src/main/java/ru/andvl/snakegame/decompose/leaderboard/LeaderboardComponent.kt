package ru.andvl.snakegame.decompose.leaderboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.andvl.snakegame.data.ScoreRepository
import ru.andvl.snakegame.decompose.leaderboard.store.LeaderboardIntent
import ru.andvl.snakegame.decompose.leaderboard.store.LeaderboardLabel
import ru.andvl.snakegame.decompose.leaderboard.store.LeaderboardState
import ru.andvl.snakegame.decompose.leaderboard.store.LeaderboardStoreFactory
import ru.andvl.snakegame.extensions.asValue

/**
 * Компонент для экрана таблицы лидеров с использованием MVIKotlin
 */
class LeaderboardComponent(
    private val componentContext: ComponentContext,
    private val scoreRepository: ScoreRepository,
    private val storeFactory: StoreFactory,
    private val onStartGameClick: () -> Unit,
    private val onSettingsClick: () -> Unit
) : ComponentContext by componentContext {

    // Используем SupervisorJob для устойчивости к ошибкам в корутинах
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    
    // Создаем и сохраняем store в instanceKeeper
    private val store = instanceKeeper.getStore {
        LeaderboardStoreFactory(
            storeFactory = storeFactory,
            scoreRepository = scoreRepository
        ).create()
    }
    
    // Экспонируем state как Value для Decompose с привязкой к lifecycle
    val state: Value<LeaderboardState> = store.asValue(lifecycle)
    
    init {
        // Подписываемся на лейблы из хранилища
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is LeaderboardLabel.ShowMessage -> {
                        // TODO: Показать сообщение пользователю
                    }
                    // Убираем обработку навигационных лейблов, так как теперь
                    // навигация будет происходить напрямую из UI методов
                    else -> { /* Игнорируем остальные лейблы */ }
                }
            }
            .launchIn(scope)
            
        // Отменяем корутины при уничтожении компонента
        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }
            
        // Инициализируем загрузку данных при первом запуске
        store.accept(LeaderboardIntent.LoadScores)
        
        // Подписываемся на жизненный цикл, чтобы обновлять данные при RESUMED
        lifecycle.subscribe(object : Lifecycle.Callbacks {
            override fun onResume() {
                // Загружаем актуальные данные при каждом возобновлении компонента
                store.accept(LeaderboardIntent.LoadScores)
            }
        })
    }
    
    // Публичные методы для UI
    
    fun onStartGameClick() {
        store.accept(LeaderboardIntent.StartGame)
        onStartGameClick.invoke()
    }
    
    fun onSettingsClick() {
        store.accept(LeaderboardIntent.OpenSettings)
        onSettingsClick.invoke()
    }
    
    fun onBackPressed() {
        store.accept(LeaderboardIntent.BackPressed)
    }
    
    // Публичный метод для явного обновления данных лидерборда, который можно вызвать извне
    fun refreshLeaderboard() {
        store.accept(LeaderboardIntent.LoadScores)
    }
} 
