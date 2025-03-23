package ru.andvl.snakegame.decompose

import android.content.Context
import android.os.Parcelable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.mvikotlin.core.store.StoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import ru.andvl.snakegame.data.PlayerScore
import ru.andvl.snakegame.data.ScoreRepository
import ru.andvl.snakegame.data.SettingsRepository
import ru.andvl.snakegame.decompose.game.GameComponent
import ru.andvl.snakegame.decompose.leaderboard.LeaderboardComponent
import ru.andvl.snakegame.decompose.settings.SettingsComponent
import ru.andvl.snakegame.game.model.GameSettings

/**
 * Корневой компонент, который управляет навигацией между экранами
 */
class RootComponent(
    componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val context: Context
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()
    private val scoreRepository = ScoreRepository(context)
    private val settingsRepository = SettingsRepository(context)
    private val scope = CoroutineScope(Dispatchers.Main)
    
    // Состояние настроек для применения темы
    val settingsState: Flow<GameSettings?> = settingsRepository.settings

    /**
     * Стек навигации с текущим активным экраном
     */
    val childStack = childStack(
        source = navigation,
        initialConfiguration = Config.Leaderboard,
        handleBackButton = true,
        childFactory = ::createChild
    )

    /**
     * Конфигурации для экранов
     */
    @Parcelize
    sealed class Config : Parcelable {
        object Leaderboard : Config()
        object Game : Config()
        object Settings : Config()
    }

    /**
     * Типы дочерних экранов
     */
    sealed class Child {
        data class Leaderboard(val component: LeaderboardComponent) : Child()
        data class Game(val component: GameComponent) : Child()
        data class Settings(val component: SettingsComponent) : Child()
    }

    private fun createChild(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Leaderboard -> Child.Leaderboard(
                component = LeaderboardComponent(
                    componentContext = componentContext,
                    scoreRepository = scoreRepository,
                    storeFactory = storeFactory,
                    onStartGameClick = { navigation.push(Config.Game) },
                    onSettingsClick = { navigation.push(Config.Settings) }
                )
            )
            is Config.Game -> Child.Game(
                component = GameComponent(
                    componentContext = componentContext,
                    storeFactory = storeFactory,
                    onNavigateToLeaderboard = { score, speedFactor, playerName ->
                        if (playerName != null) {
                            saveScore(playerName, score, speedFactor)
                        }
                        
                        // Переходим на экран лидерборда
                        navigation.navigate { stack -> listOf(Config.Leaderboard) }
                        
                        // Находим активный компонент лидерборда и обновляем его данные
                        scope.launch {
                            // Даем время на переход на экран лидерборда
                            kotlinx.coroutines.delay(100)
                            
                            // Получаем текущий активный экран
                            val currentChild = childStack.value.active.instance
                            if (currentChild is Child.Leaderboard) {
                                // Обновляем данные лидерборда
                                currentChild.component.refreshLeaderboard()
                            }
                        }
                    },
                    onBack = { navigation.pop() }
                )
            )
            is Config.Settings -> Child.Settings(
                component = SettingsComponent(
                    componentContext = componentContext,
                    settingsRepository = settingsRepository,
                    storeFactory = storeFactory,
                    onNavigateBack = { navigation.pop() },
                    context = context
                )
            )
        }

    private fun saveScore(playerName: String, score: Int, speedFactor: Float) {
        scope.launch {
            scoreRepository.addScore(
                PlayerScore(
                    playerName = playerName,
                    score = score,
                    speedFactor = speedFactor
                )
            )
        }
    }
} 
