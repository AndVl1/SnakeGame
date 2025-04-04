package ru.andvl.snakegame.decompose.game.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import ru.andvl.snakegame.R
import ru.andvl.snakegame.decompose.game.GameComponent
import ru.andvl.snakegame.game.Obstacle
import ru.andvl.snakegame.game.model.GameModelConverter
import ru.andvl.snakegame.game.model.GameState
import ru.andvl.snakegame.game.ui.FoodLegend
import ru.andvl.snakegame.game.ui.GameActionControls
import ru.andvl.snakegame.game.ui.GameBoard
import ru.andvl.snakegame.game.ui.GameDirectionControls
import ru.andvl.snakegame.game.ui.GameInstructionsDialog
import ru.andvl.snakegame.game.ui.InfoPanel
import ru.andvl.snakegame.ui.SaveScoreDialog

/**
 * Основной контент игрового экрана для Decompose
 */
@Composable
fun GameContent(
    component: GameComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.subscribeAsState()
    val showSaveScoreDialog by component.showSaveScoreDialog.subscribeAsState()
    var showExitConfirmationDialog by remember { mutableStateOf(false) }

    // Получаем размеры экрана
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    // Вычисляем оптимальный размер для игрового поля
    val maxBoardSize = minOf(screenWidth - 32.dp, screenHeight * 0.65f)

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

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок игры
            Text(
                text = stringResource(R.string.game_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )

            // Информационная панель и легенда

            InfoPanel(
                score = state.score,
                speedFactor = state.speedFactor,
                modifier = Modifier.fillMaxWidth()
            )

            FoodLegend(
                modifier = Modifier.fillMaxWidth()
            )

            // Игровое поле
            val displayFood =
                state.food?.let { GameModelConverter.convertFoodToDisplayGameFood(it) }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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
                    modifier = Modifier
                        .widthIn(max = maxBoardSize)
                        .aspectRatio(1f)
                )
            }

            // Элементы управления
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GameDirectionControls(
                    onDirectionChange = {
                        component.onDirectionChange(GameModelConverter.convertDirection(it))
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                GameActionControls(
                    gameState = state.gameState,
                    onPlayPauseClick = component::onPlayPauseClick,
                    onRestartClick = component::onRestartClick,
                    onShowInstructionsClick = component::onShowInstructionsClick
                )
            }
        }
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
