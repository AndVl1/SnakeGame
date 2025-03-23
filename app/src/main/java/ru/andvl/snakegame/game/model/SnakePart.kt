package ru.andvl.snakegame.game.model

/**
 * Представляет часть тела змейки на игровом поле
 */
data class SnakePart(val x: Int, val y: Int) {
    // Конвертация в GridPosition
    fun toGridPosition(): GridPosition = GridPosition(x, y)
    
    companion object {
        // Создание SnakePart из GridPosition
        fun fromGridPosition(position: GridPosition): SnakePart = 
            SnakePart(position.x, position.y)
    }
} 
