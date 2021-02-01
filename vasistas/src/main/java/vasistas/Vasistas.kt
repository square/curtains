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
  val rootViews: List<View>
    get() {
      checkMainThread()
      // Defensive copy.
      return rootViewsSpy.all.toList()
    }

  val windows: List<Window>
    get() {
      checkMainThread()
      // Returns a one time list
      return windowSpy.all
    }

  fun addRootViewListener(listener: RootViewListener) {
    checkMainThread()
    rootViewsSpy.listeners += listener
  }

  fun removeRootViewListener(listener: RootViewListener) {
    checkMainThread()
    rootViewsSpy.listeners -= listener
  }

  fun addWindowListener(listener: WindowListener) {
    checkMainThread()
    windowSpy.listeners += listener
  }

  fun removeWindowListener(listener: WindowListener) {
    checkMainThread()
    windowSpy.listeners -= listener
  }

  operator fun plusAssign(listener: RootViewListener) {
    addRootViewListener(listener)
  }

  operator fun minusAssign(listener: RootViewListener) {
    removeRootViewListener(listener)
  }

  operator fun plusAssign(listener: WindowListener) {
    addWindowListener(listener)
  }

  operator fun minusAssign(listener: WindowListener) {
    removeWindowListener(listener)
  }
}