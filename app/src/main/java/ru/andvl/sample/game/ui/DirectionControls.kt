package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ru.andvl.sample.game.Direction

/**
 * Элементы управления направлением змейки
 */
@Composable
fun GameDirectionControls(
    onDirectionChange: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Кнопка ВВЕРХ
        DirectionButton(
            icon = Icons.Default.KeyboardArrowUp,
            direction = Direction.UP,
            onDirectionChange = onDirectionChange
        )
        
        // Ряд с кнопками ВЛЕВО и ВПРАВО
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка ВЛЕВО
            DirectionButton(
                icon = Icons.Default.KeyboardArrowLeft,
                direction = Direction.LEFT,
                onDirectionChange = onDirectionChange
            )
            
            Spacer(modifier = Modifier.width(40.dp))
            
            // Кнопка ВПРАВО
            DirectionButton(
                icon = Icons.Default.KeyboardArrowRight,
                direction = Direction.RIGHT,
                onDirectionChange = onDirectionChange
            )
        }
        
        // Кнопка ВНИЗ
        DirectionButton(
            icon = Icons.Default.KeyboardArrowDown,
            direction = Direction.DOWN,
            onDirectionChange = onDirectionChange
        )
    }
}

/**
 * Кнопка управления направлением
 */
@Composable
private fun DirectionButton(
    icon: ImageVector,
    direction: Direction,
    onDirectionChange: (Direction) -> Unit
) {
    Box(contentAlignment = Alignment.Center) {
        FilledIconButton(
            onClick = { onDirectionChange(direction) },
            modifier = Modifier.size(56.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Двигаться ${getDirectionName(direction)}",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Получение названия направления на русском
 */
private fun getDirectionName(direction: Direction): String {
    return when (direction) {
        Direction.UP -> "вверх"
        Direction.DOWN -> "вниз"
        Direction.LEFT -> "влево"
        Direction.RIGHT -> "вправо"
    }
} 