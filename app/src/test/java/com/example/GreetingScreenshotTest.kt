package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.ClockConfig
import com.example.ui.components.ClockDisplayView
import com.example.ui.theme.WalloraTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun wallora_clock_screenshot() {
    composeTestRule.setContent {
      WalloraTheme {
        ClockDisplayView(config = ClockConfig())
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/wallora_clock.png")
  }
}
