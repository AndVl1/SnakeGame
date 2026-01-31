package ru.andvl.snakegame.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertIsNotDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import ru.andvl.snakegame.main.TestTags

object ComposeStatisticsPage : Page<ComposeStatisticsPage>() {
    private val statisticsScreen = hasTestTag(TestTags.STATISTICS_SCREEN)
    private val statisticsContent = hasTestTag(TestTags.STATISTICS_CONTENT)
    private val backButton = hasTestTag(TestTags.STATISTICS_BACK_BUTTON)
    private val resetButton = hasTestTag(TestTags.STATISTICS_RESET_BUTTON)
    private val loadingIndicator = hasTestTag(TestTags.STATISTICS_LOADING)
    private val errorMessage = hasTestTag(TestTags.STATISTICS_ERROR)

    fun assertStatisticsScreenIsDisplayed() = apply {
        step("Verify statistics screen is displayed") {
            statisticsScreen.assertIsDisplayed()
        }
    }

    fun assertStatisticsContentIsDisplayed() = apply {
        step("Verify statistics content is displayed") {
            statisticsContent.assertIsDisplayed()
        }
    }

    fun assertLoadingIsDisplayed() = apply {
        step("Verify loading indicator is displayed") {
            loadingIndicator.assertIsDisplayed()
        }
    }

    fun assertLoadingIsNotDisplayed() = apply {
        step("Verify loading indicator is not displayed") {
            loadingIndicator.assertIsNotDisplayed()
        }
    }

    fun assertErrorIsDisplayed() = apply {
        step("Verify error message is displayed") {
            errorMessage.assertIsDisplayed()
        }
    }

    fun assertErrorIsNotDisplayed() = apply {
        step("Verify error message is not displayed") {
            errorMessage.assertIsNotDisplayed()
        }
    }

    fun assertResetButtonIsDisplayed() = apply {
        step("Verify reset button is displayed") {
            resetButton.assertIsDisplayed()
        }
    }

    fun clickBackButton() = apply {
        step("Click back button") {
            backButton.click()
        }
    }

    fun clickResetButton() = apply {
        step("Click reset button") {
            resetButton.click()
        }
    }
}
