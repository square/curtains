package curtains.internal

import android.os.Build
import android.view.View
import curtains.RootViewListener
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A utility that holds the list of root views that WindowManager updates.
 */
internal class RootViewsSpy private constructor() {

  val listeners = CopyOnWriteArrayList<RootViewListener>()

  fun copyRootViewList(): List<View> {
    return if (Build.VERSION.SDK_INT >= 19) {
      delegatingViewList.toList()
    } else {
      WindowManagerSpy.windowManagerMViewsArray().toList()
    }
  }

  private val delegatingViewList = object : ArrayList<View>() {
    override fun add(element: View): Boolean {
      listeners.forEach { it.onRootViewsChanged(element, true) }
      return super.add(element)
    }

    override fun removeAt(index: Int): View {
      val removedView = super.removeAt(index)
      listeners.forEach { it.onRootViewsChanged(removedView, false) }
      return removedView
    }
  }

  companion object {
    fun install(): RootViewsSpy {
      return RootViewsSpy().apply {
        WindowManagerSpy.swapWindowManagerGlobalMViews { mViews ->
          delegatingViewList.apply { addAll(mViews) }
        }
      }
    }
  }
}