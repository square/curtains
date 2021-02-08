package curtains.test.utilities

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assume
import java.io.Closeable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

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

fun assumeSdkBelow(
  sdkInt: Int,
  reason: String
) {
  val targetSdkVersion = application.applicationInfo.targetSdkVersion
  val currentVersion = Build.VERSION.SDK_INT
  Assume.assumeTrue(
    "Can only run below API level $sdkInt because $reason. " +
      "currentVersion: $currentVersion, " +
      "targetSdkVersion: $targetSdkVersion.",
    currentVersion < sdkInt || targetSdkVersion < sdkInt
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