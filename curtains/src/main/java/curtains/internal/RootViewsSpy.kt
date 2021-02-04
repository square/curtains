package curtains.internal

import android.view.View
import curtains.ViewAttachStateListener
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A utility that holds the list of root views that WindowManager updates.
 */
internal class RootViewsSpy private constructor() {

  val listeners = CopyOnWriteArrayList<ViewAttachStateListener>()

  fun rootViewListCopy() = delegatingViewList.toList()

  val delegatingViewList = object : ArrayList<View>() {
    override fun add(element: View): Boolean {
      listeners.forEach { it.onViewAttachStateChanged(element, true) }
      return super.add(element)
    }

    override fun removeAt(index: Int): View {
      val removedView = super.removeAt(index)
      listeners.forEach { it.onViewAttachStateChanged(removedView, false) }
      return removedView
    }
  }

  companion object {
    fun install(): RootViewsSpy {
      return RootViewsSpy().apply {
        WindowManagerSpy.swapViewManagerGlobalMViews { mViews ->
          delegatingViewList.apply { addAll(mViews) }
        }
      }
    }
  }
}