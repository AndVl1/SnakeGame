package ru.andvl.snakegame.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertIsNotDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import ru.andvl.snakegame.main.TestTags

object ComposeLeaderboardPage : Page<ComposeLeaderboardPage>() {
    private val noRecordsContent = hasTestTag(TestTags.NO_RECORDS_CONTENT)
    private val recordsContent = hasTestTag(TestTags.RECORDS_CONTENT)
    private val playButton = hasTestTag(TestTags.PLAY_BUTTON)
    private val settingsButton = hasTestTag(TestTags.SETTINGS_BUTTON)
    private val achievementsButton = hasTestTag(TestTags.ACHIEVEMENTS_BUTTON)
    private val statisticsButton = hasTestTag(TestTags.STATISTICS_BUTTON)

    fun assertNoRecordsIsDisplayed() = apply {
        step("Verify 'No Records' content is displayed") {
            noRecordsContent.assertIsDisplayed()
        }
    }

    fun assertRecordsContentIsDisplayed() = apply {
        step("Verify records content is displayed") {
            recordsContent.assertIsDisplayed()
        }
    }

    fun assertNoRecordsIsNotDisplayed() = apply {
        step("Verify 'No Records' content is not displayed") {
            noRecordsContent.assertIsNotDisplayed()
        }
    }

    fun clickPlayButton() = apply {
        step("Click Play button") {
            playButton.click()
        }
    }

    fun clickSettingsButton() = apply {
        step("Click Settings button") {
            settingsButton.click()
        }
    }

    fun clickAchievementsButton() = apply {
        step("Click Achievements button") {
            achievementsButton.click()
        }
    }

    fun clickStatisticsButton() = apply {
        step("Click Statistics button") {
            statisticsButton.click()
        }
    }

    fun assertAllButtonsDisplayed() = apply {
        step("Verify all navigation buttons are displayed") {
            playButton.assertIsDisplayed()
            settingsButton.assertIsDisplayed()
            achievementsButton.assertIsDisplayed()
            statisticsButton.assertIsDisplayed()
        }
    }
}
