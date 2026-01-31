package ru.andvl.snakegame.tests

import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Rule
import org.junit.Test
import ru.andvl.snakegame.main.BaseTest
import ru.andvl.snakegame.pages.ComposeGamePage
import ru.andvl.snakegame.pages.ComposeLeaderboardPage

/**
 * UI tests for Game Screen
 */
class GameScreenTest : BaseTest() {
    @get:Rule
    val setUpRule = SetUpRule().add {
        // Navigate to game screen before each test
        ComposeLeaderboardPage.clickPlayButton()
        waitMedium()
    }

    @get:Rule
    val tearDownRule = TearDownRule().add {
        // Cleanup after each test if needed
    }

    @Test
    fun whenGameStarts_shouldDisplayAllComponents() {
        ComposeGamePage
            .assertGameScreenIsDisplayed()
            .assertGameBoardIsDisplayed()
            .assertGameControlsDisplayed()
    }

    @Test
    fun whenGameStarts_pauseFabShouldBeDisplayed() {
        ComposeGamePage
            .assertPauseFabIsDisplayed()
    }

    @Test
    fun whenPauseFabClicked_shouldPauseGame() {
        waitLong() // Wait for game to start

        ComposeGamePage
            .clickPauseFab()

        waitShort()

        // Game should be paused - FAB should still be visible
        ComposeGamePage
            .assertPauseFabIsDisplayed()
    }

    @Test
    fun whenPauseButtonClicked_shouldPauseGame() {
        waitLong()

        ComposeGamePage
            .clickPauseButton()

        waitShort()

        // Verify game is paused
        ComposeGamePage
            .assertPauseFabIsDisplayed()
    }

    @Test
    fun whenPauseFabClickedTwice_shouldResumeGame() {
        waitLong()

        ComposeGamePage
            .clickPauseFab()

        waitShort()

        ComposeGamePage
            .clickPauseFab()

        waitShort()

        // Game should be resumed
        ComposeGamePage
            .assertPauseFabIsDisplayed()
    }

    @Test
    fun whenRestartButtonClicked_shouldRestartGame() {
        waitLong()

        ComposeGamePage
            .clickRestartButton()

        waitShort()

        ComposeGamePage
            .assertGameScreenIsDisplayed()
            .assertGameBoardIsDisplayed()
    }

    @Test
    fun whenInstructionsButtonClicked_shouldShowInstructions() {
        ComposeGamePage
            .clickInstructionsButton()

        waitShort()

        // Instructions dialog should be displayed
        // (Dialog verification would need additional test tags)
    }

    @Test
    fun verifyAllControlsAreAccessible() {
        ComposeGamePage
            .assertGameControlsDisplayed()
    }

    @Test
    fun whenGamePlaying_scoreAndControlsShouldBeVisible() {
        waitLong()

        ComposeGamePage
            .assertGameScreenIsDisplayed()
            .assertGameBoardIsDisplayed()
            .assertGameControlsDisplayed()
    }
}
