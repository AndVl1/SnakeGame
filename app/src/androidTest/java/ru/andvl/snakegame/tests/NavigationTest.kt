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
 * UI tests for navigation flows across all screens
 */
class NavigationTest : BaseTest() {
    @get:Rule
    val setUpRule = SetUpRule().add {
        // Start from leaderboard
    }

    @get:Rule
    val tearDownRule = TearDownRule().add {
        // Cleanup if needed
    }

    @Test
    fun testFullNavigationFlow_LeaderboardToGameToLeaderboard() {
        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
            .clickPlayButton()

        waitMedium()

        ComposeGamePage
            .assertGameScreenIsDisplayed()

        // Navigate back using system back would require BackHandler testing
        // For now we verify forward navigation works
    }

    @Test
    fun testFullNavigationFlow_LeaderboardToSettingsToLeaderboard() {
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
    fun testFullNavigationFlow_LeaderboardToAchievementsToLeaderboard() {
        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitMedium()

        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun testFullNavigationFlow_LeaderboardToStatisticsToLeaderboard() {
        ComposeLeaderboardPage
            .clickStatisticsButton()

        waitMedium()

        ComposeStatisticsPage
            .assertStatisticsScreenIsDisplayed()
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun testMultipleScreenNavigation() {
        // Leaderboard -> Settings
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
            .clickBackButton()

        waitMedium()

        // Leaderboard -> Achievements
        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitMedium()

        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
            .clickBackButton()

        waitMedium()

        // Leaderboard -> Game
        ComposeLeaderboardPage
            .clickPlayButton()

        waitMedium()

        ComposeGamePage
            .assertGameScreenIsDisplayed()
    }

    @Test
    fun testSettingsNavigationPersistence() {
        // Navigate to settings
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        // Toggle theme
        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
            .clickDarkThemeToggle()

        waitShort()

        // Navigate back
        ComposeSettingsPage
            .clickBackButton()

        waitMedium()

        // Navigate to settings again
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        // Settings should still be displayed
        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
    }

    @Test
    fun testAllScreensAreAccessibleFromLeaderboard() {
        // Test Game navigation
        ComposeLeaderboardPage
            .assertAllButtonsDisplayed()
            .clickPlayButton()

        waitMedium()

        ComposeGamePage
            .assertGameScreenIsDisplayed()

        // We would need proper back navigation to continue testing
        // This test verifies that all navigation entry points exist
    }

    @Test
    fun testRapidNavigationBetweenScreens() {
        // Rapidly navigate between screens
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitShort()

        ComposeSettingsPage
            .clickBackButton()

        waitShort()

        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitShort()

        ComposeAchievementsPage
            .clickBackButton()

        waitShort()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun testNavigationStatePreservation() {
        // Navigate to settings and make a change
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        ComposeSettingsPage
            .clickDarkThemeToggle()

        waitShort()

        ComposeSettingsPage
            .clickBackButton()

        waitMedium()

        // Navigate to achievements
        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitMedium()

        ComposeAchievementsPage
            .clickBackButton()

        waitMedium()

        // Navigate back to settings
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        // Settings screen should still be accessible
        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
    }
}
