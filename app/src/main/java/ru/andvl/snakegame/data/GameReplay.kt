package ru.andvl.snakegame.data

import kotlinx.serialization.Serializable
import ru.andvl.snakegame.game.model.Direction
import java.util.UUID

@Serializable
data class GameReplay(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long,
    val finalScore: Int,
    val maxSpeed: Float,
    val snakeLength: Int,
    val moves: List<Move>,
    val durationSeconds: Long
)

@Serializable
data class Move(
    val direction: Direction,
    val timestamp: Long
)
