package ru.andvl.snakegame.pages

import androidx.compose.ui.test.hasTestTag
import com.atiurin.ultron.allure.step.step
import com.atiurin.ultron.extensions.assertIsDisplayed
import com.atiurin.ultron.extensions.assertIsNotDisplayed
import com.atiurin.ultron.extensions.click
import com.atiurin.ultron.page.Page
import ru.andvl.snakegame.main.TestTags

object ComposeAchievementsPage : Page<ComposeAchievementsPage>() {
    private val achievementsScreen = hasTestTag(TestTags.ACHIEVEMENTS_SCREEN)
    private val achievementsList = hasTestTag(TestTags.ACHIEVEMENTS_LIST)
    private val backButton = hasTestTag(TestTags.ACHIEVEMENTS_BACK_BUTTON)
    private val loadingIndicator = hasTestTag(TestTags.ACHIEVEMENT_LOADING)

    fun assertAchievementsScreenIsDisplayed() = apply {
        step("Verify achievements screen is displayed") {
            achievementsScreen.assertIsDisplayed()
            backButton.assertIsDisplayed()
        }
    }

    fun assertAchievementsListIsDisplayed() = apply {
        step("Verify achievements list is displayed") {
            achievementsList.assertIsDisplayed()
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

    fun assertAchievementCardIsDisplayed(achievementType: String) = apply {
        step("Verify achievement card is displayed: $achievementType") {
            hasTestTag("${TestTags.ACHIEVEMENT_CARD_PREFIX}$achievementType").assertIsDisplayed()
        }
    }

    fun clickAchievementCard(achievementType: String) = apply {
        step("Click achievement card: $achievementType") {
            hasTestTag("${TestTags.ACHIEVEMENT_CARD_PREFIX}$achievementType").click()
        }
    }

    fun clickBackButton() = apply {
        step("Click back button") {
            backButton.click()
        }
    }
}
