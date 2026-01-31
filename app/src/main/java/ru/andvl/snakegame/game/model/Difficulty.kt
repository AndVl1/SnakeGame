package ru.andvl.snakegame.game.model

/**
 * Уровень сложности игры
 */
enum class Difficulty(val value: Int, val displayName: String) {
    VERY_EASY(1, "Very Easy"),
    EASY(2, "Easy"),
    MEDIUM(3, "Medium"),
    HARD(4, "Hard"),
    VERY_HARD(5, "Very Hard");

    companion object {
        /**
         * Получить уровень сложности по значению
         */
        fun fromValue(value: Int): Difficulty = entries.find { it.value == value } ?: MEDIUM
    }
}

/**
 * Настройки сложности игры
 */
data class DifficultySettings(
    val initialSpeed: Float,
    val maxObstacles: Int,
    val specialFoodFrequency: Float
) {
    companion object {
        /**
         * Получить настройки для заданного уровня сложности
         */
        fun forDifficulty(difficulty: Difficulty): DifficultySettings = when (difficulty) {
            Difficulty.VERY_EASY -> DifficultySettings(
                initialSpeed = 0.7f,
                maxObstacles = 2,
                specialFoodFrequency = 0.15f
            )
            Difficulty.EASY -> DifficultySettings(
                initialSpeed = 0.85f,
                maxObstacles = 3,
                specialFoodFrequency = 0.12f
            )
            Difficulty.MEDIUM -> DifficultySettings(
                initialSpeed = 1.0f,
                maxObstacles = 5,
                specialFoodFrequency = 0.10f
            )
            Difficulty.HARD -> DifficultySettings(
                initialSpeed = 1.2f,
                maxObstacles = 7,
                specialFoodFrequency = 0.08f
            )
            Difficulty.VERY_HARD -> DifficultySettings(
                initialSpeed = 1.5f,
                maxObstacles = 10,
                specialFoodFrequency = 0.05f
            )
        }
    }
}
