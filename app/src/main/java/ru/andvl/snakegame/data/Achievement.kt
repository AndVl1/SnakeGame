package ru.andvl.snakegame.data

import kotlinx.serialization.Serializable

/**
 * Achievement types
 */
enum class AchievementType {
    FIRST_SCORE,        // First score of any value
    SCORE_100,          // Reach 100 points
    SCORE_500,          // Reach 500 points
    SPEED_DEMON,        // Reach 2.5x speed
    MARATHON,           // Snake length 50+
    OBSTACLE_MASTER,    // Complete 10 games with obstacles
    FOOD_COLLECTOR,     // Eat 100 special food items
    QUICK_START,        // Reach 50 points in first minute
    SURVIVOR,           // Survive 5 minutes in one game
    PERFECT_START       // Don't die for first 10 games
}

/**
 * Achievement data model
 */
@Serializable
data class Achievement(
    val type: AchievementType,
    val name: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,  // Timestamp when unlocked
    val progress: Int = 0,          // For progressive achievements
    val maxProgress: Int = 1        // Maximum progress needed
) {
    val progressPercentage: Float
        get() = if (maxProgress > 0) (progress.toFloat() / maxProgress) * 100 else 0f

    companion object {
        /**
         * Get all available achievements with default values
         */
        fun getAllAchievements(): List<Achievement> = listOf(
            Achievement(
                type = AchievementType.FIRST_SCORE,
                name = "First Steps",
                description = "Score your first points",
                maxProgress = 1
            ),
            Achievement(
                type = AchievementType.SCORE_100,
                name = "Century",
                description = "Reach 100 points",
                maxProgress = 100
            ),
            Achievement(
                type = AchievementType.SCORE_500,
                name = "High Scorer",
                description = "Reach 500 points",
                maxProgress = 500
            ),
            Achievement(
                type = AchievementType.SPEED_DEMON,
                name = "Speed Demon",
                description = "Reach 2.5x speed multiplier",
                maxProgress = 25  // 2.5x * 10 for easier tracking
            ),
            Achievement(
                type = AchievementType.MARATHON,
                name = "Marathon Runner",
                description = "Grow your snake to length 50+",
                maxProgress = 50
            ),
            Achievement(
                type = AchievementType.OBSTACLE_MASTER,
                name = "Obstacle Master",
                description = "Complete 10 games with obstacles",
                maxProgress = 10
            ),
            Achievement(
                type = AchievementType.FOOD_COLLECTOR,
                name = "Food Collector",
                description = "Eat 100 special food items",
                maxProgress = 100
            ),
            Achievement(
                type = AchievementType.QUICK_START,
                name = "Quick Start",
                description = "Reach 50 points in first minute",
                maxProgress = 1
            ),
            Achievement(
                type = AchievementType.SURVIVOR,
                name = "Survivor",
                description = "Survive for 5 minutes in one game",
                maxProgress = 1
            ),
            Achievement(
                type = AchievementType.PERFECT_START,
                name = "Perfect Start",
                description = "Complete your first 10 games",
                maxProgress = 10
            )
        )
    }
}

/**
 * Achievement progress tracker
 */
@Serializable
data class AchievementProgress(
    val achievements: Map<AchievementType, Achievement> = Achievement.getAllAchievements()
        .associateBy { it.type },
    val totalGamesPlayed: Int = 0,
    val specialFoodEaten: Int = 0
)
