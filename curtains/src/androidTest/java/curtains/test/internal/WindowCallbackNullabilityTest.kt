package curtains.test.internal

import androidx.test.ext.junit.rules.ActivityScenarioRule
import curtains.internal.WindowCallbackWrapper
import curtains.internal.WindowCallbackWrapper.Companion.listeners
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import org.junit.Rule
import org.junit.Test

class WindowCallbackNullabilityTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun onMenuOpened_with_null_menu_does_not_throw() {
    onActivity { activity ->
      // Install callback
      activity.window.listeners

      val callback = activity.window.callback as WindowCallbackWrapper

      callback.onMenuOpened(0, null)
    }
  }
}
