package curtains.test

import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.TextView
import android.widget.Toast
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import curtains.Curtains
import curtains.ViewAttachedListener
import curtains.ViewDetachedListener
import curtains.WindowAttachedListener
import curtains.WindowDetachedListener
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.TestActivity
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.Closeable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class CurtainsUiTest {

  @Test fun no_root_views_when_no_activity() {
    assertThat(getOnMain { Curtains.attachedWindows }).isEmpty()
    assertThat(getOnMain { Curtains.attachedRootViews }).isEmpty()
  }

  @Test fun create_activity_attaches_window() {
    val windowAttachedLatch = CountDownLatch(1)
    val listeners = getOnMain { Curtains.windowAttachStateListeners }
    listeners.addUntilClosed(WindowAttachedListener {
      windowAttachedLatch.countDown()
    }).use {
      ActivityScenario.launch(TestActivity::class.java).use {
        assertThat(windowAttachedLatch).countsToZero()
      }
    }
  }

  @Test fun destroy_activity_detaches_window() {
    val windowDetachedLatch = CountDownLatch(1)
    val listeners = getOnMain { Curtains.windowAttachStateListeners }
    listeners.addUntilClosed(WindowDetachedListener {
      windowDetachedLatch.countDown()
    }).use {
      ActivityScenario.launch(TestActivity::class.java).close()
      assertThat(windowDetachedLatch).countsToZero()
    }
  }

  @Test fun show_dialog_attaches_window() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val windowAttachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.windowAttachStateListeners }
      listeners.addUntilClosed(WindowAttachedListener {
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
      val listeners = getOnMain { Curtains.windowAttachStateListeners }
      listeners.addUntilClosed(WindowDetachedListener {
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
    assumeSdkBelow(29, "in Q, text toasts are rendered by SystemUI instead of in-app")
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewAttachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.rootViewAttachStateListeners }
      listeners.addUntilClosed(ViewAttachedListener {
        viewAttachedLatch.countDown()
      }).use {
        scenario.onActivity { activity ->
          val toast = Toast.makeText(activity, "Toast!", Toast.LENGTH_SHORT)
          toast.show()
          toast.cancel()
        }
        assertThat(viewAttachedLatch).countsToZero()
      }
    }
  }

  @Test fun cancel_toast_detaches_root_view() {
    assumeSdkBelow(29, "in Q, text toasts are rendered by SystemUI instead of in-app")
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewDetachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.rootViewAttachStateListeners }
      listeners.addUntilClosed(ViewDetachedListener {
        viewDetachedLatch.countDown()
      }).use {
        scenario.onActivity { activity ->
          val toast = Toast.makeText(activity, "Toast!", Toast.LENGTH_SHORT)
          toast.show()
          toast.cancel()
        }
        assertThat(viewDetachedLatch).countsToZero()
      }
    }
  }

  @Test fun calling_WindowManager_addView_attaches_root_view() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      val viewAttachedLatch = CountDownLatch(1)
      val listeners = getOnMain { Curtains.rootViewAttachStateListeners }
      listeners.addUntilClosed(ViewAttachedListener {
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
      val listeners = getOnMain { Curtains.rootViewAttachStateListeners }
      listeners.addUntilClosed(ViewDetachedListener {
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

  private fun <T> getOnMain(runOnMainSync: () -> T): T {
    val result = AtomicReference<T>()
    InstrumentationRegistry.getInstrumentation().runOnMainSync {
      result.set(runOnMainSync())
    }
    return result.get()
  }

  private fun <T> MutableList<T>.addUntilClosed(element: T): Closeable {
    this += element
    return Closeable {
      this -= element
    }
  }

  private fun assumeSdkBelow(
    sdkInt: Int,
    reason: String
  ) {
    val targetSdkVersion =
      InstrumentationRegistry.getInstrumentation().context.applicationInfo.targetSdkVersion
    val currentVersion = Build.VERSION.SDK_INT
    assumeTrue(
      "Can only run below API level $sdkInt because $reason. " +
        "currentVersion: $currentVersion, " +
        "targetSdkVersion: $targetSdkVersion.",
      currentVersion < sdkInt || targetSdkVersion < sdkInt
    )
  }
}
