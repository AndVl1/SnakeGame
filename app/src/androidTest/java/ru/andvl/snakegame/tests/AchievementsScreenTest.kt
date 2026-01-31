package ru.andvl.snakegame.tests

import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Rule
import org.junit.Test
import ru.andvl.snakegame.main.BaseTest
import ru.andvl.snakegame.pages.ComposeAchievementsPage
import ru.andvl.snakegame.pages.ComposeLeaderboardPage

/**
 * UI tests for Achievements Screen
 */
class AchievementsScreenTest : BaseTest() {
    @get:Rule
    val setUpRule = SetUpRule().add {
        // Navigate to achievements screen before each test
        ComposeLeaderboardPage.clickAchievementsButton()
        waitMedium()
    }

    @get:Rule
    val tearDownRule = TearDownRule().add {
        // Navigate back to main screen after each test
        try {
            ComposeAchievementsPage.clickBackButton()
            waitShort()
        } catch (e: Exception) {
            // Ignore if already on main screen
        }
    }

    @Test
    fun whenAchievementsOpened_shouldDisplayScreen() {
        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
    }

    @Test
    fun whenAchievementsLoad_shouldNotShowLoading() {
        waitLong() // Wait for data to load

        ComposeAchievementsPage
            .assertLoadingIsNotDisplayed()
    }

    @Test
    fun whenBackButtonClicked_shouldNavigateToLeaderboard() {
        ComposeAchievementsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun verifyAchievementsScreenIsAccessible() {
        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
    }

    @Test
    fun whenNavigatingMultipleTimes_achievementsShouldRemainAccessible() {
        // Navigate back
        ComposeAchievementsPage
            .clickBackButton()

        waitMedium()

        // Navigate forward
        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitMedium()

        // Achievements should still be accessible
        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
    }

    @Test
    fun testAchievementsListDisplaysAfterLoading() {
        waitLong() // Wait for achievements to load

        // Verify list or empty state is shown
        ComposeAchievementsPage
            .assertAchievementsScreenIsDisplayed()
    }

    @Test
    fun testNavigationFromAchievementsToLeaderboardMultipleTimes() {
        // First round
        ComposeAchievementsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .clickAchievementsButton()

        waitMedium()

        // Second round
        ComposeAchievementsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }
}
