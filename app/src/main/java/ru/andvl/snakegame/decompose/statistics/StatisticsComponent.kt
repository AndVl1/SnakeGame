package ru.andvl.snakegame.decompose.statistics

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.Value
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
import ru.andvl.snakegame.data.StatisticsRepository
import ru.andvl.snakegame.decompose.statistics.store.StatisticsIntent
import ru.andvl.snakegame.decompose.statistics.store.StatisticsLabel
import ru.andvl.snakegame.decompose.statistics.store.StatisticsState
import ru.andvl.snakegame.decompose.statistics.store.StatisticsStoreFactory
import ru.andvl.snakegame.extensions.asValue

/**
 * Компонент для экрана статистики с использованием MVIKotlin
 */
class StatisticsComponent(
    private val componentContext: ComponentContext,
    private val statisticsRepository: StatisticsRepository,
    private val storeFactory: StoreFactory,
    private val onNavigateBack: () -> Unit
) : ComponentContext by componentContext {

    // Используем SupervisorJob для устойчивости к ошибкам в корутинах
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Создаем и сохраняем store в instanceKeeper
    private val store = instanceKeeper.getStore {
        StatisticsStoreFactory(
            storeFactory = storeFactory,
            statisticsRepository = statisticsRepository
        ).create()
    }

    // Экспонируем state как Value для Decompose с привязкой к lifecycle
    val state: Value<StatisticsState> = store.asValue(lifecycle)

    init {
        // Подписываемся на лейблы из хранилища
        val labelsJob = store.labels
            .onEach { label ->
                when (label) {
                    is StatisticsLabel.ShowMessage -> {
                        // TODO: Показать сообщение пользователю (Snackbar)
                    }
                    StatisticsLabel.NavigateBack -> {
                        onNavigateBack.invoke()
                    }
                }
            }
            .launchIn(scope)

        // Отменяем корутины при уничтожении компонента
        lifecycle.doOnDestroy {
            labelsJob.cancel()
            scope.cancel()
        }

        // Инициализируем загрузку данных при первом запуске
        store.accept(StatisticsIntent.LoadStatistics)
    }

    // Публичные методы для UI

    fun onResetStatistics() {
        store.accept(StatisticsIntent.ResetStatistics)
    }

    fun onBackClick() {
        store.accept(StatisticsIntent.NavigateBack)
    }
}
