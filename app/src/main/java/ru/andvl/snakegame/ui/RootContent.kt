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
                ReplayScreen(
                    component = instance.component
                )
            }
            is RootComponent.Child.ReplayPlayer -> {
                ReplayPlayerScreen(
                    component = instance.component
                )
            }
        }
    }
} 
