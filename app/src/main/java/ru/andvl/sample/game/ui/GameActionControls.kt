package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Уменьшаем вертикальный отступ
        horizontalArrangement = Arrangement.SpaceEvenly,
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
            isPrimary = true,
            modifier = Modifier.weight(1.2f) // Даем чуть больше пространства главной кнопке
        )
        
        // Кнопка перезапуска
        ActionButton(
            icon = Icons.Default.Refresh,
            text = "Рестарт",
            onClick = onRestartClick,
            modifier = Modifier.weight(1f)
        )
        
        // Кнопка инструкций
        ActionButton(
            icon = Icons.Default.Info,
            text = "Инфо",
            onClick = onShowInstructionsClick,
            modifier = Modifier.weight(1f)
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
        shape = RoundedCornerShape(8.dp), // Более квадратная форма с небольшим округлением
        modifier = modifier
            .padding(horizontal = 6.dp)
            .padding(vertical = 8.dp), // Увеличенный вертикальный отступ
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp, // Добавляем небольшую тень для лучшей различимости
            pressedElevation = 8.dp
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(28.dp) // Увеличиваем размер иконки
        )
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp // Увеличиваем размер текста
        )
    }
} 
