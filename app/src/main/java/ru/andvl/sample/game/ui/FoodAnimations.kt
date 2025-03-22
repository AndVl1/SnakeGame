package ru.andvl.sample.game.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ru.andvl.sample.game.FoodType
import ru.andvl.sample.game.model.GameFood
import kotlin.random.Random

/**
 * Анимированное отображение еды в игре с разными эффектами
 * в зависимости от типа еды
 */
@Composable
fun AnimatedFood(
    food: GameFood,
    cellSize: Float,
    boardSize: Int = 16,
    modifier: Modifier = Modifier
) {
    val foodColor = when (food.type) {
        FoodType.REGULAR -> Color.Red
        FoodType.SPEED_UP -> Color.Blue
        FoodType.DOUBLE_SCORE -> Color.Yellow
        FoodType.SLOW_DOWN -> Color.Magenta
    }
    
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val boxWidthPx = constraints.maxWidth
        val boxHeightPx = constraints.maxHeight
        
        // Размер ячейки в пикселях
        val cellSizePx = boxWidthPx / boardSize
        
        // Размер еды должен быть меньше размера ячейки (примерно 60%)
        val foodSizePx = cellSizePx * 0.6f
        val foodSizeDp = with(density) { foodSizePx.toDp() }
        
        // Центрирование еды внутри ячейки
        val centerOffsetPx = (cellSizePx - foodSizePx) / 2
        
        // Расчет позиции с учетом центрирования
        val xPositionPx = food.position.x * cellSizePx + centerOffsetPx
        val yPositionPx = food.position.y * cellSizePx + centerOffsetPx
        
        val xPositionDp = with(density) { xPositionPx.toDp() }
        val yPositionDp = with(density) { yPositionPx.toDp() }
        
        val infiniteTransition = rememberInfiniteTransition(label = "food_animation")
        
        when (food.type) {
            FoodType.REGULAR -> {
                // Обычная еда просто пульсирует
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.8f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "regular_food_pulse"
                )
                
                Box(
                    modifier = Modifier
                        .offset(x = xPositionDp, y = yPositionDp)
                        .size(foodSizeDp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(foodColor)
                )
            }
            
            FoodType.SPEED_UP -> {
                // Еда ускорения вращается
                val rotation by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "speed_food_rotation"
                )
                
                Box(
                    modifier = Modifier
                        .offset(x = xPositionDp, y = yPositionDp)
                        .size(foodSizeDp)
                        .graphicsLayer {
                            rotationZ = rotation
                        }
                        .clip(CircleShape)
                        .background(foodColor)
                )
            }
            
            FoodType.DOUBLE_SCORE -> {
                // Еда двойных очков мерцает
                var visible by remember { mutableStateOf(true) }
                
                LaunchedEffect(key1 = food) {
                    while (true) {
                        delay(200)
                        visible = !visible
                    }
                }
                
                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0.3f,
                    animationSpec = tween(200),
                    label = "double_score_food_flash"
                )
                
                Box(
                    modifier = Modifier
                        .offset(x = xPositionDp, y = yPositionDp)
                        .size(foodSizeDp)
                        .clip(CircleShape)
                        .background(foodColor.copy(alpha = alpha))
                        .border(1.dp, Color.White.copy(alpha = alpha), CircleShape)
                )
            }
            
            FoodType.SLOW_DOWN -> {
                // Еда замедления имеет случайные движения
                var offsetXPx by remember { mutableStateOf(0f) }
                var offsetYPx by remember { mutableStateOf(0f) }
                
                LaunchedEffect(key1 = food) {
                    while (true) {
                        offsetXPx = Random.nextFloat() * cellSizePx * 0.2f
                        offsetYPx = Random.nextFloat() * cellSizePx * 0.2f
                        delay(100)
                    }
                }
                
                val offsetXDp = with(density) { offsetXPx.toDp() }
                val offsetYDp = with(density) { offsetYPx.toDp() }
                
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.7f,
                    targetValue = 1.2f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(500, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "slow_down_food_scale"
                )
                
                Box(
                    modifier = Modifier
                        .offset(x = xPositionDp + offsetXDp, y = yPositionDp + offsetYDp)
                        .size(foodSizeDp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(foodColor)
                )
            }
        }
    }
} 