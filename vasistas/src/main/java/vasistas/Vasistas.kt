package vasistas

import android.view.View
import android.view.Window
import vasistas.internal.RootViewsSpy
import vasistas.internal.WindowSpy
import vasistas.internal.checkMainThread
import kotlin.LazyThreadSafetyMode.NONE

object Vasistas {

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