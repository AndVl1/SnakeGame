package ru.andvl.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.andvl.sample.data.SettingsRepository
import ru.andvl.sample.game.model.GameSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val settings by settingsRepository.settings.collectAsStateWithLifecycle(initialValue = GameSettings())
    
    // Локальные состояния для слайдеров
    var difficulty by remember { mutableIntStateOf(settings.difficulty) }
    var boardSize by remember { mutableIntStateOf(settings.boardSize) }
    var initialSpeed by remember { mutableFloatStateOf(settings.initialSpeedFactor) }
    var maxObstacles by remember { mutableIntStateOf(settings.maxObstacles) }
    var specialFoodFrequency by remember { mutableIntStateOf(settings.specialFoodFrequency) }
    
    // Локальные состояния для свитчей
    var isDarkTheme by remember { mutableStateOf(settings.isDarkTheme) }
    var vibrationsEnabled by remember { mutableStateOf(settings.vibrationsEnabled) }
    var soundsEnabled by remember { mutableStateOf(settings.soundsEnabled) }
    
    // Синхронизация локальных состояний с настройками
    LaunchedEffect(settings) {
        difficulty = settings.difficulty
        boardSize = settings.boardSize
        initialSpeed = settings.initialSpeedFactor
        maxObstacles = settings.maxObstacles
        specialFoodFrequency = settings.specialFoodFrequency
        isDarkTheme = settings.isDarkTheme
        vibrationsEnabled = settings.vibrationsEnabled
        soundsEnabled = settings.soundsEnabled
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            settingsRepository.resetSettings()
                        }
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Сбросить настройки")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Настройки игры",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            
            // Секция общих настроек
            SettingsSection(title = "Общие настройки") {
                // Темная тема
                SettingsSwitchItem(
                    title = "Темная тема",
                    description = "Переключение между светлой и темной темой",
                    icon = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                    checked = isDarkTheme,
                    onCheckedChange = { checked ->
                        isDarkTheme = checked
                        coroutineScope.launch {
                            settingsRepository.updateDarkThemeEnabled(checked)
                        }
                    }
                )
                
                // Вибрации
                SettingsSwitchItem(
                    title = "Вибрации",
                    description = "Включить тактильную обратную связь",
                    icon = Icons.Default.Vibration,
                    checked = vibrationsEnabled,
                    onCheckedChange = { checked ->
                        vibrationsEnabled = checked
                        coroutineScope.launch {
                            settingsRepository.updateVibrationEnabled(checked)
                        }
                    }
                )
                
                // Звуки
                SettingsSwitchItem(
                    title = "Звуки",
                    description = "Включить звуковые эффекты",
                    icon = Icons.Default.VolumeUp,
                    checked = soundsEnabled,
                    onCheckedChange = { checked ->
                        soundsEnabled = checked
                        coroutineScope.launch {
                            settingsRepository.updateSoundEnabled(checked)
                        }
                    }
                )
            }
            
            // Секция настроек игрового процесса
            SettingsSection(title = "Игровой процесс") {
                // Сложность
                SettingsSliderItem(
                    title = "Сложность",
                    description = "Уровень сложности игры",
                    icon = Icons.Default.Settings,
                    value = difficulty.toFloat(),
                    valueRange = 1f..5f,
                    steps = 3,
                    valueText = "${difficulty.toInt()}",
                    onValueChange = { 
                        difficulty = it.toInt()
                    },
                    onValueChangeFinished = {
                        coroutineScope.launch {
                            settingsRepository.updateSettings(
                                settings.copy(difficulty = difficulty)
                            )
                        }
                    }
                )
                
                // Размер поля
                SettingsSliderItem(
                    title = "Размер поля",
                    description = "Размер игрового поля",
                    icon = Icons.Default.Settings,
                    value = boardSize.toFloat(),
                    valueRange = 10f..20f,
                    steps = 9,
                    valueText = "${boardSize.toInt()}x${boardSize.toInt()}",
                    onValueChange = { 
                        boardSize = it.toInt()
                    },
                    onValueChangeFinished = {
                        coroutineScope.launch {
                            settingsRepository.updateSettings(
                                settings.copy(boardSize = boardSize)
                            )
                        }
                    }
                )
                
                // Начальная скорость
                SettingsSliderItem(
                    title = "Начальная скорость",
                    description = "Начальная скорость змейки",
                    icon = Icons.Default.Speed,
                    value = initialSpeed,
                    valueRange = 0.5f..2.0f,
                    steps = 5,
                    valueText = String.format("%.1f", initialSpeed),
                    onValueChange = { 
                        initialSpeed = it
                    },
                    onValueChangeFinished = {
                        coroutineScope.launch {
                            settingsRepository.updateSettings(
                                settings.copy(initialSpeedFactor = initialSpeed)
                            )
                        }
                    }
                )
                
                // Максимальное количество препятствий
                SettingsSliderItem(
                    title = "Максимум препятствий",
                    description = "Максимальное количество препятствий на поле",
                    icon = Icons.Default.Settings,
                    value = maxObstacles.toFloat(),
                    valueRange = 0f..10f,
                    steps = 9,
                    valueText = "$maxObstacles",
                    onValueChange = { 
                        maxObstacles = it.toInt()
                    },
                    onValueChangeFinished = {
                        coroutineScope.launch {
                            settingsRepository.updateSettings(
                                settings.copy(maxObstacles = maxObstacles)
                            )
                        }
                    }
                )
                
                // Частота специальной еды
                SettingsSliderItem(
                    title = "Частота спец. еды",
                    description = "Как часто появляется особая еда",
                    icon = Icons.Default.Settings,
                    value = specialFoodFrequency.toFloat(),
                    valueRange = 1f..10f,
                    steps = 8,
                    valueText = "$specialFoodFrequency",
                    onValueChange = { 
                        specialFoodFrequency = it.toInt()
                    },
                    onValueChangeFinished = {
                        coroutineScope.launch {
                            settingsRepository.updateSettings(
                                settings.copy(specialFoodFrequency = specialFoodFrequency)
                            )
                        }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNavigateBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Готово")
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            content()
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueText: String,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            onValueChangeFinished = onValueChangeFinished,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
} 