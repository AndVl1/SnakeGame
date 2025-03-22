package ru.andvl.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import ru.andvl.sample.data.PlayerScore
import ru.andvl.sample.data.ScoreRepository
import ru.andvl.sample.data.SettingsRepository
import ru.andvl.sample.game.model.GameState
import ru.andvl.sample.game.ui.GameScreen
import ru.andvl.sample.ui.ConfirmExitDialog
import ru.andvl.sample.ui.LeaderboardScreen
import ru.andvl.sample.ui.SaveScoreDialog
import ru.andvl.sample.ui.SettingsScreen
import ru.andvl.sample.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private lateinit var scoreRepository: ScoreRepository
    private lateinit var settingsRepository: SettingsRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        scoreRepository = ScoreRepository(this)
        settingsRepository = SettingsRepository(this)
        
        enableEdgeToEdge()
        setContent {
            val settings by settingsRepository.settings.collectAsState(initial = null)
            
            // Применяем тему в зависимости от настроек
            MyApplicationTheme(
                darkTheme = settings?.isDarkTheme ?: false
            ) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SnakeGameApp(
                        scoreRepository = scoreRepository,
                        settingsRepository = settingsRepository,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun SnakeGameApp(
    scoreRepository: ScoreRepository,
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Состояния для управления приложением
    var currentScreen by remember { mutableStateOf(Screen.LEADERBOARD) }
    var showSaveScoreDialog by remember { mutableStateOf(false) }
    var showExitGameDialog by remember { mutableStateOf(false) }
    var gameScore by remember { mutableStateOf(0) }
    var gameSpeedFactor by remember { mutableStateOf(1.0f) }
    var currentGameState by remember { mutableStateOf(GameState.Paused) }
    
    // Сбрасываем состояние диалога сохранения при переходе между экранами
    if (showSaveScoreDialog && currentScreen != Screen.GAME) {
        showSaveScoreDialog = false
    }
    
    when (currentScreen) {
        Screen.LEADERBOARD -> {
            LeaderboardScreen(
                scoreRepository = scoreRepository,
                onStartGameClick = {
                    // Сбрасываем игровое состояние при начале новой игры
                    gameScore = 0
                    gameSpeedFactor = 1.0f
                    currentGameState = GameState.Paused
                    showSaveScoreDialog = false
                    showExitGameDialog = false
                    currentScreen = Screen.GAME
                },
                onSettingsClick = {
                    currentScreen = Screen.SETTINGS
                }
            )
            
            // На экране рейтинга мы не обрабатываем нажатие назад
        }
        
        Screen.GAME -> {
            // Обработка кнопки Назад на экране игры
            BackHandler {
                // Показываем диалог подтверждения выхода только если игра не завершена
                if (currentGameState != GameState.GameOver) {
                    showExitGameDialog = true
                } else {
                    // Если игра уже завершена, сразу возвращаемся к рейтингу
                    currentScreen = Screen.LEADERBOARD
                }
            }
            
            // Используем экран игры с MVVM архитектурой
            GameScreen(
                onGameOver = { score, speedFactor ->
                    gameScore = score
                    gameSpeedFactor = speedFactor
                    showSaveScoreDialog = true
                },
                onGameStateChanged = { newState ->
                    currentGameState = newState
                },
                resetGameState = currentScreen == Screen.GAME // Сброс состояния только когда активен экран игры
            )
            
            // Диалог подтверждения выхода из игры
            if (showExitGameDialog) {
                ConfirmExitDialog(
                    onConfirm = {
                        showExitGameDialog = false
                        currentScreen = Screen.LEADERBOARD
                    },
                    onDismiss = {
                        showExitGameDialog = false
                    }
                )
            }
        }
        
        Screen.SETTINGS -> {
            // Обработка кнопки Назад на экране настроек
            BackHandler {
                currentScreen = Screen.LEADERBOARD
            }
            
            SettingsScreen(
                settingsRepository = settingsRepository,
                onNavigateBack = {
                    currentScreen = Screen.LEADERBOARD
                }
            )
        }
    }
    
    // Диалог сохранения результата
    if (showSaveScoreDialog) {
        SaveScoreDialog(
            score = gameScore,
            speedFactor = gameSpeedFactor,
            onSaveScore = { playerName ->
                coroutineScope.launch {
                    scoreRepository.addScore(
                        PlayerScore(
                            playerName = playerName,
                            score = gameScore,
                            speedFactor = gameSpeedFactor
                        )
                    )
                    showSaveScoreDialog = false
                    currentScreen = Screen.LEADERBOARD
                }
            },
            onDismiss = {
                showSaveScoreDialog = false
                currentScreen = Screen.LEADERBOARD
            }
        )
    }
}

enum class Screen {
    LEADERBOARD, GAME, SETTINGS
}
