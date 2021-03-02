package curtains.test

import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.test.core.app.ActivityScenario
import curtains.Curtains
import curtains.OnRootViewsChangedListener
import curtains.OnRootViewAddedListener
import curtains.OnRootViewRemovedListener
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.TestActivity
import curtains.test.utilities.addUntilClosed
import curtains.test.utilities.assumeSdkAtLeast
import curtains.test.utilities.assumeSdkAtMost
import curtains.test.utilities.checkAwait
import curtains.test.utilities.getOnActivity
import curtains.test.utilities.getOnMain
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch

class CurtainsRootViewListenersTest {

  @Before
  fun setUp() {
    assumeSdkAtLeast(19, "WindowManagerGlobal.mViews was not backed by ArrayList prior to API 19")
  }

  @Test fun create_activity_attaches_window() {
    val windowAttachedLatch = CountDownLatch(1)
    val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
    listeners.addUntilClosed(OnRootViewAddedListener {
      windowAttachedLatch.countDown()
    }).use {
      ActivityScenario.launch(TestActivity::class.java).use {
        assertThat(windowAttachedLatch).countsToZero()
      }
    }
  }

  @Test fun destroy_activity_detaches_window() {
    val windowDetachedLatch = CountDownLatch(1)
    val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
    listeners.addUntilClosed(OnRootViewRemovedListener {
      windowDetachedLatch.countDown()
    }).use {
      ActivityScenario.launch(TestActivity::class.java).close()
      assertThat(windowDetachedLatch).countsToZero()
    }
  }

  @Test fun show_dialog_attaches_window() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val windowAttachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
      listeners.addUntilClosed(OnRootViewAddedListener {
        windowAttachedLatch.countDown()
      }).use {
        scenario.onActivity { activity ->
          AlertDialog.Builder(activity).show()
        }
        assertThat(windowAttachedLatch).countsToZero()
      }
    }
  }

  @Test fun hide_dialog_detaches_window() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val windowDetachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
      listeners.addUntilClosed(OnRootViewRemovedListener {
        windowDetachedLatch.countDown()
      }).use {
        scenario.onActivity { activity ->
          AlertDialog.Builder(activity).show().dismiss()
        }
        assertThat(windowDetachedLatch).countsToZero()
      }
    }
  }

  @Test fun show_toast_attaches_root_view() {
    assumeSdkAtMost(28, "in Q, text toasts are rendered by SystemUI instead of in-app")
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewAttachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
      listeners.addUntilClosed(OnRootViewAddedListener {
        viewAttachedLatch.countDown()
      }).use {
        val toast = scenario.getOnActivity { activity ->
          Toast.makeText(activity, "Toast!", Toast.LENGTH_SHORT).apply {
            show()
          }
        }
        assertThat(viewAttachedLatch).countsToZero()
        scenario.onActivity {
          toast.cancel()
        }
      }
    }
  }

  @Test fun cancel_toast_detaches_root_view() {
    assumeSdkAtMost(28, "in Q, text toasts are rendered by SystemUI instead of in-app")

    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewAttachedLatch = CountDownLatch(1)
      val viewDetachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
      listeners.addUntilClosed(OnRootViewsChangedListener { _, attached ->
        if (attached) {
          viewAttachedLatch.countDown()
        } else {
          viewDetachedLatch.countDown()
        }
      }).use {
        val toast = scenario.getOnActivity { activity ->
          Toast.makeText(activity, "Toast!", Toast.LENGTH_SHORT).apply {
            show()
          }
        }
        viewAttachedLatch.checkAwait()
        scenario.onActivity {
          toast.cancel()
        }
        assertThat(viewDetachedLatch).countsToZero()
      }
    }
  }

  @Test fun calling_WindowManager_addView_attaches_root_view() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewAttachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
      listeners.addUntilClosed(OnRootViewAddedListener {
        viewAttachedLatch.countDown()
      }).use {
        scenario.onActivity { activity ->
          val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
          val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.TYPE_APPLICATION_PANEL,
            LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
          )
          val textView = TextView(activity).apply { text = "Yo" }
          windowManager.addView(textView, params)
          windowManager.removeView(textView)
        }
        assertThat(viewAttachedLatch).countsToZero()
      }
    }
  }

  @Test fun calling_WindowManager_removeView_detaches_root_view() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewDetachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.onRootViewsChangedListeners }
      listeners.addUntilClosed(OnRootViewRemovedListener {
        viewDetachedLatch.countDown()
      }).use {
        scenario.onActivity { activity ->
          val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
          val params = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.TYPE_APPLICATION_PANEL,
            LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
          )
          val textView = TextView(activity).apply { text = "Yo" }
          windowManager.addView(textView, params)
          windowManager.removeView(textView)
        }
        assertThat(viewDetachedLatch).countsToZero()
      }
    }
  }
}
