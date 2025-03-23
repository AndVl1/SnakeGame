package ru.andvl.sample.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import ru.andvl.sample.decompose.RootComponent
import ru.andvl.sample.decompose.game.ui.GameContent
import ru.andvl.sample.decompose.leaderboard.ui.LeaderboardContent
import ru.andvl.sample.decompose.settings.ui.SettingsContent

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
        }
    }
} 