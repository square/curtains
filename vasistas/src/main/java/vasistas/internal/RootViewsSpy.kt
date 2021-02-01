package vasistas.internal

import android.view.View
import vasistas.RootViewListener
import java.util.concurrent.CopyOnWriteArrayList

internal class RootViewsSpy {

  val listeners = CopyOnWriteArrayList<RootViewListener>()

  val all: List<View>
    get() = delegatingViewList

  private val delegatingViewList = object : ArrayList<View>() {
    override fun add(element: View): Boolean {
      listeners.forEach { it.onRootViewAdded(element) }
      return super.add(element)
    }

    override fun removeAt(index: Int): View {
      val removedView = super.removeAt(index)
      listeners.forEach { it.onRootViewRemoved(removedView) }
      return removedView
    }
  }

  companion object {
    fun install(): RootViewsSpy {
      return RootViewsSpy().apply {
        ViewManagerSpy.swapViewManagerGlobalMViews { mViews ->
          delegatingViewList.apply { addAll(mViews) }
        }
      }
    }
  }
}