package curtains.internal

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import android.view.View
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Enables replacing WindowManagerGlobal.mViews with a custom ArrayList implementation.
 *
 * Inspired from https://github.com/android/android-test/blob/master/espresso/core/java/androidx/test/espresso/base/RootsOracle.java
 */
internal object WindowManagerSpy {

  private val windowManagerClass by lazy(NONE) {
    val className = if (SDK_INT > 16) {
      "android.view.WindowManagerGlobal"
    } else {
      "android.view.WindowManagerImpl"
    }
    try {
      Class.forName(className)
    } catch (ignored: Throwable) {
      Log.w("WindowManagerSpy", ignored)
      null
    }
  }

  private val windowManagerInstance by lazy(NONE) {
    windowManagerClass?.let { windowManagerClass ->
      val methodName = if (SDK_INT > 16) {
        "getInstance"
      } else {
        "getDefault"
      }
      windowManagerClass.getMethod(methodName).invoke(null)
    }
  }

  private val mViewsField by lazy(NONE) {
    windowManagerClass?.let { windowManagerClass ->
      windowManagerClass.getDeclaredField("mViews").apply { isAccessible = true }
    }
  }

  // You can discourage me all you want I'll still do it.
  @SuppressLint("PrivateApi", "ObsoleteSdkInt", "DiscouragedPrivateApi")
  fun swapWindowManagerGlobalMViews(swap: (ArrayList<View>) -> ArrayList<View>) {
    if (SDK_INT < 19) {
      return
    }
    try {
      windowManagerInstance?.let { windowManagerInstance ->
        mViewsField?.let { mViewsField ->
          @Suppress("UNCHECKED_CAST")
          val mViews = mViewsField[windowManagerInstance] as ArrayList<View>
          mViewsField[windowManagerInstance] = swap(mViews)
        }
      }
    } catch (ignored: Throwable) {
      Log.w("WindowManagerSpy", ignored)
    }
  }

  fun windowManagerMViewsArray(): Array<View> {
    val sdkInt = SDK_INT
    if (sdkInt >= 19) {
      return arrayOf()
    }
    try {
      windowManagerInstance?.let { windowManagerInstance ->
        mViewsField?.let { mViewsField ->
          @Suppress("UNCHECKED_CAST")
          return mViewsField[windowManagerInstance] as Array<View>
        }
      }
    } catch (ignored: Throwable) {
      Log.w("WindowManagerSpy", ignored)
    }
    return arrayOf()
  }
}