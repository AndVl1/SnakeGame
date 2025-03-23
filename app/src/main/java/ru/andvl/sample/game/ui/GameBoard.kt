package ru.andvl.sample.game.ui

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import ru.andvl.sample.game.Direction
import ru.andvl.sample.game.FoodType
import ru.andvl.sample.game.Obstacle
import ru.andvl.sample.game.SnakePart
import ru.andvl.sample.game.model.DisplayGameFood
import ru.andvl.sample.game.model.Food
import ru.andvl.sample.game.model.FoodType as NewFoodType
import ru.andvl.sample.game.model.GameModelConverter
import ru.andvl.sample.game.model.GridPosition
import kotlin.math.abs

// Константы для размеров игрового поля
private const val GRID_SIZE = 16
private const val BOARD_SIZE_FOR_FOOD = 20

/**
 * Компонент игрового поля
 */
@Composable
fun GameBoard(
    snakeParts: List<SnakePart>,
    food: DisplayGameFood,
    obstacles: List<Obstacle>,
    isGameOver: Boolean,
    doubleScoreActive: Boolean,
    pulsatingSpeedActive: Boolean,
    modifier: Modifier = Modifier,
    boardSize: Int = GRID_SIZE, // Используем константу по умолчанию
    onDirectionChange: (Direction) -> Unit,
) {
    // Логирование координат еды
    LaunchedEffect(food) {
        Log.d("GameBoard", "Еда появилась: тип=${food.type}, позиция=(${food.position.x}, ${food.position.y})")
    }
    
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
        // Используем remember для кэширования размеров канваса
        var savedCanvasWidth = 0f
        var savedPixelCellSize = 0f
        
        // Рисуем сетку и препятствия на Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val pixelCellSize = canvasWidth / boardSize
            
            // Сохраняем значения для использования за пределами Canvas
            savedCanvasWidth = canvasWidth
            savedPixelCellSize = pixelCellSize
            
            Log.d("GameBoard", "Размер Canvas: width=$canvasWidth, height=$canvasHeight, cellSize=$pixelCellSize")

            // Рисуем сетку
            val gridColor = Color(0xFFDDDDDD)
            for (i in 0..boardSize) {
                // Вертикальные линии
                drawLine(
                    color = gridColor,
                    start = Offset(i * pixelCellSize, 0f),
                    end = Offset(i * pixelCellSize, canvasHeight),
                    strokeWidth = 1f
                )
                // Горизонтальные линии
                drawLine(
                    color = gridColor,
                    start = Offset(0f, i * pixelCellSize),
                    end = Offset(canvasWidth, i * pixelCellSize),
                    strokeWidth = 1f
                )
            }
            
            // Рисуем препятствия
            obstacles.forEach { obstacle ->
                // Нормализуем координаты, чтобы они были в пределах отображаемой сетки
                val normalizedX = obstacle.x % boardSize
                val normalizedY = obstacle.y % boardSize
                
                drawRect(
                    color = Color(0xFF5D4037), // Коричневый цвет для препятствий
                    topLeft = Offset(normalizedX * pixelCellSize, normalizedY * pixelCellSize),
                    size = Size(pixelCellSize, pixelCellSize)
                )
            }
            
            // Рисуем змейку
            snakeParts.forEachIndexed { index, part ->
                // Нормализуем координаты, чтобы они были в пределах отображаемой сетки
                val normalizedX = part.x % boardSize
                val normalizedY = part.y % boardSize
                
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
                    topLeft = Offset(normalizedX * pixelCellSize, normalizedY * pixelCellSize),
                    size = Size(pixelCellSize, pixelCellSize)
                )
            }
            
            // ПОЛНОСТЬЮ ПЕРЕДЕЛЫВАЕМ ОТРИСОВКУ ЕДЫ
            // Получаем логические координаты из еды на основе точного размера ячейки
            val gridX = (food.position.x / GameModelConverter.CELL_SIZE).toInt()
            val gridY = (food.position.y / GameModelConverter.CELL_SIZE).toInt()
            
            // Вычисляем центр ячейки в пикселях Canvas
            val foodCenterX = (gridX + 0.5f) * pixelCellSize
            val foodCenterY = (gridY + 0.5f) * pixelCellSize
            
            Log.d("GameBoard", "==== ОТЛАДКА КООРДИНАТ ЕДЫ ====")
            Log.d("GameBoard", "Исходные пиксельные: x=${food.position.x}, y=${food.position.y}")
            Log.d("GameBoard", "Множитель GameModelConverter.CELL_SIZE=${GameModelConverter.CELL_SIZE}")
            Log.d("GameBoard", "Множитель нашего Canvas pixelCellSize=$pixelCellSize")
            Log.d("GameBoard", "Логические координаты сетки: x=$gridX, y=$gridY") 
            Log.d("GameBoard", "Пиксельные координаты центра для отрисовки: x=$foodCenterX, y=$foodCenterY")
            
            // Определяем цвет на основе типа еды
            val foodColor = when (food.type) {
                FoodType.REGULAR -> Color.Red
                FoodType.SPEED_UP -> Color.Green
                FoodType.DOUBLE_SCORE -> Color(0xFFFFD700) // Золотой
                else -> Color.Red
            }
            
            // Рисуем базовый круг еды в центре ячейки сетки
            drawCircle(
                color = foodColor,
                radius = pixelCellSize * 0.4f,
                center = Offset(foodCenterX, foodCenterY)
            )
            
            // Рисуем границу ячейки для отладки
            drawRect(
                color = Color.Blue.copy(alpha = 0.3f),
                topLeft = Offset(gridX * pixelCellSize, gridY * pixelCellSize),
                size = Size(pixelCellSize, pixelCellSize)
            )
            
            // Рисуем индикатор центра ячейки
            drawCircle(
                color = Color.Black,
                radius = 3f,
                center = Offset(foodCenterX, foodCenterY)
            )
        }
        
        // Расчет размера ячейки для анимированной еды
        val pixelCellSize = savedCanvasWidth / boardSize
        
        // Получаем логические координаты еды для анимации
        val gridX = (food.position.x / GameModelConverter.CELL_SIZE).toInt()
        val gridY = (food.position.y / GameModelConverter.CELL_SIZE).toInt()
        
        // Логирование позиции еды
        Log.d("GameBoard", "Позиция точки для AnimatedFood: x=${food.position.x}, y=${food.position.y}")
        
        // ИСПРАВЛЯЕМ ЛОГИКУ ДЛЯ АНИМИРОВАННОЙ ЕДЫ
        // Создаем точку для анимации с корректными координатами сетки
        val foodObj = Food(
            position = GridPosition(
                x = gridX,
                y = gridY
            ),
            type = when(food.type) {
                FoodType.REGULAR -> NewFoodType.REGULAR
                FoodType.DOUBLE_SCORE -> NewFoodType.DOUBLE_SCORE
                FoodType.SPEED_UP -> NewFoodType.SPEED_BOOST
                else -> NewFoodType.REGULAR
            }
        )
        
        // Подробное логирование для отладки
        Log.d("GameBoard", "Создана точка для анимации: тип=${foodObj.type}, позиция=(${foodObj.position.x}, ${foodObj.position.y})")
        
        // Создаем IntSize для доски
        val boardSizeForAnimatedFood = IntSize(boardSize, boardSize)
        
        // Показываем анимированную еду
        AnimatedFood(
            food = foodObj,
            boardSize = boardSizeForAnimatedFood,
            modifier = Modifier.fillMaxSize()
        )
    }
} 
