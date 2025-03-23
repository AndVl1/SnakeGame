package ru.andvl.sample.game.ui

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.andvl.sample.game.model.Food
import ru.andvl.sample.game.model.FoodType
import kotlin.math.absoluteValue
import kotlin.math.min
import kotlin.math.sin

/**
 * Компонент анимированной еды
 */
@Composable
fun AnimatedFood(
    food: Food,
    boardSize: IntSize,
    modifier: Modifier = Modifier
) {
    
    // Определяем цвет на основе типа еды
    val foodColor = when (food.type) {
        FoodType.REGULAR -> Color.Red
        FoodType.SPEED_BOOST -> Color.Green
        FoodType.DOUBLE_SCORE -> Color(0xFFFFD700) // Золотой
    }
    
    // Создаем анимации для разных типов еды
    val infiniteTransition = rememberInfiniteTransition()
    
    // Анимация для обычной еды (пульсация)
    val scale = if (food.type == FoodType.REGULAR) {
        infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        ).value
    } else {
        1.0f
    }
    
    // Анимация для ускорения (вращение)
    val rotation = if (food.type == FoodType.SPEED_BOOST) {
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        ).value
    } else {
        0f
    }
    
    // Анимация для удвоения очков (прозрачность)
    val alpha = if (food.type == FoodType.DOUBLE_SCORE) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(500, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        ).value
    } else {
        1.0f
    }
    
    // Теперь рисуем анимации в Canvas, чтобы иметь доступ к размеру канваса
    Canvas(modifier = modifier.fillMaxSize()) {
        // Получаем фактические размеры канваса
        val canvasWidth = size.width
        val canvasHeight = size.height
        
        // Рассчитываем размер ячейки так же, как в GameBoard
        val pixelCellSize = canvasWidth / boardSize.width
        
        // Вычисляем центр еды в пикселях
        val x = (food.position.x + 0.5f) * pixelCellSize
        val y = (food.position.y + 0.5f) * pixelCellSize
        
        // Размер еды в пикселях
        val foodSize = pixelCellSize * 0.4f

        // Рисуем в зависимости от типа еды
        when (food.type) {
            FoodType.REGULAR -> {
                // Рисуем белую обводку с анимацией
                drawCircle(
                    color = Color.White,
                    center = Offset(x, y),
                    radius = foodSize * 1.1f * scale
                )
                
                // Рисуем сам круг с анимацией
                drawCircle(
                    color = foodColor,
                    center = Offset(x, y),
                    radius = foodSize * scale
                )
            }
            FoodType.SPEED_BOOST -> {
                // Вращаем Canvas вокруг центра еды
                rotate(rotation, Offset(x, y)) {
                    // Рисуем белую обводку для квадрата
                    drawRect(
                        color = Color.White,
                        topLeft = Offset(x - foodSize * 1.1f, y - foodSize * 1.1f),
                        size = Size(foodSize * 2.2f, foodSize * 2.2f)
                    )
                    
                    // Рисуем сам квадрат
                    drawRect(
                        color = foodColor,
                        topLeft = Offset(x - foodSize, y - foodSize),
                        size = Size(foodSize * 2, foodSize * 2)
                    )
                }
            }
            FoodType.DOUBLE_SCORE -> {
                // Рисуем ромб с белой обводкой
                drawDiamond(x, y, foodSize * 2.2f, Color.White)
                
                // Рисуем ромб основного цвета с анимацией прозрачности
                drawDiamond(x, y, foodSize * 2f, foodColor.copy(alpha = alpha))
            }
        }
    }
}

/**
 * Вспомогательная функция для рисования ромба
 */
private fun DrawScope.drawDiamond(x: Float, y: Float, size: Float, color: Color) {
    val halfSize = size / 2
    
    // Создаем путь в форме ромба
    drawPath(
        path = androidx.compose.ui.graphics.Path().apply {
            moveTo(x, y - halfSize) // Верхняя точка
            lineTo(x + halfSize, y) // Правая точка
            lineTo(x, y + halfSize) // Нижняя точка
            lineTo(x - halfSize, y) // Левая точка
            close()
        },
        color = color
    )
} 
