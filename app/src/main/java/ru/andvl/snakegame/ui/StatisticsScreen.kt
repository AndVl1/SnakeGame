package ru.andvl.snakegame.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.snakegame.decompose.statistics.StatisticsComponent
import ru.andvl.snakegame.decompose.statistics.store.StatisticsState
import ru.andvl.snakegame.decompose.statistics.ui.StatisticsContent

/**
 * Экран статистики игры
 */
@Composable
fun StatisticsScreen(
    component: StatisticsComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()

    Box(modifier = modifier.fillMaxSize()) {
        when (val currentState = state) {
            is StatisticsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is StatisticsState.Content -> {
                StatisticsContent(
                    statistics = currentState.statistics,
                    onResetClick = component::onResetStatistics
                )
            }
            is StatisticsState.Error -> {
                Text(
                    text = currentState.message,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
