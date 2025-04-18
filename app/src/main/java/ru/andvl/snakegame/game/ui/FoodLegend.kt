package ru.andvl.snakegame.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.andvl.snakegame.R

/**
 * Легенда для объяснения различных типов еды
 */
@Composable
fun FoodLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Верхний ряд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Обычная еда
                FoodLegendItem(
                    color = Color.Red,
                    label = stringResource(R.string.food_regular)
                )
                
                // Двойные очки
                FoodLegendItem(
                    color = Color(0xFFFFD700), // Золотой
                    label = stringResource(R.string.food_double_score)
                )
                
                // Ускорение
                FoodLegendItem(
                    color = Color.Green,
                    label = stringResource(R.string.food_speed_boost)
                )
            }
            
            // Нижний ряд
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Постоянное ускорение
                FoodLegendItem(
                    color = Color(0xFF00FFFF), // Голубой
                    label = stringResource(R.string.food_speed_up)
                )
                
                // Замедление
                FoodLegendItem(
                    color = Color(0xFFFF00FF), // Фиолетовый
                    label = stringResource(R.string.food_slow_down)
                )
            }
        }
    }
}

/**
 * Элемент легенды типа еды
 */
@Composable
private fun FoodLegendItem(
    color: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Цветной круг, представляющий тип еды
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        
        // Подпись
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
} 
