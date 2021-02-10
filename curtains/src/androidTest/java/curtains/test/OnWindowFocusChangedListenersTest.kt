package curtains.test

import android.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import curtains.OnActivityCreated
import curtains.OnWindowFocusChangedListener
import curtains.onWindowFocusChangedListeners
import curtains.test.utilities.TestActivity
import curtains.test.utilities.application
import curtains.test.utilities.assumeSdkAtMost
import curtains.test.utilities.launchWaitingForFocus
import curtains.test.utilities.registerUntilClosed
import curtains.test.utilities.BlockingQueueSubject.Companion.assertThat
import org.junit.Before
import org.junit.Test
import java.util.concurrent.ArrayBlockingQueue

class OnWindowFocusChangedListenersTest {

  @Before
  fun setUp() {
    assumeSdkAtMost(29, "Can't repro locally: TestActivity never gets focus in CI on API 30")
  }

  @Test
  fun activity_focus_gained_on_activity_resumed() {
    val activityWindowFocusChanged = ArrayBlockingQueue<Boolean>(1)
    application.registerUntilClosed(OnActivityCreated { activity, _ ->
      if (activity !is TestActivity) {
        return@OnActivityCreated
      }
      activity.window.onWindowFocusChangedListeners += object : OnWindowFocusChangedListener {
        override fun onWindowFocusChanged(hasFocus: Boolean) {
          activity.window.onWindowFocusChangedListeners -= this
          activityWindowFocusChanged.put(hasFocus)
        }
      }
    }).use {
      ActivityScenario.launch(TestActivity::class.java).use {
        assertThat(activityWindowFocusChanged).polls(true)
      }
    }
  }

  @Test
  fun activity_focus_lost_on_activity_paused() {
    val activityWindowFocusChanged = ArrayBlockingQueue<Boolean>(1)
    launchWaitingForFocus(TestActivity::class).use { scenario ->
      scenario.onActivity { activity ->
        activity.window.onWindowFocusChangedListeners += object : OnWindowFocusChangedListener {
          override fun onWindowFocusChanged(hasFocus: Boolean) {
            activity.window.onWindowFocusChangedListeners -= this
            activityWindowFocusChanged.put(hasFocus)
          }
        }
      }
      scenario.moveToState(Lifecycle.State.STARTED)
      assertThat(activityWindowFocusChanged).polls(false)
    }
  }

  @Test
  fun activity_focus_lost_on_dialog_showed() {
    val activityWindowFocusChanged = ArrayBlockingQueue<Boolean>(1)
    launchWaitingForFocus(TestActivity::class).use { scenario ->
      scenario.onActivity { activity ->
        activity.window.onWindowFocusChangedListeners += object : OnWindowFocusChangedListener {
          override fun onWindowFocusChanged(hasFocus: Boolean) {
            activity.window.onWindowFocusChangedListeners -= this
            activityWindowFocusChanged.put(hasFocus)
          }
        }
        AlertDialog.Builder(activity).show()
      }
      assertThat(activityWindowFocusChanged).polls(false)
    }
  }

  @Test
  fun dialog_focus_gained_on_dialog_showed() {
    val dialogWindowFocusChanged = ArrayBlockingQueue<Boolean>(1)
    launchWaitingForFocus(TestActivity::class).use { scenario ->
      scenario.onActivity { activity ->
        val dialog = AlertDialog.Builder(activity).show()
        dialog.window!!.onWindowFocusChangedListeners += object : OnWindowFocusChangedListener {
          override fun onWindowFocusChanged(hasFocus: Boolean) {
            dialog.window!!.onWindowFocusChangedListeners -= this
            dialogWindowFocusChanged.put(hasFocus)
          }
        }
      }
      assertThat(dialogWindowFocusChanged).polls(true)
    }
  }
}