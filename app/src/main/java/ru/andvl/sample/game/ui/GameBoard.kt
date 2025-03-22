package ru.andvl.sample.game.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import ru.andvl.sample.game.Direction
import ru.andvl.sample.game.FoodType
import ru.andvl.sample.game.Obstacle
import ru.andvl.sample.game.SnakePart
import ru.andvl.sample.game.model.GameFood
import kotlin.math.abs

/**
 * Компонент игрового поля
 */
@Composable
fun GameBoard(
    snakeParts: List<SnakePart>,
    food: GameFood,
    obstacles: List<Obstacle>,
    isGameOver: Boolean,
    doubleScoreActive: Boolean,
    pulsatingSpeedActive: Boolean,
    boardSize: Int = 16,
    onDirectionChange: (Direction) -> Unit,
    modifier: Modifier = Modifier
) {
    // Анимация пульсации для смерти
    val infiniteTransition = rememberInfiniteTransition(label = "deathPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // Анимация затухания для смерти
    val fadeAlpha by animateFloatAsState(
        targetValue = if (isGameOver) 0.7f else 1f,
        animationSpec = tween(1500),
        label = "fade"
    )
    
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(Color(0xFFEEEEEE))
            .scale(if (isGameOver) pulseScale else 1f)
            .graphicsLayer { alpha = fadeAlpha }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    when {
                        abs(x) > abs(y) -> {
                            if (x > 0) onDirectionChange(Direction.RIGHT)
                            else onDirectionChange(Direction.LEFT)
                        }
                        else -> {
                            if (y > 0) onDirectionChange(Direction.DOWN)
                            else onDirectionChange(Direction.UP)
                        }
                    }
                }
            }
    ) {
        // Рисуем сетку и препятствия на Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val pixelCellSize = size.width / boardSize
            
            // Рисуем сетку
            val gridColor = Color(0xFFDDDDDD)
            for (i in 0..boardSize) {
                // Вертикальные линии
                drawLine(
                    color = gridColor,
                    start = Offset(i * pixelCellSize, 0f),
                    end = Offset(i * pixelCellSize, size.height),
                    strokeWidth = 1f
                )
                // Горизонтальные линии
                drawLine(
                    color = gridColor,
                    start = Offset(0f, i * pixelCellSize),
                    end = Offset(size.width, i * pixelCellSize),
                    strokeWidth = 1f
                )
            }
            
            // Рисуем препятствия
            obstacles.forEach { obstacle ->
                drawRect(
                    color = Color(0xFF5D4037), // Коричневый цвет для препятствий
                    topLeft = Offset(obstacle.x * pixelCellSize, obstacle.y * pixelCellSize),
                    size = Size(pixelCellSize, pixelCellSize)
                )
            }
            
            // Рисуем змейку
            snakeParts.forEachIndexed { index, part ->
                // Определяем цвет части змейки
                val snakeColor = when {
                    // Голова красная при смерти
                    isGameOver && index == 0 -> Color.Red
                    
                    // Голова зеленая в обычном состоянии
                    index == 0 -> Color(0xFF4CAF50)
                    
                    // Тело желтое при активном удвоении очков
                    doubleScoreActive && index % 2 == 0 -> Color(0xFFFFEB3B)
                    
                    // Тело с переливающимися цветами при активной пульсации скорости
                    pulsatingSpeedActive && index % 2 == 0 -> Color(0xFFE91E63)
                    
                    // Обычный цвет тела
                    else -> Color(0xFF8BC34A)
                }
                
                drawRect(
                    color = snakeColor,
                    topLeft = Offset(part.x * pixelCellSize, part.y * pixelCellSize),
                    size = Size(pixelCellSize, pixelCellSize)
                )
            }
        }
        
        // Добавляем анимированную еду как Composable
        AnimatedFood(
            food = food,
            cellSize = 0f, // Значение не используется в новой версии
            boardSize = boardSize,
            modifier = Modifier.fillMaxSize()
        )
    }
} 