package curtains

import android.view.View
import android.view.Window
import curtains.internal.RootViewsSpy
import curtains.internal.WindowSpy
import curtains.internal.checkMainThread
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Was ist das? The missing Android Window APIs!
 *
 * [Curtains] is the entry point to retrieve the list of currently attached root views and windows,
 * and listen to newly added or removed ones.
 *
 * [Curtains] only works on Android API level 19+ and is otherwise a no op.
 *
 *
 */
object Curtains {

  private val rootViewsSpy by lazy(NONE) {
    RootViewsSpy.install()
  }

  private val windowSpy by lazy(NONE) { WindowSpy.install(rootViewsSpy) }

  /**
   * @returns a copy of the list of root views held by WindowManagerGlobal.
   */
  val attachedRootViews: List<View>
    get() {
      checkMainThread()
      return rootViewsSpy.rootViewListCopy()
    }

  /**
   * @returns a copy of the list of windows held by WindowManagerGlobal.
   * That list is based on a subset of [attachedRootViews] for views that
   * are instances of DecorView.
   */
  val attachedWindows: List<Window>
    get() {
      checkMainThread()
      return windowSpy.windowListCopy()
    }

  val rootViewAttachListeners: MutableList<(View, AttachState) -> Unit>
    get() {
      checkMainThread()
      return rootViewsSpy.listeners
    }

  val windowAttachListeners: MutableList<(Window, AttachState) -> Unit>
    get() {
      checkMainThread()
      return windowSpy.listeners
    }
}
