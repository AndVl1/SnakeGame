package ru.andvl.sample.game.ui

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import ru.andvl.sample.game.model.Food
import ru.andvl.sample.game.model.FoodType
import kotlin.math.absoluteValue
import kotlin.math.sin

/**
 * Компонент анимированной еды
 */
@Composable
fun AnimatedFood(
    food: Food?,
    boardSize: IntSize,
    modifier: Modifier = Modifier
) {
    if (food == null) return
    
    // Вычисление размеров на основе доски
    val boardWidth = boardSize.width
    val boardHeight = boardSize.height
    val pixelCellSize = boardWidth / boardHeight.toFloat()
    
    // Размер еды - 80% от размера ячейки для лучшей видимости
    val foodSize = pixelCellSize * 0.8f
    
    // Определяем цвет на основе типа еды
    val foodColor = when (food.type) {
        FoodType.REGULAR -> Color.Red
        FoodType.SPEED_BOOST -> Color.Green
        FoodType.DOUBLE_SCORE -> Color(0xFFFFD700) // Золотой
    }
    
    // Вычисляем центр точки - правильное преобразование из логической позиции
    val foodCenterX = (food.position.x + 0.5f) * pixelCellSize
    val foodCenterY = (food.position.y + 0.5f) * pixelCellSize
    
    // Подробное логирование для отладки
    LaunchedEffect(food) {
        Log.d("AnimatedFood", "==== ОТРИСОВКА АНИМИРОВАННОЙ ЕДЫ ====")
        Log.d("AnimatedFood", "Тип точки: ${food.type}")
        Log.d("AnimatedFood", "Логические координаты сетки: (${food.position.x}, ${food.position.y})")
        Log.d("AnimatedFood", "Размер ячейки Canvas: $pixelCellSize")
        Log.d("AnimatedFood", "Пиксельные координаты центра: ($foodCenterX, $foodCenterY)")
        Log.d("AnimatedFood", "Размер точки: $foodSize")
    }
    
    // ДОБАВЛЯЕМ ОБЯЗАТЕЛЬНУЮ ОТРИСОВКУ ТОЧКИ В ЛЮБОМ СЛУЧАЕ
    Canvas(modifier = modifier.fillMaxSize().border(3.dp, Color.Red)) {
        // Рисуем базовый круг в любом случае для гарантии отображения
        drawCircle(
            color = foodColor,
            radius = foodSize * 0.5f,
            center = Offset(foodCenterX, foodCenterY)
        )
        
        // Для отладки рисуем черную точку в центре
        drawCircle(
            color = Color.Black,
            radius = 3f,
            center = Offset(foodCenterX, foodCenterY)
        )
    }
    
    // Выбираем анимацию в зависимости от типа еды
    when (food.type) {
        FoodType.REGULAR -> RegularFoodAnimation(foodCenterX, foodCenterY, foodSize, foodColor, modifier)
        FoodType.SPEED_BOOST -> SpeedBoostFoodAnimation(foodCenterX, foodCenterY, foodSize, foodColor, modifier)
        FoodType.DOUBLE_SCORE -> DoubleScoreFoodAnimation(foodCenterX, foodCenterY, foodSize, foodColor, modifier)
    }
}

/**
 * Анимация обычной еды - пульсирующий круг
 */
@Composable
private fun RegularFoodAnimation(x: Float, y: Float, size: Float, color: Color, modifier: Modifier) {
    // Создаем бесконечный переход для анимации
    val infiniteTransition = rememberInfiniteTransition()
    
    // Анимация масштаба для пульсации
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Отрисовка на Canvas
    Canvas(modifier = modifier.fillMaxSize()) {
        // Белая граница для лучшей видимости
        drawCircle(
            color = Color.White,
            radius = size * scale * 0.55f,
            center = Offset(x, y)
        )
        
        // Цветной центр
        drawCircle(
            color = color,
            radius = size * scale * 0.5f,
            center = Offset(x, y)
        )
    }
}

/**
 * Анимация еды с ускорением - вращающийся квадрат
 */
@Composable
private fun SpeedBoostFoodAnimation(x: Float, y: Float, size: Float, color: Color, modifier: Modifier) {
    // Создаем бесконечный переход для анимации
    val infiniteTransition = rememberInfiniteTransition()
    
    // Анимация вращения
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    
    // Отрисовка на Canvas
    Canvas(modifier = modifier.fillMaxSize()) {
        // Вращаем Canvas вокруг центра еды
        rotate(rotation, Offset(x, y)) {
            // Белая граница для лучшей видимости
            drawRect(
                color = Color.White,
                topLeft = Offset(x - size * 0.55f, y - size * 0.55f),
                size = androidx.compose.ui.geometry.Size(size * 1.1f, size * 1.1f)
            )
            
            // Цветной центр
            drawRect(
                color = color,
                topLeft = Offset(x - size * 0.5f, y - size * 0.5f),
                size = androidx.compose.ui.geometry.Size(size, size)
            )
        }
    }
}

/**
 * Анимация еды с удвоением очков - мерцающий ромб
 */
@Composable
private fun DoubleScoreFoodAnimation(x: Float, y: Float, size: Float, color: Color, modifier: Modifier) {
    // Создаем бесконечный переход для анимации
    val infiniteTransition = rememberInfiniteTransition()
    
    // Анимация прозрачности для мерцания
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    
    // Отрисовка на Canvas
    Canvas(modifier = modifier.fillMaxSize()) {
        // Белая граница для лучшей видимости
        drawDiamond(x, y, size * 1.1f, Color.White)
        
        // Цветной центр с анимированной прозрачностью
        drawDiamond(x, y, size, color.copy(alpha = alpha))
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
