package ru.andvl.snakegame.decompose.statistics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.andvl.snakegame.R
import ru.andvl.snakegame.data.GameStatistics
import java.text.NumberFormat
import java.util.Locale

/**
 * Содержимое экрана статистики
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsContent(
    statistics: GameStatistics,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.statistics_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.reset_statistics))
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // Общая статистика
            item {
                StatisticsSection(
                    title = stringResource(R.string.general_statistics)
                ) {
                    StatCard(
                        icon = Icons.Default.PlayArrow,
                        label = stringResource(R.string.games_played),
                        value = formatNumber(statistics.totalGamesPlayed)
                    )
                    StatCard(
                        icon = Icons.Default.Star,
                        label = stringResource(R.string.total_score),
                        value = formatNumber(statistics.totalScore)
                    )
                    StatCard(
                        icon = Icons.Default.DateRange,
                        label = stringResource(R.string.total_play_time),
                        value = formatTime(statistics.totalPlayTimeSeconds)
                    )
                }
            }

            // Рекорды
            item {
                StatisticsSection(
                    title = stringResource(R.string.records_section)
                ) {
                    StatCard(
                        icon = Icons.Default.Star,
                        label = stringResource(R.string.highest_score),
                        value = formatNumber(statistics.highestScore),
                        highlighted = true
                    )
                    StatCard(
                        icon = Icons.Default.Info,
                        label = stringResource(R.string.longest_snake),
                        value = formatNumber(statistics.longestSnake),
                        highlighted = true
                    )
                    StatCard(
                        icon = Icons.Default.Done,
                        label = stringResource(R.string.fastest_speed),
                        value = "x${String.format(Locale.US, "%.1f", statistics.fastestSpeed)}",
                        highlighted = true
                    )
                }
            }

            // Статистика еды
            item {
                StatisticsSection(
                    title = stringResource(R.string.food_statistics)
                ) {
                    StatCard(
                        icon = Icons.Default.Clear,
                        label = stringResource(R.string.regular_food_eaten),
                        value = formatNumber(statistics.regularFoodEaten)
                    )
                    StatCard(
                        icon = Icons.Default.Add,
                        label = stringResource(R.string.special_food_eaten),
                        value = formatNumber(statistics.specialFoodEaten)
                    )
                    StatCard(
                        icon = Icons.Default.Call,
                        label = stringResource(R.string.total_food_eaten),
                        value = formatNumber(statistics.regularFoodEaten + statistics.specialFoodEaten)
                    )
                }
            }

            // Средние показатели
            item {
                StatisticsSection(
                    title = stringResource(R.string.averages_section)
                ) {
                    StatCard(
                        icon = Icons.Default.DateRange,
                        label = stringResource(R.string.average_game_duration),
                        value = formatTime(statistics.averageGameDuration)
                    )
                    val avgScore = if (statistics.totalGamesPlayed > 0) {
                        statistics.totalScore / statistics.totalGamesPlayed
                    } else 0
                    StatCard(
                        icon = Icons.Default.Star,
                        label = stringResource(R.string.average_score),
                        value = formatNumber(avgScore)
                    )
                }
            }
        }
    }

    // Диалог подтверждения сброса
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = stringResource(R.string.reset_statistics_title)) },
            text = { Text(text = stringResource(R.string.reset_statistics_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        onResetClick()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = stringResource(R.string.reset))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun StatisticsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (highlighted) {
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlighted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (highlighted) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                color = if (highlighted) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

/**
 * Форматирует число с разделителями тысяч
 */
private fun formatNumber(number: Int): String {
    return NumberFormat.getNumberInstance(Locale.getDefault()).format(number)
}

/**
 * Форматирует время в секундах как MM:SS
 */
private fun formatTime(seconds: Long): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, secs)
}
