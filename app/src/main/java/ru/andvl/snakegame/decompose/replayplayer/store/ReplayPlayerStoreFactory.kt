package ru.andvl.snakegame.decompose.replayplayer.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.andvl.snakegame.data.GameReplay
import ru.andvl.snakegame.data.Move
import ru.andvl.snakegame.data.ReplayRepository
import ru.andvl.snakegame.decompose.game.GameUiState
import ru.andvl.snakegame.game.model.Direction
import ru.andvl.snakegame.game.model.Food
import ru.andvl.snakegame.game.model.FoodType
import ru.andvl.snakegame.game.model.GameConstants
import ru.andvl.snakegame.game.model.GridPosition
import ru.andvl.snakegame.game.model.SnakePart
import kotlin.random.Random

/**
 * Фабрика для создания ReplayPlayerStore
 */
class ReplayPlayerStoreFactory(
    private val storeFactory: StoreFactory,
    private val replayRepository: ReplayRepository
) {
    fun create(): ReplayPlayerStore =
        object : ReplayPlayerStore, Store<ReplayPlayerIntent, ReplayPlayerState, ReplayPlayerLabel> by storeFactory.create(
            name = "ReplayPlayerStore",
            initialState = ReplayPlayerState(),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = { createExecutor() },
            reducer = ReducerImpl
        ) {}

    private fun createExecutor() = object : CoroutineExecutor<ReplayPlayerIntent, Unit, ReplayPlayerState, Result, ReplayPlayerLabel>(
        Dispatchers.Main
    ) {
        private var playbackJob: Job? = null
        private val boardSize = GameConstants.BOARD_SIZE
        private val initialSnakeLength = 3

        override fun executeAction(action: Unit, getState: () -> ReplayPlayerState) {
            // No actions to handle
        }

        override fun executeIntent(intent: ReplayPlayerIntent, getState: () -> ReplayPlayerState) {
            when (intent) {
                is ReplayPlayerIntent.LoadReplay -> loadReplay(intent.replayId)
                is ReplayPlayerIntent.Play -> playReplay(getState())
                is ReplayPlayerIntent.Pause -> pauseReplay()
                is ReplayPlayerIntent.Resume -> resumeReplay(getState())
                is ReplayPlayerIntent.SetPlaybackSpeed -> setPlaybackSpeed(intent.speed)
                is ReplayPlayerIntent.NavigateBack -> publish(ReplayPlayerLabel.NavigateBack)
            }
        }

        private fun loadReplay(replayId: String) {
            scope.launch {
                dispatch(Result.Loading)
                try {
                    val replay = withContext(Dispatchers.IO) {
                        replayRepository.getReplay(replayId)
                    }
                    if (replay != null) {
                        dispatch(Result.ReplayLoaded(replay))
                    } else {
                        dispatch(Result.Error("Replay not found"))
                        publish(ReplayPlayerLabel.ShowMessage("Replay not found"))
                    }
                } catch (e: Exception) {
                    dispatch(Result.Error(e.message ?: "Failed to load replay"))
                    publish(ReplayPlayerLabel.ShowMessage(e.message ?: "Failed to load replay"))
                }
            }
        }

        private fun playReplay(state: ReplayPlayerState) {
            val replay = state.replay ?: return

            playbackJob?.cancel()
            dispatch(Result.PlaybackStarted)

            playbackJob = scope.launch {
                // Initialize snake in center
                val centerY = boardSize / 2
                val startX = boardSize / 2
                val snakeParts = mutableListOf<SnakePart>()
                for (i in 0 until initialSnakeLength) {
                    snakeParts.add(SnakePart(startX - i, centerY))
                }

                var currentDirection = Direction.RIGHT
                var moveIndex = 0
                val startTime = System.currentTimeMillis()

                // Spawn initial food
                val food = spawnFood(snakeParts, emptyList())

                // Initial UI state
                dispatch(Result.UpdateGameUiState(
                    GameUiState(
                        snakeParts = snakeParts.toList(),
                        food = food,
                        score = 0
                    )
                ))

                // Play through each move
                while (moveIndex < replay.moves.size) {
                    val playbackSpeed = state.playbackSpeed
                    val nextMove = replay.moves[moveIndex]

                    // Wait until it's time for the next move (accounting for playback speed)
                    val targetTime = (nextMove.timestamp / playbackSpeed).toLong()
                    val elapsedTime = System.currentTimeMillis() - startTime

                    if (elapsedTime < targetTime) {
                        delay(targetTime - elapsedTime)
                    }

                    // Update direction
                    currentDirection = nextMove.direction

                    // Calculate progress
                    val progress = nextMove.timestamp.toFloat() / (replay.durationSeconds * 1000)
                    dispatch(Result.UpdateProgress(progress))

                    moveIndex++
                }

                // Playback finished
                dispatch(Result.PlaybackFinished)
                publish(ReplayPlayerLabel.ShowMessage("Replay finished"))
            }
        }

        private fun pauseReplay() {
            playbackJob?.cancel()
            dispatch(Result.PlaybackPaused)
        }

        private fun resumeReplay(state: ReplayPlayerState) {
            // Resume from current progress
            playReplay(state)
        }

        private fun setPlaybackSpeed(speed: Float) {
            dispatch(Result.PlaybackSpeedChanged(speed))
        }

        private fun spawnFood(snakeParts: List<SnakePart>, obstacles: List<GridPosition>): Food {
            val availablePositions = mutableListOf<GridPosition>()

            for (x in 0 until boardSize) {
                for (y in 0 until boardSize) {
                    val position = GridPosition(x, y)
                    val positionFree = !snakeParts.any { it.x == position.x && it.y == position.y } &&
                            !obstacles.any { it.x == position.x && it.y == position.y }
                    if (positionFree) {
                        availablePositions.add(position)
                    }
                }
            }

            return if (availablePositions.isNotEmpty()) {
                Food(availablePositions.random(), FoodType.REGULAR)
            } else {
                Food(GridPosition(0, 0), FoodType.REGULAR)
            }
        }
    }

    private sealed interface Result {
        data object Loading : Result
        data class ReplayLoaded(val replay: GameReplay) : Result
        data object PlaybackStarted : Result
        data object PlaybackPaused : Result
        data object PlaybackFinished : Result
        data class PlaybackSpeedChanged(val speed: Float) : Result
        data class UpdateGameUiState(val gameUiState: GameUiState) : Result
        data class UpdateProgress(val progress: Float) : Result
        data class Error(val message: String) : Result
    }

    private object ReducerImpl : Reducer<ReplayPlayerState, Result> {
        override fun ReplayPlayerState.reduce(result: Result): ReplayPlayerState =
            when (result) {
                is Result.Loading -> copy(isLoading = true, error = null)
                is Result.ReplayLoaded -> copy(replay = result.replay, isLoading = false, error = null)
                is Result.PlaybackStarted -> copy(isPlaying = true, isPaused = false)
                is Result.PlaybackPaused -> copy(isPlaying = false, isPaused = true)
                is Result.PlaybackFinished -> copy(isPlaying = false, isPaused = false, currentProgress = 1.0f)
                is Result.PlaybackSpeedChanged -> copy(playbackSpeed = result.speed)
                is Result.UpdateGameUiState -> copy(gameUiState = result.gameUiState)
                is Result.UpdateProgress -> copy(currentProgress = result.progress)
                is Result.Error -> copy(error = result.message, isLoading = false)
            }
    }
}
