package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
    // Размер кнопок
    val buttonSize = 70.dp
    
    // Основной контейнер для управления
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Основной ряд с кнопками ВЛЕВО, центральным столбцом и ВПРАВО
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка ВЛЕВО
            DirectionButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                direction = Direction.LEFT,
                onDirectionChange = onDirectionChange
            )
            
            // Центральная колонка с кнопками ВВЕРХ и ВНИЗ
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Кнопка ВВЕРХ
                DirectionButton(
                    icon = Icons.Default.KeyboardArrowUp,
                    direction = Direction.UP,
                    onDirectionChange = onDirectionChange
                )
                
                // Кнопка ВНИЗ
                DirectionButton(
                    icon = Icons.Default.KeyboardArrowDown,
                    direction = Direction.DOWN,
                    onDirectionChange = onDirectionChange
                )
            }
            
            // Кнопка ВПРАВО
            DirectionButton(
                icon = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                direction = Direction.RIGHT,
                onDirectionChange = onDirectionChange
            )
        }
    }
}

/**
 * Кнопка управления направлением
 */
@Composable
private fun DirectionButton(
    icon: ImageVector,
    direction: Direction,
    onDirectionChange: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        FilledIconButton(
            onClick = { onDirectionChange(direction) },
            modifier = Modifier.size(70.dp),
            shape = RoundedCornerShape(8.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Двигаться ${getDirectionName(direction)}",
                modifier = Modifier.size(40.dp)
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