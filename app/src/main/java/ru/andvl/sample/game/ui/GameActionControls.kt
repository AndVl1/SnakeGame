package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.andvl.sample.R
import ru.andvl.sample.game.model.GameState

/**
 * Панель с кнопками управления игрой
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameActionControls(
    gameState: GameState,
    onPlayPauseClick: () -> Unit,
    onRestartClick: () -> Unit,
    onShowInstructionsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Вспомогательная кнопка инструкций (только иконка)
        GameIconButton(
            icon = Icons.Default.Info,
            contentDescription = stringResource(R.string.instructions),
            onClick = onShowInstructionsClick,
            modifier = Modifier
        )
        
        // Основная кнопка игры/паузы (с текстом)
        ActionButton(
            icon = when (gameState) {
                GameState.Running -> Icons.Default.Pause
                else -> Icons.Default.PlayArrow
            },
            text = when (gameState) {
                GameState.Running -> stringResource(R.string.pause_game)
                GameState.Paused -> stringResource(R.string.resume_game)
                GameState.GameOver -> stringResource(R.string.restart_game)
            },
            onClick = onPlayPauseClick,
            isPrimary = true,
            modifier = Modifier.weight(1f)
        )
        
        // Вспомогательная кнопка рестарта (только иконка)
        GameIconButton(
            icon = Icons.Default.Refresh,
            contentDescription = stringResource(R.string.restart_game),
            onClick = onRestartClick,
            modifier = Modifier
        )
    }
}

/**
 * Кнопка-иконка для управления игрой
 */
@Composable
private fun GameIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(50.dp),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(28.dp)
        )
    }
}

/**
 * Кнопка действия в интерфейсе с текстом
 */
@Composable
private fun ActionButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    Button(
        onClick = onClick,
        colors = if (isPrimary) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(vertical = 8.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(28.dp)
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}
