package ru.andvl.sample.game

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToLong

@Composable
fun SnakeGameScreen(snakeGame: SnakeGame = remember { SnakeGame() }) {
    var gameRunning by remember { mutableStateOf(false) }
    
    // Состояние для анимации окончания игры
    var isPlayingDeathAnimation by remember { mutableStateOf(false) }
    
    // Состояние для отображения инструкции
    var showInstructions by remember { mutableStateOf(true) }
    
    // При уходе с экрана сбрасываем анимацию
    DisposableEffect(Unit) {
        onDispose {
            isPlayingDeathAnimation = false
        }
    }
    
    // Запускаем анимацию смерти перед завершением
    LaunchedEffect(snakeGame.isGameOver) {
        if (snakeGame.isGameOver) {
            isPlayingDeathAnimation = true
            delay(1500) // Длительность анимации
            isPlayingDeathAnimation = false
        }
    }
    
    // Диалог с инструкцией
    if (showInstructions) {
        InstructionsDialog(
            onDismiss = { showInstructions = false }
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Змейка+",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // Информационная панель с фиксированной высотой
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp) // Фиксируем высоту информационной панели
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Панель со счетом и скоростью
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Счёт: ${snakeGame.score}",
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Text(
                        text = "Скорость: ${String.format("%.1f", snakeGame.speedFactor)}x",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (snakeGame.pulsatingSpeedActive) 
                            MaterialTheme.colorScheme.tertiary
                        else 
                            MaterialTheme.colorScheme.primary
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Панель активных эффектов
                if (snakeGame.doubleScoreActive || snakeGame.pulsatingSpeedActive) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (snakeGame.doubleScoreActive) {
                            Text(
                                text = "2X ОЧКИ!",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        if (snakeGame.pulsatingSpeedActive) {
                            Text(
                                text = "ПУЛЬСАЦИЯ!",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
        
        Text(
            text = "Проходите через стены!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // Пояснение по типам еды
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Red, CircleShape)
            )
            Text(
                text = "Обычная",
                style = MaterialTheme.typography.bodySmall
            )
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Blue, CircleShape)
            )
            Text(
                text = "Скорость",
                style = MaterialTheme.typography.bodySmall
            )
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Yellow, CircleShape)
            )
            Text(
                text = "2X очки",
                style = MaterialTheme.typography.bodySmall
            )
            
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Magenta, CircleShape)
            )
            Text(
                text = "Пульс",
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        GameBoard(
            snakeGame = snakeGame,
            isGameOver = isPlayingDeathAnimation,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFEEEEEE))
        )
        
        // Кнопки управления направлением движения
        DirectionControls(snakeGame = snakeGame)
        
        // Кнопки игра/пауза и инструкция
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка игра/пауза
            if (!snakeGame.isGameOver) {
                Button(
                    onClick = { gameRunning = !gameRunning },
                    modifier = Modifier
                        .weight(0.7f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (gameRunning) 
                            MaterialTheme.colorScheme.secondary 
                        else 
                            MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (gameRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (gameRunning) "Пауза" else "Играть",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Кнопка инструкции
                IconButton(
                    onClick = { showInstructions = true },
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = "?",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
    
    // Игровой цикл с учетом скорости
    LaunchedEffect(gameRunning, snakeGame.speedFactor) {
        while (gameRunning && !snakeGame.isGameOver) {
            // Базовая задержка 300 мс, делённая на фактор скорости
            val delayTime = (300 / snakeGame.speedFactor).roundToLong()
            delay(delayTime)
            snakeGame.update()
        }
        
        if (snakeGame.isGameOver) {
            gameRunning = false
        }
    }
}

@Composable
fun InstructionsDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Как играть в Змейку+",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Цель игры: собирайте еду и увеличивайте счёт, не врезаясь в препятствия и в своё тело.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Управление:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                
                Text(
                    text = "• Используйте кнопки направлений\n• Проводите пальцем по игровому полю\n• Змейка может проходить через стены",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Типы еды:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Red, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Обычная еда: +1 очко",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Blue, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ускорение: +1 очко, +20% скорости",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Yellow, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Двойные очки: удвоение очков на 10 секунд",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Magenta, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Пульсация: изменяющаяся скорость на 15 секунд",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Особенности:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                
                Text(
                    text = "• Специальная еда исчезает через 8 секунд\n• Коричневые препятствия появляются во время игры\n• Скорость растёт каждые 5 очков\n• Мигающие бонусы указывают на активные эффекты",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Вперед к игре!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun DirectionControls(snakeGame: SnakeGame) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Кнопка вверх
        IconButton(
            onClick = { snakeGame.changeDirection(Direction.UP) },
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Вверх",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(36.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Кнопки влево-вправо
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { snakeGame.changeDirection(Direction.LEFT) },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Влево",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(60.dp))
            
            IconButton(
                onClick = { snakeGame.changeDirection(Direction.RIGHT) },
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Вправо",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Кнопка вниз
        IconButton(
            onClick = { snakeGame.changeDirection(Direction.DOWN) },
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Вниз",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun GameBoard(
    snakeGame: SnakeGame,
    isGameOver: Boolean,
    modifier: Modifier = Modifier
) {
    val boardSize = snakeGame.boardSize
    
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
    
    // Анимация мерцания для специальной еды
    val foodPulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "foodPulse"
    )
    
    Box(
        modifier = modifier
            .scale(if (isGameOver) pulseScale else 1f)
            .graphicsLayer { alpha = fadeAlpha }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    when {
                        abs(x) > abs(y) -> {
                            if (x > 0) snakeGame.changeDirection(Direction.RIGHT)
                            else snakeGame.changeDirection(Direction.LEFT)
                        }
                        else -> {
                            if (y > 0) snakeGame.changeDirection(Direction.DOWN)
                            else snakeGame.changeDirection(Direction.UP)
                        }
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / boardSize
            
            // Рисуем сетку
            val gridColor = Color(0xFFDDDDDD)
            for (i in 0..boardSize) {
                // Вертикальные линии
                drawLine(
                    color = gridColor,
                    start = Offset(i * cellSize, 0f),
                    end = Offset(i * cellSize, size.height),
                    strokeWidth = 1f
                )
                // Горизонтальные линии
                drawLine(
                    color = gridColor,
                    start = Offset(0f, i * cellSize),
                    end = Offset(size.width, i * cellSize),
                    strokeWidth = 1f
                )
            }
            
            // Рисуем препятствия
            snakeGame.obstacles.forEach { obstacle ->
                drawRect(
                    color = Color(0xFF5D4037), // Коричневый цвет для препятствий
                    topLeft = Offset(obstacle.x * cellSize, obstacle.y * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
            
            // Рисуем еду с учетом типа
            val foodColor = when (snakeGame.food.type) {
                FoodType.REGULAR -> Color.Red
                FoodType.SPEED_UP -> Color.Blue
                FoodType.DOUBLE_SCORE -> Color.Yellow
                FoodType.SLOW_DOWN -> Color.Magenta
            }
            
            // Определяем, нужно ли анимировать еду (для специальных типов)
            val shouldPulse = snakeGame.food.type != FoodType.REGULAR
            val foodAlpha = if (shouldPulse) foodPulse else 1f
            
            // Рисуем еду как круг с анимацией пульсации для специальных типов
            drawCircle(
                color = foodColor.copy(alpha = foodAlpha),
                radius = cellSize / 2,
                center = Offset(
                    snakeGame.food.position.x * cellSize + cellSize / 2,
                    snakeGame.food.position.y * cellSize + cellSize / 2
                )
            )
            
            // Для специальных типов еды добавляем внутренний круг
            if (snakeGame.food.type != FoodType.REGULAR) {
                drawCircle(
                    color = Color.White,
                    radius = cellSize / 4,
                    center = Offset(
                        snakeGame.food.position.x * cellSize + cellSize / 2,
                        snakeGame.food.position.y * cellSize + cellSize / 2
                    )
                )
            }
            
            // Рисуем змейку
            snakeGame.snake.forEachIndexed { index, part ->
                // Определяем цвет части змейки
                val snakeColor = when {
                    // Голова красная при смерти
                    isGameOver && index == 0 -> Color.Red
                    
                    // Голова зеленая в обычном состоянии
                    index == 0 -> Color(0xFF4CAF50)
                    
                    // Тело желтое при активном удвоении очков
                    snakeGame.doubleScoreActive && index % 2 == 0 -> Color(0xFFFFEB3B)
                    
                    // Тело с переливающимися цветами при активной пульсации скорости
                    snakeGame.pulsatingSpeedActive && index % 2 == 0 -> Color(0xFFE91E63)
                    
                    // Обычный цвет тела
                    else -> Color(0xFF8BC34A)
                }
                
                drawRect(
                    color = snakeColor,
                    topLeft = Offset(part.x * cellSize, part.y * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }
        }
    }
}