package curtains.internal

import android.os.Build
import android.view.View
import curtains.OnRootViewsChangedListener
import java.util.concurrent.CopyOnWriteArrayList

/**
 * A utility that holds the list of root views that WindowManager updates.
 */
internal class RootViewsSpy private constructor() {

  val listeners = CopyOnWriteArrayList<OnRootViewsChangedListener>()

  private var delegatingViewList: ArrayList<View>? = null

  fun copyRootViewList(): List<View> {
    return if (Build.VERSION.SDK_INT >= 19) {
      delegatingViewList?.toList() ?: emptyList()
    } else {
      WindowManagerSpy.windowManagerMViewsArray().toList()
    }
  }

  companion object {
    fun install(): RootViewsSpy {
      return RootViewsSpy().apply {
        WindowManagerSpy.swapWindowManagerGlobalMViews { mViews ->
          buildDelegatingViewList(mViews, listeners).also { delegatingViewList = it }
        }
      }
    }

    /**
     * Builds the [ArrayList] that replaces `WindowManagerGlobal.mViews`, notifying [listeners]
     * whenever WindowManager adds or removes a root view.
     *
     * If another library has already swapped in its own observing list, [previous] is a custom
     * [ArrayList] subclass rather than the framework's plain `ArrayList`. In that case this forwards
     * add/remove to it so it keeps receiving root-view callbacks. Previously the prior list was
     * simply discarded, so whichever library installed last silently disabled every earlier one
     * (e.g. two window-observing SDKs in the same app). When [previous] is a plain `ArrayList`
     * (the normal first-install case) there is no prior observer and behavior is unchanged.
     *
     * Extracted and made `internal` so the chaining behavior can be unit tested without reflecting
     * into `WindowManagerGlobal`.
     */
    internal fun buildDelegatingViewList(
      previous: ArrayList<View>,
      listeners: List<OnRootViewsChangedListener>,
    ): ArrayList<View> {
      // A plain ArrayList means no other observer is installed yet, so there is nothing to preserve.
      val priorObserver = previous.takeIf { it.javaClass != ArrayList::class.java }
      return object : ArrayList<View>() {
        override fun add(element: View): Boolean {
          listeners.forEach { it.onRootViewsChanged(element, true) }
          priorObserver?.add(element)
          return super.add(element)
        }

        override fun removeAt(index: Int): View {
          val removedView = super.removeAt(index)
          listeners.forEach { it.onRootViewsChanged(removedView, false) }
          priorObserver?.let { if (index < it.size) it.removeAt(index) }
          return removedView
        }
      }.apply {
        // Seed with the views WindowManager already tracks. ArrayList.addAll copies in bulk without
        // invoking add(), so this neither re-notifies listeners nor re-forwards to priorObserver
        // (which already holds these views).
        addAll(previous)
      }
    }
  }
}
