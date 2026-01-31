package ru.andvl.snakegame.tests

import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Rule
import org.junit.Test
import ru.andvl.snakegame.main.BaseTest
import ru.andvl.snakegame.pages.ComposeLeaderboardPage
import ru.andvl.snakegame.pages.ComposeStatisticsPage

/**
 * UI tests for Statistics Screen
 */
class StatisticsScreenTest : BaseTest() {
    @get:Rule
    val setUpRule = SetUpRule().add {
        // Navigate to statistics screen before each test
        ComposeLeaderboardPage.clickStatisticsButton()
        waitMedium()
    }

    @get:Rule
    val tearDownRule = TearDownRule().add {
        // Navigate back to main screen after each test
        try {
            ComposeStatisticsPage.clickBackButton()
            waitShort()
        } catch (e: Exception) {
            // Ignore if already on main screen
        }
    }

    @Test
    fun whenStatisticsOpened_shouldDisplayScreen() {
        ComposeStatisticsPage
            .assertStatisticsScreenIsDisplayed()
    }

    @Test
    fun whenStatisticsLoad_shouldDisplayContent() {
        waitLong() // Wait for data to load

        ComposeStatisticsPage
            .assertStatisticsContentIsDisplayed()
    }

    @Test
    fun whenStatisticsLoad_shouldNotShowLoading() {
        waitLong() // Wait for data to load

        ComposeStatisticsPage
            .assertLoadingIsNotDisplayed()
    }

    @Test
    fun whenStatisticsLoad_shouldNotShowError() {
        waitLong() // Wait for data to load

        ComposeStatisticsPage
            .assertErrorIsNotDisplayed()
    }

    @Test
    fun whenBackButtonClicked_shouldNavigateToLeaderboard() {
        ComposeStatisticsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun verifyStatisticsScreenIsAccessible() {
        ComposeStatisticsPage
            .assertStatisticsScreenIsDisplayed()
    }

    @Test
    fun whenNavigatingMultipleTimes_statisticsShouldRemainAccessible() {
        // Navigate back
        ComposeStatisticsPage
            .clickBackButton()

        waitMedium()

        // Navigate forward
        ComposeLeaderboardPage
            .clickStatisticsButton()

        waitMedium()

        // Statistics should still be accessible
        ComposeStatisticsPage
            .assertStatisticsScreenIsDisplayed()
    }

    @Test
    fun testNavigationFromStatisticsToLeaderboardMultipleTimes() {
        // First round
        ComposeStatisticsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .clickStatisticsButton()

        waitMedium()

        // Second round
        ComposeStatisticsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun testStatisticsContentDisplaysAfterLoading() {
        waitLong() // Wait for statistics to load

        ComposeStatisticsPage
            .assertStatisticsContentIsDisplayed()
            .assertLoadingIsNotDisplayed()
    }
}
