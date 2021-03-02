package curtains.test

import androidx.appcompat.view.WindowCallbackWrapper
import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import curtains.OnTouchEventListener
import curtains.test.utilities.TestActivity
import curtains.test.utilities.TestCompatActivity
import curtains.touchEventInterceptors
import curtains.wrappedCallback
import org.junit.Test

class WrappedCallbackTest {

  @Test fun window_callback_not_wrapped_on_new_activity() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        assertThat(activity.window.callback.wrappedCallback).isSameInstanceAs(activity)
      }
    }
  }

  @Test fun window_callback_wrapped_by_app_compat_activity() {
    ActivityScenario.launch(TestCompatActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        assertThat(activity.window.callback).isInstanceOf(WindowCallbackWrapper::class.java)
        assertThat(activity.window.callback.wrappedCallback).isSameInstanceAs(activity)
      }
    }
  }

  @Test fun window_callback_wrapped_when_setting_listener() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        activity.window.touchEventInterceptors += OnTouchEventListener {}

        assertThat(activity.window.callback).isNotSameInstanceAs(activity)
      }
    }
  }

  @Test fun wrapped_window_callback_can_be_unwrapped() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        activity.window.touchEventInterceptors += OnTouchEventListener {}

        assertThat(activity.window.callback.wrappedCallback).isSameInstanceAs(activity)
      }
    }
  }

  @Test fun wrapped_window_callback_can_be_unwrapped_past_app_compat() {
    ActivityScenario.launch(TestCompatActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        activity.window.touchEventInterceptors += OnTouchEventListener {}

        assertThat(activity.window.callback.wrappedCallback).isSameInstanceAs(activity)
      }
    }
  }
}