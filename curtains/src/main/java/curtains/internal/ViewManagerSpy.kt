package curtains.internal

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.View

internal object ViewManagerSpy {

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
      Log.w("ViewManagerSpy", ignored)
    }
  }
}