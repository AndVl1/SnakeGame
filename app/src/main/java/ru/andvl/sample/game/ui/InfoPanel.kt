package ru.andvl.sample.game.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.DecimalFormat

/**
 * Панель с информацией о текущем счете и скорости
 */
@Composable
fun InfoPanel(
    score: Int,
    speedFactor: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Счет
            Text(
                text = "Счет: $score",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Скорость
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Скорость",
                    tint = getSpeedColor(speedFactor)
                )
                
                Text(
                    text = "×${formatSpeed(speedFactor)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = getSpeedColor(speedFactor)
                )
            }
        }
    }
}

/**
 * Форматирование значения скорости
 */
private fun formatSpeed(speed: Float): String {
    return DecimalFormat("0.0").format(speed)
}

/**
 * Определение цвета отображения скорости в зависимости от значения
 */
private fun getSpeedColor(speed: Float): Color {
    return when {
        speed < 0.8f -> Color(0xFF2196F3) // Синий для низкой скорости
        speed < 1.5f -> Color(0xFF4CAF50) // Зеленый для нормальной скорости
        speed < 2.0f -> Color(0xFFFFC107) // Желтый для повышенной скорости
        else -> Color(0xFFF44336)         // Красный для высокой скорости
    }
} 