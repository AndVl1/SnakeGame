package ru.andvl.sample.decompose.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.sample.R
import ru.andvl.sample.decompose.game.GameComponent
import ru.andvl.sample.game.Obstacle
import ru.andvl.sample.game.model.GameModelConverter
import ru.andvl.sample.game.model.GameState
import ru.andvl.sample.game.ui.FoodLegend
import ru.andvl.sample.game.ui.GameActionControls
import ru.andvl.sample.game.ui.GameBoard
import ru.andvl.sample.game.ui.GameDirectionControls
import ru.andvl.sample.game.ui.GameInstructionsDialog
import ru.andvl.sample.game.ui.InfoPanel
import ru.andvl.sample.ui.SaveScoreDialog

/**
 * Основной контент игрового экрана для Decompose
 */
@Composable
fun GameContent(
    component: GameComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.subscribeAsState()
    val showSaveScoreDialog by component.showSaveScoreDialog.subscribeAsState()
    
    // Состояние для диалога подтверждения выхода
    var showExitConfirmationDialog by remember { mutableStateOf(false) }
    
    // Перехватчик кнопки назад
    BackHandler(enabled = true) {
        if (state.gameState == GameState.Running) {
            // Пауза игры и показать диалог
            component.onPlayPauseClick()
            showExitConfirmationDialog = true
        } else {
            // Выход
            component.onBackPressed()
        }
    }
    
    // Проверка на конец игры
    LaunchedEffect(state.gameState, state.deathAnimationActive) {
        if (state.gameState == GameState.GameOver && !state.deathAnimationActive && !showSaveScoreDialog) {
            component.handleGameOver()
        }
    }
    
    // Показываем диалог с инструкциями при необходимости
    if (state.showInstructions) {
        GameInstructionsDialog(onDismiss = component::onDismissInstructions)
    }
    
    // Основной контент экрана
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Заголовок игры
        Text(
            text = stringResource(R.string.game_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )
        
        // Информационная панель (счет и скорость)
        InfoPanel(
            score = state.score,
            speedFactor = state.speedFactor,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Легенда типов еды
        FoodLegend(modifier = Modifier.fillMaxWidth())
        
        // Игровое поле
        val displayFood = GameModelConverter.convertFoodToDisplayGameFood(state.food)
        
        GameBoard(
            snakeParts = GameModelConverter.convertGridPositionsToSnakeParts(state.snakeParts),
            food = displayFood,
            obstacles = state.obstacles.map { Obstacle(it.position.x, it.position.y) },
            isGameOver = state.deathAnimationActive,
            doubleScoreActive = state.doubleScoreActive,
            pulsatingSpeedActive = state.pulsatingSpeedActive,
            onDirectionChange = { 
                component.onDirectionChange(GameModelConverter.convertDirection(it))
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Небольшой отступ между полем и элементами управления
        Spacer(modifier = Modifier.height(4.dp))
        
        // Элементы управления направлением
        GameDirectionControls(
            onDirectionChange = { 
                component.onDirectionChange(GameModelConverter.convertDirection(it))
            },
            modifier = Modifier.padding(vertical = 4.dp)
        )
        
        // Небольшой отступ между элементами управления и кнопками действий
        Spacer(modifier = Modifier.height(4.dp))
        
        // Кнопки действий
        GameActionControls(
            gameState = state.gameState,
            onPlayPauseClick = component::onPlayPauseClick,
            onRestartClick = component::onRestartClick,
            onShowInstructionsClick = component::onShowInstructionsClick,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
    
    // Диалог сохранения счета
    if (showSaveScoreDialog) {
        SaveScoreDialog(
            score = state.score,
            speedFactor = state.speedFactor,
            onSaveScore = component::onSaveScore,
            onDismiss = component::onDismissSaveScore
        )
    }
    
    // Диалог подтверждения выхода
    if (showExitConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { 
                showExitConfirmationDialog = false 
                // Возобновляем игру если отменили
                if (state.gameState == GameState.Paused) {
                    component.onPlayPauseClick()
                }
            },
            title = { Text(stringResource(R.string.exit_confirmation_title)) },
            text = { Text(stringResource(R.string.exit_confirmation_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitConfirmationDialog = false
                    component.onBackPressed()
                }) {
                    Text(stringResource(R.string.exit))
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showExitConfirmationDialog = false 
                    // Возобновляем игру если отменили
                    if (state.gameState == GameState.Paused) {
                        component.onPlayPauseClick()
                    }
                }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
} 
