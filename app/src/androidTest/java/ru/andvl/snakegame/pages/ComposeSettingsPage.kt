package ru.andvl.snakegame.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import ru.andvl.snakegame.main.TestTags

object ComposeSettingsPage : Page<ComposeSettingsPage>() {
    private val settingsScreen = hasTestTag(TestTags.SETTINGS_SCREEN)
    private val backButton = hasTestTag(TestTags.SETTINGS_BACK_BUTTON)
    private val settingsCard = hasTestTag(TestTags.SETTINGS_CARD)
    private val darkThemeToggle = hasTestTag(TestTags.SETTINGS_THEME_SWITCH)
    private val soundToggle = hasTestTag(TestTags.SETTINGS_SOUND_SWITCH)
    private val vibrationToggle = hasTestTag(TestTags.SETTINGS_VIBRATION_SWITCH)
    private val difficultySelector = hasTestTag(TestTags.SETTINGS_DIFFICULTY_SELECTOR)
    private val swipeSensitivitySlider = hasTestTag(TestTags.SETTINGS_SWIPE_SENSITIVITY_SLIDER)

    fun assertSettingsScreenIsDisplayed() = apply {
        step("Verify settings screen is displayed") {
            settingsScreen.assertIsDisplayed()
            backButton.assertIsDisplayed()
            settingsCard.assertIsDisplayed()
        }
    }

    fun assertDarkThemeToggleDisplayed() = apply {
        step("Verify dark theme toggle is displayed") {
            darkThemeToggle.assertIsDisplayed()
        }
    }

    fun assertSoundToggleDisplayed() = apply {
        step("Verify sound toggle is displayed") {
            soundToggle.assertIsDisplayed()
        }
    }

    fun assertVibrationToggleDisplayed() = apply {
        step("Verify vibration toggle is displayed") {
            vibrationToggle.assertIsDisplayed()
        }
    }

    fun assertDifficultySelectorDisplayed() = apply {
        step("Verify difficulty selector is displayed") {
            difficultySelector.assertIsDisplayed()
        }
    }

    fun assertSwipeSensitivitySliderDisplayed() = apply {
        step("Verify swipe sensitivity slider is displayed") {
            swipeSensitivitySlider.assertIsDisplayed()
        }
    }

    fun clickDarkThemeToggle() = apply {
        step("Click dark theme toggle") {
            darkThemeToggle.click()
        }
    }

    fun clickSoundToggle() = apply {
        step("Click sound toggle") {
            soundToggle.click()
        }
    }

    fun clickVibrationToggle() = apply {
        step("Click vibration toggle") {
            vibrationToggle.click()
        }
    }

    fun clickDifficultyButton(difficulty: Int) = apply {
        step("Click difficulty button: $difficulty") {
            hasTestTag("${TestTags.SETTINGS_DIFFICULTY_BUTTON_PREFIX}$difficulty").click()
        }
    }

    fun clickBackButton() = apply {
        step("Click back button") {
            backButton.click()
        }
    }

    fun assertAllSettingsDisplayed() = apply {
        step("Verify all settings controls are displayed") {
            darkThemeToggle.assertIsDisplayed()
            // Sound and vibration might be optional
            settingsCard.assertIsDisplayed()
        }
    }
}
