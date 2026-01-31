package ru.andvl.snakegame.main

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.atiurin.ultron.core.config.UltronConfig
import org.junit.Before
import org.junit.Rule
import ru.andvl.snakegame.MainActivity

abstract class BaseTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        UltronConfig.apply {
            operationTimeoutMs = 10_000
            elementWaitingTimeoutMs = 5_000
        }
    }

    /**
     * Wait for a short time (useful for animations)
     */
    protected fun waitShort() {
        Thread.sleep(300)
    }

    /**
     * Wait for a medium time (useful for screen transitions)
     */
    protected fun waitMedium() {
        Thread.sleep(500)
    }

    /**
     * Wait for a long time (useful for complex operations)
     */
    protected fun waitLong() {
        Thread.sleep(1000)
    }
}
