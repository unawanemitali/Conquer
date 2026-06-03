package com.example

import android.content.Context
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.Review
import com.example.ui.TaskViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Test
  fun testReflectionArchiveFlow() {
    // Obtain AppDatabase and insert reviews synchronously
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = AppDatabase.getDatabase(context)
    
    runBlocking {
      db.reviewDao().insertReview(
          Review(
              reviewType = "Weekly",
              wentWell = "I completed major task milestones in the orbital timeline.",
              distractions = "Got slightly delayed by cosmic rays and solar flares."
          )
      )
      db.reviewDao().insertReview(
          Review(
              reviewType = "Monthly",
              wentWell = "Acquired great masteries in gamified focusing mechanics.",
              distractions = "Asteroid fields were quite active during DeX exploration."
          )
      )
    }
    
    // Advance the compose main clock by 4 seconds to bypass the splash screen
    composeTestRule.mainClock.advanceTimeBy(4000)
    composeTestRule.waitForIdle()

    // Open settings dialog
    composeTestRule.onNodeWithTag("global_settings_button", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    // Check if dialog tabs exist
    composeTestRule.onNodeWithTag("settings_archive_tab", useUnmergedTree = true).assertExists()

    // Tap on Reflection Archive Tab
    composeTestRule.onNodeWithTag("settings_archive_tab", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()

    // Assert archive filter keys exist
    composeTestRule.onNodeWithTag("filter_review_all", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("filter_review_weekly", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("filter_review_monthly", useUnmergedTree = true).assertExists()

    // Perform filter clicks to verify no crashes occur during state updates
    composeTestRule.onNodeWithTag("filter_review_weekly", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    
    composeTestRule.onNodeWithTag("filter_review_monthly", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
    
    composeTestRule.onNodeWithTag("filter_review_all", useUnmergedTree = true).performClick()
    composeTestRule.waitForIdle()
  }

  @Test
  fun testSplashTransitionOnForeground() {
    // Initially, splash screen is displayed. Assert it exists.
    composeTestRule.onNodeWithTag("splash_screen_container", useUnmergedTree = true).assertExists()

    // Advance clock by 4 seconds so that splash is dismissed.
    composeTestRule.mainClock.advanceTimeBy(4000)
    composeTestRule.waitForIdle()

    // Now splash screen is gone. Assert it does not exist.
    composeTestRule.onNodeWithTag("splash_screen_container", useUnmergedTree = true).assertDoesNotExist()

    // Put activity through a lifecycle pause/resume transition (background to foreground)
    val scenario = composeTestRule.activityRule.scenario
    scenario.moveToState(androidx.lifecycle.Lifecycle.State.CREATED) // Simulates backgrounding
    composeTestRule.waitForIdle()

    scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED) // Simulates returning to foreground
    composeTestRule.waitForIdle()

    // Splash screen should immediately appear again!
    composeTestRule.onNodeWithTag("splash_screen_container", useUnmergedTree = true).assertExists()

    // And after 4 more seconds, it should be gone again.
    composeTestRule.mainClock.advanceTimeBy(4000)
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithTag("splash_screen_container", useUnmergedTree = true).assertDoesNotExist()
  }

  @Test
  fun testEatTheFrogSectionAndEmptyState() {
    // Advance clock by 4 seconds so that splash is dismissed.
    composeTestRule.mainClock.advanceTimeBy(4000)
    composeTestRule.waitForIdle()

    // Assert that the Today's Frog section title is permanently visible
    composeTestRule.onNodeWithTag("frog_section_title", useUnmergedTree = true).assertExists()

    // Assert that the empty state is displayed because no frog is selected initially
    composeTestRule.onNodeWithTag("frog_empty_state_container", useUnmergedTree = true).assertExists()
    composeTestRule.onNodeWithTag("frog_empty_state_title", useUnmergedTree = true)
        .assertTextEquals("No frog selected for today.")
  }
}
