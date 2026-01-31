package ru.andvl.snakegame.main

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page

object ComposeMainPage : Page<ComposeMainPage>() {
    val noRecordsContent = hasTestTag(TestTags.NO_RECORDS_CONTENT)
    val recordsContent = hasTestTag(TestTags.RECORDS_CONTENT)
    val playButton = hasTestTag(TestTags.PLAY_BUTTON)
    val settingsButton = hasTestTag(TestTags.SETTINGS_BUTTON)

    fun assertNoRecordIsDisplayed() = apply {
        step("initial content is displayed") {
            noRecordsContent.assertIsDisplayed()
        }
    }

    fun assertRecordsContentIsDisplayed() = apply {
        step("records content is displayed") {
            recordsContent.assertIsDisplayed()
        }
    }

    fun startGame() = apply {
        step("press start game button") {
            playButton.click()
        }
    }

    fun openSettings() = apply {
        step("press settings button") {
            settingsButton.click()
        }
    }
}
