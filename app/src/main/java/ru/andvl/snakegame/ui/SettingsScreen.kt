package ru.andvl.snakegame.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.andvl.snakegame.R
import ru.andvl.snakegame.game.model.Difficulty
import ru.andvl.snakegame.main.TestTags

/**
 * Экран настроек
 */
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    soundsEnabled: Boolean = true,
    vibrationsEnabled: Boolean = true,
    difficulty: Int = 3,
    swipeSensitivity: Float = 1.0f,
    onThemeToggled: () -> Unit,
    onSoundsToggled: (() -> Unit)? = null,
    onVibrationsToggled: (() -> Unit)? = null,
    onDifficultySelected: ((Int) -> Unit)? = null,
    onSwipeSensitivityChanged: ((Float) -> Unit)? = null,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag(TestTags.SETTINGS_SCREEN)
    ) {
        // Заголовок с кнопкой назад
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.testTag(TestTags.SETTINGS_BACK_BUTTON)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
            
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Настройки
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .testTag(TestTags.SETTINGS_CARD)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Переключатель темы
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.dark_theme),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.dark_theme_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onThemeToggled() },
                        modifier = Modifier.testTag(TestTags.SETTINGS_THEME_SWITCH)
                    )
                }

                // Переключатель звуков
                onSoundsToggled?.let { callback ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.sound_effects),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.sound_effects_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = soundsEnabled,
                            onCheckedChange = { callback() }
                        )
                    }
                }

                // Переключатель вибрации
                onVibrationsToggled?.let { callback ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.haptic_feedback),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = stringResource(R.string.haptic_feedback_description),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Switch(
                            checked = vibrationsEnabled,
                            onCheckedChange = { callback() }
                        )
                    }
                }

                // Селектор сложности
                onDifficultySelected?.let { callback ->
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.difficulty_level),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.difficulty_level_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Селектор уровня сложности
                        DifficultySelector(
                            selectedDifficulty = difficulty,
                            onDifficultySelected = callback,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Слайдер чувствительности свайпов
                onSwipeSensitivityChanged?.let { callback ->
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(R.string.swipe_sensitivity),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.swipe_sensitivity_description),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.sensitivity_low),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.sensitivity_very_high),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Slider(
                            value = swipeSensitivity,
                            onValueChange = { callback(it) },
                            valueRange = 0.5f..2.0f,
                            steps = 5,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text(
                            text = when {
                                swipeSensitivity < 0.75f -> stringResource(R.string.sensitivity_low)
                                swipeSensitivity < 1.25f -> stringResource(R.string.sensitivity_medium)
                                swipeSensitivity < 1.75f -> stringResource(R.string.sensitivity_high)
                                else -> stringResource(R.string.sensitivity_very_high)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Компонент для выбора уровня сложности
 */
@Composable
private fun DifficultySelector(
    selectedDifficulty: Int,
    onDifficultySelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Difficulty.entries.forEach { difficulty ->
            DifficultyButton(
                difficulty = difficulty,
                isSelected = selectedDifficulty == difficulty.value,
                onClick = { onDifficultySelected(difficulty.value) }
            )
        }
    }
}

/**
 * Кнопка уровня сложности
 */
@Composable
private fun DifficultyButton(
    difficulty: Difficulty,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        ),
        border = BorderStroke(
            width = 2.dp,
            color = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(getDifficultyNameRes(difficulty)),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = stringResource(getDifficultyDescriptionRes(difficulty)),
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * Получить ресурс названия уровня сложности
 */
private fun getDifficultyNameRes(difficulty: Difficulty): Int = when (difficulty) {
    Difficulty.VERY_EASY -> R.string.difficulty_very_easy
    Difficulty.EASY -> R.string.difficulty_easy
    Difficulty.MEDIUM -> R.string.difficulty_medium
    Difficulty.HARD -> R.string.difficulty_hard
    Difficulty.VERY_HARD -> R.string.difficulty_very_hard
}

/**
 * Получить ресурс описания уровня сложности
 */
private fun getDifficultyDescriptionRes(difficulty: Difficulty): Int = when (difficulty) {
    Difficulty.VERY_EASY -> R.string.difficulty_very_easy_desc
    Difficulty.EASY -> R.string.difficulty_easy_desc
    Difficulty.MEDIUM -> R.string.difficulty_medium_desc
    Difficulty.HARD -> R.string.difficulty_hard_desc
    Difficulty.VERY_HARD -> R.string.difficulty_very_hard_desc
}
