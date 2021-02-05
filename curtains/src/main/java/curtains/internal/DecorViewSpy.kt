package curtains.internal

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Utilities to extracts a [Window] out of a [View] if that view is a decor view.
 */
@SuppressLint("PrivateApi")
internal object DecorViewSpy {

  /**
   * Originally, DecorView was an inner class of PhoneWindow. In the initial import in 2009,
   * PhoneWindow is in com.android.internal.policy.impl.PhoneWindow and that didn't change until
   * API 23.
   * In API 22: https://android.googlesource.com/platform/frameworks/base/+/android-5.1.1_r38/policy/src/com/android/internal/policy/impl/PhoneWindow.java
   * PhoneWindow was then moved to android.view and then again to com.android.internal.policy
   * https://android.googlesource.com/platform/frameworks/base/+/b10e33ff804a831c71be9303146cea892b9aeb5d
   * https://android.googlesource.com/platform/frameworks/base/+/6711f3b34c2ad9c622f56a08b81e313795fe7647
   * In API 23: https://android.googlesource.com/platform/frameworks/base/+/android-6.0.0_r1/core/java/com/android/internal/policy/PhoneWindow.java
   * Then DecorView moved out of PhoneWindow into its own class:
   * https://android.googlesource.com/platform/frameworks/base/+/8804af2b63b0584034f7ec7d4dc701d06e6a8754
   * In API 24: https://android.googlesource.com/platform/frameworks/base/+/android-7.0.0_r1/core/java/com/android/internal/policy/DecorView.java
   */
  private val decorViewClass by lazy(NONE) {
    val sdkInt = Build.VERSION.SDK_INT
    val decorViewClassName = when {
      sdkInt >= 24 -> "com.android.internal.policy.DecorView"
      sdkInt == 23 -> "com.android.internal.policy.PhoneWindow\$DecorView"
      else -> "com.android.internal.policy.impl.PhoneWindow\$DecorView"
    }
    try {
      Class.forName(decorViewClassName)
    } catch (ignored: Throwable) {
      Log.d(
        "DecorViewSpy", "Unexpected exception loading $decorViewClassName on API $sdkInt", ignored
      )
      null
    }
  }

  // In Android 6 / API 23 PhoneWindow.DecorView became an inner static class. Before, it had
  // a direct reference to its outer PhoneWindow class.
  //  https://android.googlesource.com/platform/frameworks/base/+/0daf2102a20d224edeb4ee45dd4ee91889ef3e0c
  /**
   * See [decorViewClass] for the AOSP history of the DecorView class.
   * Between the latest API 23 release and the first API 24 release, DecorView first became a
   * static class:
   * https://android.googlesource.com/platform/frameworks/base/+/0daf2102a20d224edeb4ee45dd4ee91889ef3e0c
   * Then it was extracted into a separate class.
   *
   * Hence the change of window field name from "this$0" to "mWindow" on API 24+.
   */
  private val mWindowField by lazy(NONE) {
    decorViewClass?.let { decorViewClass ->
      val sdkInt = Build.VERSION.SDK_INT
      val fieldName = if (sdkInt >= 24) "mWindow" else "this$0"
      try {
        decorViewClass.getDeclaredField("fieldName").apply { isAccessible = true }
      } catch (ignored: NoSuchFieldException) {
        Log.d(
          "DecorViewSpy",
          "Unexpected exception retrieving $decorViewClass#$fieldName on API $sdkInt", ignored
        )
        null
      }
    }
  }

  fun pullDecorViewWindow(maybeDecorView: View): Window? {
    return decorViewClass?.let { decorViewClass ->
      if (decorViewClass.isInstance(maybeDecorView)) {
        mWindowField?.let { mWindowField ->
          mWindowField[maybeDecorView] as Window
        }
      } else {
        null
      }
    }
  }
}