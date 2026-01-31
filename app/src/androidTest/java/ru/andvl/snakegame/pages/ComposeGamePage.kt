package ru.andvl.snakegame.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertIsNotDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import ru.andvl.snakegame.main.TestTags

object ComposeGamePage : Page<ComposeGamePage>() {
    private val gameScreen = hasTestTag(TestTags.GAME_SCREEN)
    private val gameScore = hasTestTag(TestTags.GAME_SCORE)
    private val gameControls = hasTestTag(TestTags.GAME_CONTROLS)
    private val gameBoard = hasTestTag(TestTags.GAME_BOARD)
    private val gamePauseFab = hasTestTag(TestTags.GAME_PAUSE_FAB)
    private val gamePauseButton = hasTestTag(TestTags.GAME_PAUSE_BUTTON)
    private val gameRestartButton = hasTestTag(TestTags.GAME_RESTART_BUTTON)
    private val gameInstructionsButton = hasTestTag(TestTags.GAME_INSTRUCTIONS_BUTTON)
    private val saveScoreDialog = hasTestTag(TestTags.SAVE_SCORE_DIALOG)
    private val saveScoreNameInput = hasTestTag(TestTags.SAVE_SCORE_NAME_INPUT)
    private val saveScoreSaveButton = hasTestTag(TestTags.SAVE_SCORE_SAVE_BUTTON)
    private val saveScoreCancelButton = hasTestTag(TestTags.SAVE_SCORE_CANCEL_BUTTON)

    fun assertGameScreenIsDisplayed() = apply {
        step("Verify game screen is displayed") {
            gameScreen.assertIsDisplayed()
            gameScore.assertIsDisplayed()
            gameControls.assertIsDisplayed()
        }
    }

    fun assertGameBoardIsDisplayed() = apply {
        step("Verify game board is displayed") {
            gameBoard.assertIsDisplayed()
        }
    }

    fun clickPauseFab() = apply {
        step("Click pause FAB") {
            gamePauseFab.click()
        }
    }

    fun clickPauseButton() = apply {
        step("Click pause button in controls") {
            gamePauseButton.click()
        }
    }

    fun clickRestartButton() = apply {
        step("Click restart button") {
            gameRestartButton.click()
        }
    }

    fun clickInstructionsButton() = apply {
        step("Click instructions button") {
            gameInstructionsButton.click()
        }
    }

    fun assertPauseFabIsDisplayed() = apply {
        step("Verify pause FAB is displayed") {
            gamePauseFab.assertIsDisplayed()
        }
    }

    fun assertPauseFabIsNotDisplayed() = apply {
        step("Verify pause FAB is not displayed") {
            gamePauseFab.assertIsNotDisplayed()
        }
    }

    fun assertSaveScoreDialogIsDisplayed() = apply {
        step("Verify save score dialog is displayed") {
            saveScoreDialog.assertIsDisplayed()
            saveScoreNameInput.assertIsDisplayed()
            saveScoreSaveButton.assertIsDisplayed()
            saveScoreCancelButton.assertIsDisplayed()
        }
    }

    fun assertSaveScoreDialogIsNotDisplayed() = apply {
        step("Verify save score dialog is not displayed") {
            saveScoreDialog.assertIsNotDisplayed()
        }
    }

    fun clickSaveScoreSaveButton() = apply {
        step("Click save button in save score dialog") {
            saveScoreSaveButton.click()
        }
    }

    fun clickSaveScoreCancelButton() = apply {
        step("Click cancel button in save score dialog") {
            saveScoreCancelButton.click()
        }
    }

    fun assertGameControlsDisplayed() = apply {
        step("Verify all game controls are displayed") {
            gameControls.assertIsDisplayed()
            gamePauseButton.assertIsDisplayed()
            gameRestartButton.assertIsDisplayed()
            gameInstructionsButton.assertIsDisplayed()
        }
    }
}
