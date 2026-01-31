package ru.andvl.snakegame.decompose.statistics.store

import ru.andvl.snakegame.data.GameStatistics

/**
 * Intent - намерения пользователя
 */
sealed class StatisticsIntent {
    object LoadStatistics : StatisticsIntent()
    object ResetStatistics : StatisticsIntent()
    object NavigateBack : StatisticsIntent()
}

/**
 * State - состояние экрана статистики
 */
sealed class StatisticsState {
    object Loading : StatisticsState()
    data class Content(val statistics: GameStatistics) : StatisticsState()
    data class Error(val message: String) : StatisticsState()
}

/**
 * Label - одноразовые события (навигация, сообщения)
 */
sealed class StatisticsLabel {
    object NavigateBack : StatisticsLabel()
    data class ShowMessage(val message: String) : StatisticsLabel()
}
