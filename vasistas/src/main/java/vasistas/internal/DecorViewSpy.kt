package vasistas.internal

import android.annotation.SuppressLint
import android.view.View
import android.view.Window
import kotlin.LazyThreadSafetyMode.NONE

@SuppressLint("PrivateApi")
internal object DecorViewSpy {

  private val decorViewClass by lazy(NONE) {
    try {
      Class.forName("com.android.internal.policy.DecorView")
    } catch (ignored: ClassNotFoundException) {
      // In some later release of Android 6 / API 23 DecorView was moved out of PhoneWindow.
      // https://cs.android.com/android/_/android/platform/frameworks/base/+
      // /8804af2b63b0584034f7ec7d4dc701d06e6a8754
      try {
        Class.forName("com.android.internal.policy.PhoneWindow\$DecorView")
      } catch (ignored: ClassNotFoundException) {
        null
      }
    }
  }

  private val mWindowField by lazy(NONE) {
    decorViewClass?.let { decorViewClass ->
      try {
        decorViewClass.getDeclaredField("mWindow").apply { isAccessible = true }
      } catch (ignored: NoSuchFieldException) {
        // In Android 6 / API 23 PhoneWindow.DecorView became an inner static class. Before, it had
        // a direct reference to its outer PhoneWindow class.
        // https://cs.android.com/android/_/android/platform/frameworks/base/+
        // /0daf2102a20d224edeb4ee45dd4ee91889ef3e0c
        try {
          decorViewClass.getDeclaredField("this$0").apply { isAccessible = true }
        } catch (ignored: NoSuchFieldException) {
          null
        }
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