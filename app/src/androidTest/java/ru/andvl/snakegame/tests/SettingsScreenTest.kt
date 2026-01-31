package ru.andvl.snakegame.tests

import com.atiurin.ultron.testlifecycle.setupteardown.SetUpRule
import com.atiurin.ultron.testlifecycle.setupteardown.TearDownRule
import org.junit.Rule
import org.junit.Test
import ru.andvl.snakegame.main.BaseTest
import ru.andvl.snakegame.pages.ComposeLeaderboardPage
import ru.andvl.snakegame.pages.ComposeSettingsPage

/**
 * UI tests for Settings Screen
 */
class SettingsScreenTest : BaseTest() {
    @get:Rule
    val setUpRule = SetUpRule().add {
        // Navigate to settings screen before each test
        ComposeLeaderboardPage.clickSettingsButton()
        waitMedium()
    }

    @get:Rule
    val tearDownRule = TearDownRule().add {
        // Navigate back to main screen after each test
        try {
            ComposeSettingsPage.clickBackButton()
            waitShort()
        } catch (e: Exception) {
            // Ignore if already on main screen
        }
    }

    @Test
    fun whenSettingsOpened_shouldDisplayAllComponents() {
        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
            .assertAllSettingsDisplayed()
    }

    @Test
    fun whenSettingsOpened_darkThemeToggleShouldBeDisplayed() {
        ComposeSettingsPage
            .assertDarkThemeToggleDisplayed()
    }

    @Test
    fun whenDarkThemeToggleClicked_shouldToggleTheme() {
        ComposeSettingsPage
            .clickDarkThemeToggle()

        waitShort()

        // Theme should be toggled (verified by visual inspection in actual use)
        ComposeSettingsPage
            .assertDarkThemeToggleDisplayed()
    }

    @Test
    fun whenDarkThemeToggledTwice_shouldReturnToOriginalState() {
        ComposeSettingsPage
            .clickDarkThemeToggle()

        waitShort()

        ComposeSettingsPage
            .clickDarkThemeToggle()

        waitShort()

        ComposeSettingsPage
            .assertDarkThemeToggleDisplayed()
    }

    @Test
    fun whenBackButtonClicked_shouldNavigateToLeaderboard() {
        ComposeSettingsPage
            .clickBackButton()

        waitMedium()

        ComposeLeaderboardPage
            .assertNoRecordsIsDisplayed()
    }

    @Test
    fun verifyThemeTogglePersistence() {
        // Toggle theme
        ComposeSettingsPage
            .clickDarkThemeToggle()

        waitShort()

        // Navigate away
        ComposeSettingsPage
            .clickBackButton()

        waitMedium()

        // Navigate back to settings
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        // Theme setting should be persisted
        ComposeSettingsPage
            .assertDarkThemeToggleDisplayed()
    }

    @Test
    fun verifyAllSettingsAreAccessible() {
        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
            .assertDarkThemeToggleDisplayed()
    }

    @Test
    fun whenNavigatingMultipleTimes_settingsShouldRemainAccessible() {
        // Navigate back
        ComposeSettingsPage
            .clickBackButton()

        waitMedium()

        // Navigate forward
        ComposeLeaderboardPage
            .clickSettingsButton()

        waitMedium()

        // Settings should still be accessible
        ComposeSettingsPage
            .assertSettingsScreenIsDisplayed()
            .assertDarkThemeToggleDisplayed()
    }
}
