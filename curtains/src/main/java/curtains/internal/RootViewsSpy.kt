package curtains.internal

import android.view.View
import curtains.AttachState
import curtains.ViewAttachStateListener
import curtains.WindowAttachStateListener
import java.util.concurrent.CopyOnWriteArrayList

internal class RootViewsSpy private constructor() {

  val listeners = CopyOnWriteArrayList<ViewAttachStateListener>()

  fun rootViewListCopy() = delegatingViewList.toList()

  val delegatingViewList = object : ArrayList<View>() {
    override fun add(element: View): Boolean {
      listeners.forEach { it.onViewAttachStateChanged(element, AttachState.ATTACHED) }
      return super.add(element)
    }

    override fun removeAt(index: Int): View {
      val removedView = super.removeAt(index)
      listeners.forEach { it.onViewAttachStateChanged(removedView, AttachState.DETACHED) }
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