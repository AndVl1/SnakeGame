package ru.andvl.sample.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Компонент отображения информационной панели с очками и эффектами
 */
@Composable
fun GameInfoPanel(
    score: Int,
    speedFactor: Float,
    doubleScoreActive: Boolean,
    pulsatingSpeedActive: Boolean,
    modifier: Modifier = Modifier
) {
    // Информационная панель с фиксированной высотой
    Box(modifier = modifier.height(80.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Панель со счетом и скоростью
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Счёт: $score",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = "Скорость: ${String.format("%.1f", speedFactor)}x",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (pulsatingSpeedActive) 
                        MaterialTheme.colorScheme.tertiary
                    else 
                        MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Панель активных эффектов
            if (doubleScoreActive || pulsatingSpeedActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (doubleScoreActive) {
                        Text(
                            text = "2X ОЧКИ!",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    if (pulsatingSpeedActive) {
                        Text(
                            text = "ПУЛЬСАЦИЯ!",
                            color = MaterialTheme.colorScheme.tertiary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Компонент отображения информации о типах еды
 */
@Composable
fun FoodTypesLegend(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Проходите через стены!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
        )
        
        // Более расширенное представление можно реализовать здесь
    }
} 