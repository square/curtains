package curtains.test.utilities

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.os.ParcelFileDescriptor
import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import curtains.OnWindowFocusChangedListener
import curtains.onWindowFocusChangedListeners
import org.junit.Assume
import java.io.Closeable
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

fun <T> getOnMain(runOnMainSync: () -> T): T {
  val result = AtomicReference<T>()
  InstrumentationRegistry.getInstrumentation().runOnMainSync {
    result.set(runOnMainSync())
  }
  return result.get()
}

fun <T> MutableList<T>.addUntilClosed(element: T): Closeable {
  this += element
  return Closeable {
    this -= element
  }
}

fun Application.registerUntilClosed(callbacks: ActivityLifecycleCallbacks): Closeable {
  registerActivityLifecycleCallbacks(callbacks)
  return Closeable {
    unregisterActivityLifecycleCallbacks(callbacks)
  }
}

fun assumeSdkAtMost(
  sdkInt: Int,
  reason: String
) {
  val currentVersion = Build.VERSION.SDK_INT
  Assume.assumeTrue(
    "SDK Int $currentVersion > max $sdkInt: $reason",
    currentVersion <= sdkInt
  )
}

fun assumeSdkAtLeast(
  sdkInt: Int,
  reason: String
) {
  val currentVersion = Build.VERSION.SDK_INT
  Assume.assumeTrue(
    "SDK Int $currentVersion < min $sdkInt: $reason",
    currentVersion >= sdkInt
  )
}

fun CountDownLatch.checkAwait() {
  check(await(30, TimeUnit.SECONDS)) {
    "30 seconds elapsed without count coming down"
  }
}

val application: Application
  get() = InstrumentationRegistry.getInstrumentation().context.applicationContext as Application

fun <T : Activity, R> ActivityScenario<T>.getOnActivity(getOnActivity: (T) -> R): R {
  val result = AtomicReference<R>()
  onActivity { activity ->
    result.set(getOnActivity(activity))
  }
  return result.get()
}

fun Closeable.useWith(other: Closeable): Closeable {
  return Closeable {
    close()
    other.close()
  }
}

fun View.onAttachedToWindow(onAttachedToWindow: () -> Unit) {
  addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
    override fun onViewAttachedToWindow(v: View) = onAttachedToWindow()

    override fun onViewDetachedFromWindow(v: View) = Unit
  })
}

fun <T : Activity> launchWaitingForFocus(activityClass: KClass<T>): ActivityScenario<T> {
  val scenario = ActivityScenario.launch(activityClass.java)
  var hasFocus = scenario.waitForFocus()
  if (hasFocus == null) {
    resolveAnr()
  }
  hasFocus = scenario.waitForFocus()
  if (hasFocus == null) {
    dumpWindowService()
  }
  check(hasFocus) {
    "expected activity to become focused"
  }
  return scenario
}

private fun <T : Activity> ActivityScenario<T>.waitForFocus(): Boolean? {
  val activityHasWindowFocus = ArrayBlockingQueue<Boolean>(1)
  onActivity { activity ->
    if (activity.hasWindowFocus()) {
      activityHasWindowFocus.put(true)
    } else {
      activity.window.onWindowFocusChangedListeners += object : OnWindowFocusChangedListener {
        override fun onWindowFocusChanged(hasFocus: Boolean) {
          activity.window.onWindowFocusChangedListeners -= this
          activityHasWindowFocus.put(hasFocus)
        }
      }
    }
  }
  return activityHasWindowFocus.poll(10, TimeUnit.SECONDS)
}

private fun resolveAnr() {
  val instrumentation = InstrumentationRegistry.getInstrumentation()
  val uiDevice = UiDevice.getInstance(instrumentation)
  uiDevice.findObject(UiSelector().resourceId("android:id/aerr_wait"))?.click()
}

private fun dumpWindowService(): Nothing {
  val dump = InstrumentationRegistry.getInstrumentation()
      .uiAutomation
      .executeShellCommand("dumpsys window")
      .let(ParcelFileDescriptor::AutoCloseInputStream)
      .bufferedReader().useLines { it.joinToString("\n|") }
  throw RuntimeException(dump)
}