package ru.andvl.snakegame.game.model

/**
 * Представляет позицию на игровом поле
 */
data class GridPosition(val x: Int, val y: Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GridPosition) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }
} 
