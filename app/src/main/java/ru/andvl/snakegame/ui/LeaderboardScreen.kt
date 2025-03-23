package ru.andvl.snakegame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.andvl.snakegame.R
import ru.andvl.snakegame.data.PlayerScore

/**
 * Экран с таблицей лидеров
 */
@Composable
fun LeaderboardScreen(
    scores: List<PlayerScore>,
    onStartGameClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            text = stringResource(R.string.leaderboard_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        
        // Таблица рекордов
        Card(
            modifier = Modifier.weight(1f)
        ) {
            if (scores.isEmpty()) {
                // Если рекордов нет
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_records),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(R.string.no_records_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Заголовок таблицы
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.player_column),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(2f)
                        )
                        Text(
                            text = stringResource(R.string.score_column),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = stringResource(R.string.speed_column),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                    }
                    
                    // Список рекордов
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(scores.sortedByDescending { it.score }) { score ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = score.playerName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    text = score.score.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                Text(
                                    text = "x${String.format("%.1f", score.speedFactor)}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Кнопки действий
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onStartGameClick,
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Text(text = stringResource(R.string.play_button))
                }
            }
            
            Button(
                onClick = onSettingsClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                    Text(text = stringResource(R.string.settings_button))
                }
            }
        }
    }
}
