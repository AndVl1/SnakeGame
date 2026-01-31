package ru.andvl.snakegame.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.snakegame.decompose.RootComponent
import ru.andvl.snakegame.decompose.game.ui.GameContent
import ru.andvl.snakegame.decompose.leaderboard.ui.LeaderboardContent
import ru.andvl.snakegame.decompose.settings.ui.SettingsContent

/**
 * UI для корневого компонента, который отображает текущий экран
 */
@Composable
fun RootContent(
    component: RootComponent,
    modifier: Modifier = Modifier
) {
    // Анимированное переключение между экранами
    Children(
        stack = component.childStack,
        modifier = modifier,
        animation = stackAnimation(fade() + scale()),
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Leaderboard -> LeaderboardContent(
                component = instance.component
            )
            is RootComponent.Child.Game -> GameContent(
                component = instance.component
            )
            is RootComponent.Child.Settings -> SettingsContent(
                component = instance.component
            )
            is RootComponent.Child.Achievements -> {
                val state by instance.component.state.subscribeAsState()
                AchievementsScreen(
                    achievements = state.achievements,
                    isLoading = state.isLoading,
                    onBackClick = instance.component::onBackClick
                )
            }
            is RootComponent.Child.Statistics -> {
                StatisticsScreen(
                    component = instance.component
                )
            }
            is RootComponent.Child.Replays -> {
                val state by instance.component.state.subscribeAsState()
                ReplayScreen(
                    replays = state.replays,
                    isLoading = state.isLoading,
                    onWatchClick = instance.component::onWatchReplayClick,
                    onDeleteClick = instance.component::onDeleteReplayClick,
                    onBackClick = instance.component::onBackClick
                )
            }
            is RootComponent.Child.ReplayPlayer -> {
                val state by instance.component.state.subscribeAsState()
                ReplayPlayerScreen(
                    gameUiState = state.gameUiState,
                    isPlaying = state.isPlaying,
                    isPaused = state.isPaused,
                    playbackSpeed = state.playbackSpeed,
                    currentProgress = state.currentProgress,
                    isLoading = state.isLoading,
                    onPlayClick = instance.component::onPlayClick,
                    onPauseClick = instance.component::onPauseClick,
                    onResumeClick = instance.component::onResumeClick,
                    onSpeedChange = instance.component::onPlaybackSpeedChange,
                    onBackClick = instance.component::onBackClick
                )
            }
        }
    }
} 
