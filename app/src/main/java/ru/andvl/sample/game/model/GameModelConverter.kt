package ru.andvl.sample.game.model

import androidx.compose.ui.geometry.Offset
import ru.andvl.sample.game.Direction as OldDirection
import ru.andvl.sample.game.FoodType as OldFoodType
import ru.andvl.sample.game.Obstacle as OldObstacle
import ru.andvl.sample.game.SnakePart as OldSnakePart
import ru.andvl.sample.game.model.Direction as NewDirection
import ru.andvl.sample.game.model.Food
import ru.andvl.sample.game.model.FoodType as NewFoodType
import ru.andvl.sample.game.model.GridPosition
import ru.andvl.sample.game.model.SnakePart as NewSnakePart

/**
 * Класс, представляющий еду в игре для отображения на UI
 * Позиция хранится в абсолютных координатах (пикселях)
 */
data class DisplayGameFood(
    val position: Offset,
    val type: OldFoodType
)

/**
 * Класс для конвертации между моделями старого и нового форматов
 */
object GameModelConverter {

    // ФИКСИРОВАННЫЙ размер ячейки для всех расчетов координат
    const val CELL_SIZE = 32f

    /**
     * Конвертирует Direction из старого формата в новый
     */
    fun convertDirection(direction: OldDirection): NewDirection {
        return when (direction) {
            OldDirection.UP -> NewDirection.UP
            OldDirection.DOWN -> NewDirection.DOWN
            OldDirection.LEFT -> NewDirection.LEFT
            OldDirection.RIGHT -> NewDirection.RIGHT
        }
    }

    /**
     * Конвертирует Direction из нового формата в старый
     */
    fun convertDirection(direction: NewDirection): OldDirection {
        return when (direction) {
            NewDirection.UP -> OldDirection.UP
            NewDirection.DOWN -> OldDirection.DOWN
            NewDirection.LEFT -> OldDirection.LEFT
            NewDirection.RIGHT -> OldDirection.RIGHT
        }
    }

    /**
     * Конвертирует FoodType из старого формата в новый
     */
    fun convertFoodType(foodType: OldFoodType): NewFoodType {
        return when (foodType) {
            OldFoodType.REGULAR -> NewFoodType.REGULAR
            OldFoodType.DOUBLE_SCORE -> NewFoodType.DOUBLE_SCORE
            OldFoodType.SPEED_UP -> NewFoodType.SPEED_BOOST
            OldFoodType.SLOW_DOWN -> NewFoodType.SPEED_BOOST // В новом формате нет SLOW_DOWN, используем SPEED_BOOST
        }
    }

    /**
     * Конвертирует FoodType из нового формата в старый
     */
    fun convertFoodType(foodType: NewFoodType): OldFoodType {
        return when (foodType) {
            NewFoodType.REGULAR -> OldFoodType.REGULAR
            NewFoodType.DOUBLE_SCORE -> OldFoodType.DOUBLE_SCORE
            NewFoodType.SPEED_BOOST -> OldFoodType.SPEED_UP
        }
    }

    /**
     * Конвертирует GridPosition в SnakePart старого формата
     */
    fun convertGridPositionToSnakePart(position: GridPosition): OldSnakePart {
        return OldSnakePart(position.x, position.y)
    }

    /**
     * Конвертирует список GridPosition в список SnakePart старого формата
     */
    fun convertGridPositionsToSnakeParts(positions: List<GridPosition>): List<OldSnakePart> {
        return positions.map { convertGridPositionToSnakePart(it) }
    }

    /**
     * Конвертирует SnakePart старого формата в GridPosition
     */
    fun convertSnakePartToGridPosition(snakePart: OldSnakePart): GridPosition {
        return GridPosition(snakePart.x, snakePart.y)
    }

    /**
     * Конвертирует список SnakePart старого формата в список GridPosition
     */
    fun convertSnakePartsToGridPositions(snakeParts: List<OldSnakePart>): List<GridPosition> {
        return snakeParts.map { convertSnakePartToGridPosition(it) }
    }

    /**
     * Конвертирует список Obstacle старого формата в список Obstacle нового формата (GridPosition)
     */
    fun convertObstaclesToGridPositions(obstacles: List<OldObstacle>): List<GridPosition> {
        return obstacles.map { GridPosition(it.x, it.y) }
    }

    /**
     * Конвертирует список GridPosition в список Obstacle старого формата
     */
    fun convertGridPositionsToObstacles(positions: List<GridPosition>): List<OldObstacle> {
        return positions.map { OldObstacle(it.x, it.y) }
    }

    /**
     * Конвертирует Food в GameFood для UI
     * Преобразует ЛОГИЧЕСКИЕ координаты в ПИКСЕЛЬНЫЕ
     */
    fun convertFoodToDisplayGameFood(food: Food): DisplayGameFood {
        val gridX = food.position.x
        val gridY = food.position.y
        
        // Преобразуем координаты сетки в пиксельные координаты
        // Умножаем координаты на размер ячейки и добавляем половину для центрирования
        val pixelX = gridX * CELL_SIZE + (CELL_SIZE / 2)
        val pixelY = gridY * CELL_SIZE + (CELL_SIZE / 2)
        
        return DisplayGameFood(Offset(pixelX, pixelY), convertFoodType(food.type))
    }

    /**
     * Конвертирует GameFood в Food
     * Преобразует ПИКСЕЛЬНЫЕ координаты в ЛОГИЧЕСКИЕ
     */
    fun convertGameFoodToFood(gameFood: DisplayGameFood): Food {
        val pixelX = gameFood.position.x
        val pixelY = gameFood.position.y
        
        // Преобразуем пиксельные координаты в координаты сетки
        // Делим координаты на размер ячейки и округляем
        val gridX = (pixelX / CELL_SIZE).toInt()
        val gridY = (pixelY / CELL_SIZE).toInt()
        
        return Food(GridPosition(gridX, gridY), convertFoodType(gameFood.type))
    }

    /**
     * Конвертирует список препятствий старого формата для UI
     */
    fun convertObstacles(obstacles: List<OldObstacle>): List<OldObstacle> {
        return obstacles
    }

    /**
     * Конвертирует SnakePart нового формата в SnakePart старого формата
     */
    fun convertNewSnakePartToOldSnakePart(snakePart: NewSnakePart): OldSnakePart {
        return OldSnakePart(snakePart.x, snakePart.y)
    }

    /**
     * Конвертирует список SnakePart нового формата в список SnakePart старого формата
     */
    fun convertNewSnakePartsToOldSnakeParts(snakeParts: List<NewSnakePart>): List<OldSnakePart> {
        return snakeParts.map { convertNewSnakePartToOldSnakePart(it) }
    }
} 