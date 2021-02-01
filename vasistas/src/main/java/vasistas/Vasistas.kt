package vasistas

import android.view.View
import vasistas.internal.RootViewsSpy
import vasistas.internal.checkMainThread

object Vasistas {

  private val rootViewsSpy by lazy(LazyThreadSafetyMode.NONE) {
    RootViewsSpy.install()
  }

  /**
   * @returns a copy of the list of root views held by WindowManagerGlobal.
   */
  val rootViews: List<View>
    get() {
      checkMainThread()
      return rootViewsSpy.all.toList()
    }

  fun addRootViewListener(listener: RootViewListener) {
    checkMainThread()
    rootViewsSpy.listeners += listener
  }

  fun removeRootViewListener(listener: RootViewListener) {
    checkMainThread()
    rootViewsSpy.listeners -= listener
  }

  operator fun plusAssign(listener: RootViewListener) {
    addRootViewListener(listener)
  }

  operator fun minusAssign(listener: RootViewListener) {
    removeRootViewListener(listener)
  }
}