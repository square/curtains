package curtains.test

import android.view.KeyEvent
import android.view.View
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.DispatchState
import curtains.KeyEventInterceptor
import curtains.OnKeyEventListener
import curtains.keyEventInterceptors
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class KeyEventInterceptorsTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun keyEvent_dispatched_to_listeners() {
    val eventReceived = CountDownLatch(2)
    onActivity { activity ->
      val keyEvent = keyEvent()
      activity.window.keyEventInterceptors += OnKeyEventListener { event ->
        eventReceived.countDown()
        assertThat(event).isSameInstanceAs(keyEvent)
      }

      activity.onKeyDown = { _, event ->
        assertThat(eventReceived.count).isEqualTo(1)
        eventReceived.countDown()
        assertThat(event).isSameInstanceAs(keyEvent)
        false
      }

      val rootView = activity.findViewById<View>(android.R.id.content).rootView
      rootView.dispatchKeyEvent(keyEvent)
    }
    assertThat(eventReceived).countsToZero()
  }

  @Test fun touchEvent_intercepted() {
    onActivity { activity ->
      val backEvent = keyEvent()
      activity.window.keyEventInterceptors += KeyEventInterceptor { _, _ ->
        DispatchState.Consumed
      }

      activity.onKeyDown = { _, event ->
        fail("Event $event should have been intercepted")
        false
      }

      val rootView = activity.findViewById<View>(android.R.id.content).rootView

      val consumed = rootView.dispatchKeyEvent(backEvent)

      assertThat(consumed).isTrue()
    }
  }

  @Test fun touchEvent_consumed_dispatched_back() {
    onActivity { activity ->
      val keyEvent = keyEvent()
      activity.window.keyEventInterceptors += KeyEventInterceptor { event, dispatch ->
        val result = dispatch(event)
        assertThat(result).isInstanceOf(DispatchState.Consumed::class.java)
        result
      }

      activity.onKeyDown = { _, _ ->
        true
      }

      val rootView = activity.findViewById<View>(android.R.id.content).rootView
      rootView.dispatchKeyEvent(keyEvent)
    }
  }

  private fun keyEvent() = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_A)
}