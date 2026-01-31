package ru.andvl.snakegame.decompose.statistics.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import ru.andvl.snakegame.data.GameStatistics
import ru.andvl.snakegame.data.StatisticsRepository

/**
 * Фабрика для создания Store статистики
 */
class StatisticsStoreFactory(
    private val storeFactory: StoreFactory,
    private val statisticsRepository: StatisticsRepository
) {

    fun create(): StatisticsStoreImpl =
        object : StatisticsStoreImpl, Store<StatisticsIntent, StatisticsState, StatisticsLabel> by storeFactory.create(
            name = "StatisticsStore",
            initialState = StatisticsState.Loading,
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Message {
        data class StatisticsLoaded(val statistics: GameStatistics) : Message()
        data class LoadError(val message: String) : Message()
        object StatisticsReset : Message()
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<StatisticsIntent, Nothing, StatisticsState, Message, StatisticsLabel>() {

        override fun executeIntent(intent: StatisticsIntent, getState: () -> StatisticsState) {
            when (intent) {
                is StatisticsIntent.LoadStatistics -> loadStatistics()
                is StatisticsIntent.ResetStatistics -> resetStatistics()
                is StatisticsIntent.NavigateBack -> publish(StatisticsLabel.NavigateBack)
            }
        }

        private fun loadStatistics() {
            scope.launch {
                try {
                    val statistics = statisticsRepository.statistics.firstOrNull()
                    if (statistics != null) {
                        dispatch(Message.StatisticsLoaded(statistics))
                    } else {
                        dispatch(Message.LoadError("Не удалось загрузить статистику"))
                    }
                } catch (e: Exception) {
                    dispatch(Message.LoadError(e.message ?: "Неизвестная ошибка"))
                }
            }
        }

        private fun resetStatistics() {
            scope.launch {
                try {
                    statisticsRepository.resetStatistics()
                    publish(StatisticsLabel.ShowMessage("Статистика сброшена"))
                    // Загружаем обновленную статистику
                    loadStatistics()
                } catch (e: Exception) {
                    publish(StatisticsLabel.ShowMessage("Ошибка сброса статистики: ${e.message}"))
                }
            }
        }
    }

    private object ReducerImpl : Reducer<StatisticsState, Message> {
        override fun StatisticsState.reduce(msg: Message): StatisticsState =
            when (msg) {
                is Message.StatisticsLoaded -> StatisticsState.Content(msg.statistics)
                is Message.LoadError -> StatisticsState.Error(msg.message)
                is Message.StatisticsReset -> StatisticsState.Loading
            }
    }

    interface StatisticsStoreImpl : Store<StatisticsIntent, StatisticsState, StatisticsLabel>
}
