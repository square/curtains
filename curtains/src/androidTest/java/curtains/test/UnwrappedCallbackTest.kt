package curtains.test

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.TouchEventListener
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import curtains.touchEventInterceptors
import curtains.unwrappedCallback
import org.junit.Rule
import org.junit.Test

class UnwrappedCallbackTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun window_callback_not_wrapped_on_new_activity() {
    onActivity { activity ->
      assertThat(activity.window.unwrappedCallback).isSameInstanceAs(activity.window.callback)
    }
  }

  @Test fun window_callback_wrapped_when_setting_listener() {
    onActivity { activity ->
      val initialCallback = activity.window.callback

      activity.window.touchEventInterceptors += TouchEventListener {}

      assertThat(activity.window.callback).isNotSameInstanceAs(initialCallback)
    }
  }

  @Test fun wrapped_window_callback_can_be_unwrapped() {
    onActivity { activity ->
      val initialCallback = activity.window.callback

      activity.window.touchEventInterceptors += TouchEventListener {}

      assertThat(activity.window.unwrappedCallback).isSameInstanceAs(initialCallback)
    }
  }
}