package ru.andvl.snakegame.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Game color themes for customization
 */
enum class GameColorTheme {
    CLASSIC,
    OCEAN,
    FOREST,
    SUNSET,
    NEON,
    CANDY
}

/**
 * Color palette for game elements
 */
data class GameColors(
    val snakeColor: Color,
    val foodColor: Color,
    val boardBackground: Color,
    val gridLines: Color,
    val obstacleColor: Color
)

/**
 * Predefined color themes for the game
 */
object GameColorThemes {

    val Classic = GameColors(
        snakeColor = Color(0xFF4CAF50),      // Green snake
        foodColor = Color(0xFFFF5722),        // Red food
        boardBackground = Color(0xFFF5F5F5),  // Light gray background
        gridLines = Color(0xFFE0E0E0),        // Lighter grid
        obstacleColor = Color(0xFF757575)     // Gray obstacles
    )

    val Ocean = GameColors(
        snakeColor = Color(0xFF2196F3),      // Blue snake
        foodColor = Color(0xFFFFEB3B),        // Yellow food
        boardBackground = Color(0xFFE3F2FD),  // Light blue background
        gridLines = Color(0xFFBBDEFB),        // Lighter blue grid
        obstacleColor = Color(0xFF607D8B)     // Blue gray obstacles
    )

    val Forest = GameColors(
        snakeColor = Color(0xFF8BC34A),      // Light green snake
        foodColor = Color(0xFFFF9800),        // Orange food
        boardBackground = Color(0xFFF1F8E9),  // Very light green background
        gridLines = Color(0xFFDCEDC8),        // Light green grid
        obstacleColor = Color(0xFF795548)     // Brown obstacles
    )

    val Sunset = GameColors(
        snakeColor = Color(0xFFFF5722),      // Deep orange snake
        foodColor = Color(0xFFFFEB3B),        // Yellow food
        boardBackground = Color(0xFFFFF3E0),  // Light orange background
        gridLines = Color(0xFFFFE0B2),        // Lighter orange grid
        obstacleColor = Color(0xFF5D4037)     // Dark brown obstacles
    )

    val Neon = GameColors(
        snakeColor = Color(0xFF00E676),      // Bright green snake
        foodColor = Color(0xFFFF1744),        // Bright red food
        boardBackground = Color(0xFF212121),  // Dark background
        gridLines = Color(0xFF424242),        // Dark gray grid
        obstacleColor = Color(0xFF9E9E9E)     // Gray obstacles
    )

    val Candy = GameColors(
        snakeColor = Color(0xFFE91E63),      // Pink snake
        foodColor = Color(0xFF00BCD4),        // Cyan food
        boardBackground = Color(0xFFFCE4EC),  // Light pink background
        gridLines = Color(0xFFF8BBD0),        // Light pink grid
        obstacleColor = Color(0xFF9C27B0)     // Purple obstacles
    )

    /**
     * Get color palette for a specific theme
     */
    fun getColors(theme: GameColorTheme): GameColors = when (theme) {
        GameColorTheme.CLASSIC -> Classic
        GameColorTheme.OCEAN -> Ocean
        GameColorTheme.FOREST -> Forest
        GameColorTheme.SUNSET -> Sunset
        GameColorTheme.NEON -> Neon
        GameColorTheme.CANDY -> Candy
    }
}
