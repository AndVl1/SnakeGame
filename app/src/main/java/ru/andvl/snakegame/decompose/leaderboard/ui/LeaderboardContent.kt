package ru.andvl.snakegame.decompose.leaderboard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.snakegame.decompose.leaderboard.LeaderboardComponent
import ru.andvl.snakegame.ui.LeaderboardScreen

/**
 * UI для экрана таблицы лидеров
 */
@Composable
fun LeaderboardContent(
    component: LeaderboardComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    
    LeaderboardScreen(
        scores = state.scores,
        onStartGameClick = { component.onStartGameClicked() },
        onSettingsClick = { component.onSettingsClicked() },
        modifier = modifier
    )
} 
