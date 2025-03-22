package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.andvl.sample.game.model.GameState
import ru.andvl.sample.game.model.GameUiEvent
import ru.andvl.sample.game.viewmodel.GameViewModel

/**
 * Главный экран игры с организацией MVVM
 */
@Composable
fun GameScreen(
    onGameOver: (score: Int, speedFactor: Float) -> Unit,
    onGameStateChanged: (GameState) -> Unit = {},
    resetGameState: Boolean = true,
    modifier: Modifier = Modifier,
    viewModel: GameViewModel = viewModel()
) {
    // Получаем стейт из ViewModel
    val gameUiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Дополнительное состояние, чтобы отслеживать завершение анимации смерти
    var gameOverProcessed by remember { mutableStateOf(false) }
    
    // Сбрасываем состояние игры при входе на экран, если требуется
    LaunchedEffect(resetGameState) {
        if (resetGameState) {
            viewModel.handleUiEvent(GameUiEvent.RestartGame)
            gameOverProcessed = false
        }
    }
    
    // Обработка завершения игры
    LaunchedEffect(gameUiState.gameState, gameUiState.deathAnimationActive) {
        // Проверяем, что игра закончилась И анимация смерти была активна, но теперь закончилась
        if (gameUiState.gameState == GameState.GameOver && 
            !gameUiState.deathAnimationActive && 
            !gameOverProcessed) {
            gameOverProcessed = true
            onGameOver(gameUiState.score, gameUiState.speedFactor)
        }
        
        // Уведомляем родительский компонент об изменении состояния игры
        onGameStateChanged(gameUiState.gameState)
    }
    
    // Сбрасываем флаг обработки, если игра запускается заново
    LaunchedEffect(gameUiState.gameState) {
        if (gameUiState.gameState == GameState.Running) {
            gameOverProcessed = false
        }
    }
    
    // Показываем диалог с инструкциями, если нужно
    if (gameUiState.showInstructions) {
        InstructionsDialog(
            onDismiss = { viewModel.handleUiEvent(GameUiEvent.DismissInstructions) }
        )
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Змейка+",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // Информационная панель
        GameInfoPanel(
            score = gameUiState.score,
            speedFactor = gameUiState.speedFactor,
            doubleScoreActive = gameUiState.doubleScoreActive,
            pulsatingSpeedActive = gameUiState.pulsatingSpeedActive,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Описание про проход через стены
        Text(
            text = "Проходите через стены!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // Легенда типов еды
        FoodLegend(modifier = Modifier.fillMaxWidth())
        
        // Игровое поле
        GameBoard(
            snakeParts = gameUiState.snakeParts,
            food = gameUiState.food,
            obstacles = gameUiState.obstacles,
            isGameOver = gameUiState.deathAnimationActive,
            doubleScoreActive = gameUiState.doubleScoreActive,
            pulsatingSpeedActive = gameUiState.pulsatingSpeedActive,
            onDirectionChange = { direction ->
                viewModel.handleUiEvent(GameUiEvent.ChangeDirection(direction))
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Элементы управления направлением
        DirectionControls(
            onDirectionChange = { direction ->
                viewModel.handleUiEvent(GameUiEvent.ChangeDirection(direction))
            }
        )
        
        // Кнопки действий
        GameActionControls(
            gameState = gameUiState.gameState,
            onPlayPauseClick = {
                if (gameUiState.gameState == GameState.Running) {
                    viewModel.handleUiEvent(GameUiEvent.PauseGame)
                } else {
                    viewModel.handleUiEvent(GameUiEvent.ResumeGame)
                }
            },
            onRestartClick = {
                viewModel.handleUiEvent(GameUiEvent.RestartGame)
            },
            onShowInstructionsClick = {
                viewModel.handleUiEvent(GameUiEvent.ShowInstructions)
            }
        )
    }
} 