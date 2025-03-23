package ru.andvl.sample.game.model

/**
 * Типы еды в игре
 */
enum class FoodType {
    REGULAR,      // Обычная еда
    DOUBLE_SCORE, // Удваивает очки на время
    SPEED_BOOST   // Временно увеличивает скорость
}

/**
 * Представляет еду на игровом поле
 */
data class Food(
    val position: GridPosition,
    val type: FoodType = FoodType.REGULAR
) 