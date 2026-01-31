package ru.andvl.snakegame.tests

import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Rule
import org.junit.Test
import ru.andvl.snakegame.main.BaseTest
import ru.andvl.snakegame.pages.ComposeAchievementsPage
import ru.andvl.snakegame.pages.ComposeGamePage
import ru.andvl.snakegame.pages.ComposeLeaderboardPage
import ru.andvl.snakegame.pages.ComposeSettingsPage
import ru.andvl.snakegame.pages.ComposeStatisticsPage

/**
 * UI tests for Leaderboard (Main) Screen
 */
class LeaderboardScreenTest : BaseTest() {
    @get:Rule
    val setUpRule = SetUpRule().add {
        // Setup before each test if needed
    }

    @get:Rule
    val tearDownRule = TearDownRule().add {
        // Cleanup after each test if needed
    }

    @Test
    fun whenAppStarts_shouldDisplayLeaderboardScreen() {
        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
            .assertAllButtonsDisplayed()
    }

    @Test
    fun whenPlayButtonClicked_shouldNavigateToGameScreen() {
        ComposeLeaderboardPage
            .clickPlayButton()

        waitMedium()

        ComposeGamePage
            .assertGameScreenIsDisplayed()
    }

    @Test
    fun whenSettingsButtonClicked_shouldNavigateToSettingsScreen() {
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun whenAchievementsButtonClicked_shouldNavigateToAchievementsScreen() {
        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitMedium()

        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
    }

    @Test
    fun whenStatisticsButtonClicked_shouldNavigateToStatisticsScreen() {
        ComposeLeaderboardPage
            .clickStatisticsButton()

        waitMedium()

        ComposeStatisticsPage
            .assertStatisticsScreenIsDisplayed()
    }

    @Test
    fun whenNavigatingBackFromSettings_shouldReturnToLeaderboard() {
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun verifyAllNavigationButtonsAreClickable() {
        ComposeLeaderboardPage
            .assertAllButtonsDisplayed()
            .clickPlayButton()

        waitMedium()

        ComposeGamePage
            .assertGameScreenIsDisplayed()
    }
}
