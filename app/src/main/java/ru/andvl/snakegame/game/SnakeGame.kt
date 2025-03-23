package ru.andvl.snakegame.game


enum class Direction {
    UP, DOWN, LEFT, RIGHT
}

data class SnakePart(val x: Int, val y: Int)

// Типы специальной еды
enum class FoodType {
    REGULAR,    // Обычная еда
    SPEED_UP,   // Увеличивает скорость
    DOUBLE_SCORE, // Двойные очки
    SLOW_DOWN   // Замедление
}

// Препятствие на поле
data class Obstacle(val x: Int, val y: Int)

