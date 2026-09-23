package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.SkillEntry
import com.example.model.SkillLevel
import com.example.ui.components.MatchScoreBadge
import com.example.ui.components.SkillBadge
import com.example.ui.theme.MyApplicationTheme
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
  fun skill_badge_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        SkillBadge(skill = SkillEntry("Python", SkillLevel.ADVANCED), isHighlighted = true)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/skill_badge.png")
  }

  @Test
  fun match_badge_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        MatchScoreBadge(score = 94)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/match_badge.png")
  }
}
