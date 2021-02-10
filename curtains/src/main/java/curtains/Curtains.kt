package curtains

import android.view.View
import android.view.Window
import curtains.Curtains.rootViews
import curtains.Curtains.rootViewListeners
import curtains.internal.RootViewsSpy
import curtains.internal.checkMainThread
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Lift the curtain on Android Windows!
 *
 * [Curtains] is the entry point to retrieve the list of root views, and listen to newly added or ]
 * removed ones. This is done by reaching into [android.view.WindowManagerGlobal].
 *
 * [android.view.WindowManagerGlobal] is an internal Android Framework class that maintains the list
 * of "root views" that your process manages. Each of these root view corresponds to a window from
 * the system perspective, however not all of these root views will have a [Window] associated with
 * it: the window / root view for toasts has no [Window], and neither does any root view directly
 * added by calling [android.view.WindowManager.addView].
 *
 * [rootViews] returns a snapshot of the currently attached root views.
 *
 * All properties defined in this class must be accessed from the main thread and will otherwise
 * throw an [IllegalStateException].
 *
 * [rootViewListeners] allows apps to have a central place where they can interact with
 * newly added or removed windows. It's exposed as a mutable list to allow apps to reorder or
 * remove listeners added by libraries.
 */
object Curtains {

  private val rootViewsSpy by lazy(NONE) {
    RootViewsSpy.install()
  }

  /**
   * @returns a copy of the list of root views held by [android.view.WindowManagerGlobal].
   * @throws IllegalStateException if not called from the main thread.
   */
  @JvmStatic
  val rootViews: List<View>
    get() {
      checkMainThread()
      return rootViewsSpy.copyRootViewList()
    }

  /**
   * The list of listeners for newly attached or detached root views. It is safe to update this
   * list from within [RootViewListener.onRootViewsChanged].
   *
   * If you only care about the attached state, you can implement the SAM interface
   * [RootViewAddedListener] which extends [RootViewListener].
   *
   * If you only care about the detached state, you can implement the SAM interface
   * [RootViewRemovedListener] which extends [RootViewListener].
   *
   * Note: The listeners are invoked immediately when [android.view.WindowManager.addView] and
   * [android.view.WindowManager.removeView] are called. [android.view.WindowManager.addView]
   * happens BEFORE [View.onAttachedToWindow] is invoked, as a view only becomes attached on the
   * next view traversal.
   *
   * No-op below Android API 19, i.e. any listener added here will never be triggered.
   *
   * @throws IllegalStateException if not called from the main thread.
   */
  @JvmStatic
  val rootViewListeners: MutableList<RootViewListener>
    get() {
      checkMainThread()
      return rootViewsSpy.listeners
    }
}
