package curtains.internal

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View

/**
 * Enables replacing WindowManagerGlobal.mViews with a custom ArrayList implementation.
 *
 * Inspired from https://github.com/android/android-test/blob/master/espresso/core/java/androidx/test/espresso/base/RootsOracle.java
 */
internal object WindowManagerSpy {

  // You can discourage me all you want I'll still do it.
  @SuppressLint("PrivateApi", "ObsoleteSdkInt", "DiscouragedPrivateApi")
  fun swapViewManagerGlobalMViews(swap: (ArrayList<View>) -> ArrayList<View>) {
    if (Build.VERSION.SDK_INT < 19) {
      return
    }
    try {
      val windowManagerGlobalClass = Class.forName("android.view.WindowManagerGlobal")
      val windowManagerGlobalInstance =
        windowManagerGlobalClass.getDeclaredMethod("getInstance").invoke(null)

      val mViewsField =
        windowManagerGlobalClass.getDeclaredField("mViews").apply { isAccessible = true }

      @Suppress("UNCHECKED_CAST")
      val mViews = mViewsField[windowManagerGlobalInstance] as ArrayList<View>

      mViewsField[windowManagerGlobalInstance] = swap(mViews)
    } catch (ignored: Throwable) {
      Log.w("WindowManagerSpy", ignored)
    }
  }
}