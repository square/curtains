package curtains.test

import android.app.AlertDialog
import android.view.View
import androidx.test.core.app.ActivityScenario
import curtains.Curtains
import curtains.OnActivityCreated
import curtains.OnContentChangedListener
import curtains.WindowAttachedListener
import curtains.onContentChangedListeners
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.TestActivity
import curtains.test.utilities.addUntilClosed
import curtains.test.utilities.application
import curtains.test.utilities.registerUntilClosed
import org.junit.Test
import java.util.concurrent.CountDownLatch

class OnContentChangedListenersTest {

  @Test
  fun contentChanged_on_activity_created() {
    val contentChanged = CountDownLatch(1)
    application.registerUntilClosed(OnActivityCreated { activity, _ ->
      if (activity !is TestActivity) {
        return@OnActivityCreated
      }
      activity.window.onContentChangedListeners += OnContentChangedListener {
        contentChanged.countDown()
      }
    }).use {
      ActivityScenario.launch(TestActivity::class.java).use {
        assertThat(contentChanged).countsToZero()
      }
    }
  }

  @Test
  fun contentChanged_on_activity_setContentView() {
    val contentChanged = CountDownLatch(1)
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        activity.window.onContentChangedListeners += OnContentChangedListener {
          contentChanged.countDown()
        }
        activity.setContentView(View(activity))

      }
    }

    assertThat(contentChanged).countsToZero()
  }

  @Test
  fun contentChanged_on_dialog_setContentView() {
    val contentChanged = CountDownLatch(1)
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        val dialog = AlertDialog.Builder(activity).show()
        dialog.window!!.onContentChangedListeners += OnContentChangedListener {
          contentChanged.countDown()
        }
        dialog.setContentView(View(activity))
      }
    }

    assertThat(contentChanged).countsToZero()
  }
}