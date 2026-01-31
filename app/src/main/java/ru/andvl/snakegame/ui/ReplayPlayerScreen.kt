package ru.andvl.snakegame.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.andvl.snakegame.decompose.game.GameUiState
import ru.andvl.snakegame.game.Obstacle
import ru.andvl.snakegame.game.model.GameModelConverter
import ru.andvl.snakegame.game.ui.GameBoard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplayPlayerScreen(
    gameUiState: GameUiState,
    isPlaying: Boolean,
    isPaused: Boolean,
    playbackSpeed: Float,
    currentProgress: Float,
    isLoading: Boolean,
    onPlayClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResumeClick: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Replay Player") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Game board
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    val displayFood = gameUiState.food?.let {
                        GameModelConverter.convertFoodToDisplayGameFood(it)
                    }

                    GameBoard(
                        snakeParts = GameModelConverter.convertNewSnakePartsToOldSnakeParts(gameUiState.snakeParts),
                        food = displayFood,
                        obstacles = gameUiState.obstacles.map {
                            Obstacle(it.position.x, it.position.y)
                        },
                        isGameOver = gameUiState.deathAnimationActive,
                        doubleScoreActive = gameUiState.doubleScoreActive,
                        pulsatingSpeedActive = gameUiState.pulsatingSpeedActive,
                        swipeSensitivity = 1.0f,
                        onDirectionChange = { /* No-op in replay mode */ },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Score overlay
                    Text(
                        text = "Score: ${gameUiState.score}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(16.dp)
                    )
                }

                // Progress bar
                LinearProgressIndicator(
                    progress = currentProgress,
                    modifier = Modifier.fillMaxWidth()
                )

                // Playback controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Play/Pause button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!isPlaying && !isPaused) {
                            Button(onClick = onPlayClick) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Text("Play", modifier = Modifier.padding(start = 8.dp))
                            }
                        } else if (isPlaying) {
                            Button(onClick = onPauseClick) {
                                Icon(Icons.Default.Pause, contentDescription = null)
                                Text("Pause", modifier = Modifier.padding(start = 8.dp))
                            }
                        } else if (isPaused) {
                            Button(onClick = onResumeClick) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Text("Resume", modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }

                    // Playback speed controls
                    Text(
                        text = "Playback Speed: ${playbackSpeed}x",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf(0.5f, 1.0f, 2.0f, 4.0f).forEach { speed ->
                            TextButton(
                                onClick = { onSpeedChange(speed) },
                                enabled = !isPlaying || playbackSpeed != speed
                            ) {
                                Text("${speed}x")
                            }
                        }
                    }
                }
            }
        }
    }
}
