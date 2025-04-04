package ru.andvl.snakegame.game.ui

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import ru.andvl.snakegame.game.Direction
import ru.andvl.snakegame.game.FoodType
import ru.andvl.snakegame.game.Obstacle
import ru.andvl.snakegame.game.SnakePart
import ru.andvl.snakegame.game.model.DisplayGameFood
import ru.andvl.snakegame.game.model.GameConstants
import ru.andvl.snakegame.game.model.GameModelConverter
import ru.andvl.snakegame.game.model.GridPosition
import kotlin.math.abs
import ru.andvl.snakegame.game.model.Food as NewFood
import ru.andvl.snakegame.game.model.FoodType as NewFoodType

// Константа для размера сетки из единого источника
const val GRID_SIZE = GameConstants.BOARD_SIZE

/**
 * Компонент игрового поля
 */
@Composable
fun GameBoard(
    snakeParts: List<SnakePart>,
    food: DisplayGameFood?,
    obstacles: List<Obstacle>,
    isGameOver: Boolean,
    doubleScoreActive: Boolean,
    pulsatingSpeedActive: Boolean,
    modifier: Modifier = Modifier,
    boardSize: Int = GRID_SIZE, // Используем константу из единого источника
    onDirectionChange: (Direction) -> Unit,
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
            .graphicsLayer {
                alpha = fadeAlpha
                scaleX = if (isGameOver) pulseScale else 1f
                scaleY = if (isGameOver) pulseScale else 1f
            }
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
            food?.let { safeFood ->
                // Преобразуем пиксельные координаты в координаты сетки
                val gridX = (safeFood.position.x / GameModelConverter.CELL_SIZE).toInt()
                val gridY = (safeFood.position.y / GameModelConverter.CELL_SIZE).toInt()

                // Используем нормализованные координаты для учета того же размера сетки, как в GameEngine
                val normalizedX = gridX % boardSize
                val normalizedY = gridY % boardSize

                // Вычисляем центр ячейки в пикселях Canvas
                val centerX = (normalizedX + 0.5f) * pixelCellSize
                val centerY = (normalizedY + 0.5f) * pixelCellSize

                // Отладочный вывод для координат еды
                println("DEBUG: Отрисовка еды - пиксельные: (${safeFood.position.x}, ${safeFood.position.y}), сетка: ($gridX, $gridY), нормализованные: ($normalizedX, $normalizedY), центр: ($centerX, $centerY)")

                // Определяем радиус еды (чуть меньше половины ячейки)
                val radius = pixelCellSize * 0.4f

                // Определяем цвет на основе типа еды
                val foodColor = when (safeFood.type) {
                    FoodType.REGULAR -> Color.Red
                    FoodType.SPEED_UP -> Color.Green
                    FoodType.DOUBLE_SCORE -> Color(0xFFFFD700) // Gold
                    FoodType.SLOW_DOWN -> Color.Blue
                }

                // Рисуем еду как круг
                drawCircle(
                    color = foodColor,
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }
        }

        // Расчет размера ячейки для анимированной еды
        val pixelCellSize = savedCanvasWidth / boardSize

        // Анимируем еду в зависимости от типа
        food?.let { safeFood ->
            // Получаем логические координаты еды для анимации
            val gridX = (safeFood.position.x / GameModelConverter.CELL_SIZE).toInt()
            val gridY = (safeFood.position.y / GameModelConverter.CELL_SIZE).toInt()

            // Нормализуем координаты для согласованности с GameEngine
            val normalizedX = gridX % boardSize
            val normalizedY = gridY % boardSize

            // ИСПРАВЛЯЕМ ЛОГИКУ ДЛЯ АНИМИРОВАННОЙ ЕДЫ
            // Теперь данные для анимаций берутся из модели Food под капотом
            val foodObj = NewFood(
                position = GridPosition(
                    x = normalizedX,
                    y = normalizedY
                ),
                type = when(safeFood.type) {
                    FoodType.REGULAR -> NewFoodType.REGULAR
                    FoodType.DOUBLE_SCORE -> NewFoodType.DOUBLE_SCORE
                    FoodType.SPEED_UP -> NewFoodType.SPEED_UP
                    FoodType.SLOW_DOWN -> NewFoodType.SLOW_DOWN
                }
            )

            // Создаем IntSize для доски
            val boardSizeIntSize = IntSize(boardSize, boardSize)

            // Показываем анимированную еду
            AnimatedFood(
                food = foodObj,
                boardSize = boardSizeIntSize,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
