package curtains.test

import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import curtains.OnActivityCreated
import curtains.OnActivityResumed
import curtains.onNextDraw
import curtains.test.OnNextDrawTest.CountOrder.ON_DRAW_LISTENER
import curtains.test.OnNextDrawTest.CountOrder.ON_NEXT_DRAW
import curtains.test.OnNextDrawTest.CountOrder.ON_RESUME
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.TestActivity
import curtains.test.utilities.application
import curtains.test.utilities.onAttachedToWindow
import curtains.test.utilities.registerUntilClosed
import curtains.test.utilities.useWith
import org.junit.Test
import java.util.concurrent.CountDownLatch

class OnNextDrawTest {

  /**
   * This enum help express that we expect [onNextDraw] to trigger in between
   * [android.app.Activity.onResume] and a draw listener set from on resume (i.e. later in the list
   * of listeners).
   */
  enum class CountOrder {
    ON_RESUME,
    ON_NEXT_DRAW,
    ON_DRAW_LISTENER;

    val expectedCount: Int
      get() = values().size - ordinal
  }

  @Test fun onNextDraw_triggers_on_first_activity_draw() {
    val activityDrawn = CountDownLatch(CountOrder.values().size)
    application.registerUntilClosed(OnActivityCreated { activity, _ ->
      if (activity !is TestActivity) {
        return@OnActivityCreated
      }
      activity.window.onNextDraw {
        assertThat(activityDrawn.count).isEqualTo(ON_NEXT_DRAW.expectedCount)
        activityDrawn.countDown()
      }
    }).useWith(
      application.registerUntilClosed(OnActivityResumed { activity ->
        if (activity !is TestActivity) {
          return@OnActivityResumed
        }
        var firstOnDrawn = true
        val decorView = activity.window.peekDecorView()!!
        decorView.onAttachedToWindow {
          decorView.viewTreeObserver.addOnDrawListener {
            if (firstOnDrawn) {
              firstOnDrawn = false
              assertThat(activityDrawn.count).isEqualTo(ON_DRAW_LISTENER.expectedCount)
              activityDrawn.countDown()
            }
          }
        }
        assertThat(activityDrawn.count).isEqualTo(ON_RESUME.expectedCount)
        activityDrawn.countDown()
      })
    ).use {
      ActivityScenario.launch(TestActivity::class.java).use {
        assertThat(activityDrawn).countsToZero()
      }
    }
  }
}