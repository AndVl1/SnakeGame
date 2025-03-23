package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ru.andvl.sample.game.model.GameState

/**
 * Панель с кнопками управления игрой
 */
@Composable
fun GameActionControls(
    gameState: GameState,
    onPlayPauseClick: () -> Unit,
    onRestartClick: () -> Unit,
    onShowInstructionsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Кнопка игры/паузы
        ActionButton(
            icon = when (gameState) {
                GameState.Running -> Icons.Default.Pause
                else -> Icons.Default.PlayArrow
            },
            text = when (gameState) {
                GameState.Running -> "Пауза"
                GameState.Paused -> "Играть"
                GameState.GameOver -> "Новая игра"
            },
            onClick = onPlayPauseClick,
            isPrimary = true
        )
        
        // Кнопка перезапуска
        ActionButton(
            icon = Icons.Default.Refresh,
            text = "Рестарт",
            onClick = onRestartClick
        )
        
        // Кнопка инструкций
        ActionButton(
            icon = Icons.Default.Info,
            text = "Инфо",
            onClick = onShowInstructionsClick
        )
    }
}

/**
 * Кнопка действия в интерфейсе
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
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(text = text)
    }
} 