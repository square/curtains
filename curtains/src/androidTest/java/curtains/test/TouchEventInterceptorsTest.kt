
package curtains.test

import android.view.MotionEvent
import android.view.View
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.DispatchState
import curtains.TouchEventInterceptor
import curtains.TouchEventListener
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import curtains.touchEventInterceptors
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class TouchEventInterceptorsTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun touchEvent_dispatched_to_listeners() {
    val touchEventReceived = CountDownLatch(2)
    onActivity { activity ->
      val cancelEvent = cancelEvent()
      activity.window.touchEventInterceptors += TouchEventListener { event ->
        touchEventReceived.countDown()
        assertThat(event).isSameInstanceAs(cancelEvent)
      }

      val rootView = activity.findViewById<View>(android.R.id.content).rootView
      rootView.setOnTouchListener { _, event ->
        assertThat(touchEventReceived.count).isEqualTo(1)
        touchEventReceived.countDown()
        assertThat(event).isSameInstanceAs(cancelEvent)
        false
      }

      rootView.dispatchTouchEvent(cancelEvent)
    }
    assertThat(touchEventReceived).countsToZero()
  }

  @Test fun touchEvent_intercepted() {
    onActivity { activity ->
      val cancelEvent = cancelEvent()
      activity.window.touchEventInterceptors += TouchEventInterceptor { _, _ ->
        DispatchState.Consumed
      }

      val rootView = activity.findViewById<View>(android.R.id.content).rootView
      rootView.setOnTouchListener { _, event ->
        fail("Event $event should have been intercepted")
        false
      }

      val consumed = rootView.dispatchTouchEvent(cancelEvent)

      assertThat(consumed).isTrue()
    }
  }

  @Test fun touchEvent_consumed_dispatched_back() {
    onActivity { activity ->
      val cancelEvent = cancelEvent()
      activity.window.touchEventInterceptors += TouchEventInterceptor { event, dispatch ->
        val result = dispatch(event)
        assertThat(result).isInstanceOf(DispatchState.Consumed::class.java)
        result
      }

      val rootView = activity.findViewById<View>(android.R.id.content).rootView
      rootView.setOnTouchListener { _, _ ->
        true
      }

      rootView.dispatchTouchEvent(cancelEvent)
    }
  }

  private fun cancelEvent() = MotionEvent.obtain(0L, 0L, MotionEvent.ACTION_CANCEL, 0f, 0f, 0)
}