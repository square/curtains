package curtains

import android.view.View
import android.view.Window
import curtains.internal.RootViewsSpy
import curtains.internal.WindowSpy
import curtains.internal.checkMainThread
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Lift the curtain on Android Windows!
 *
 * [Curtains] is the entry point to retrieve the list of currently attached root views and windows,
 * and listen to newly added or removed ones. This is done by reaching into
 * [android.view.WindowManagerGlobal].
 *
 * [android.view.WindowManagerGlobal] is an internal Android Framework class that maintains the list
 * of "root views" that your process manages. Each of these root view corresponds to a window from
 * the system perspective, however not all of these root views will have a [Window] associated with
 * it: the window / root view for toasts has no [Window], and neither does any root view directly
 * added by calling [android.view.WindowManager.addView].
 *
 * [attachedRootViews] returns a snapshot of the currently attached root views, and
 * [attachedWindows] returns a snapshot of the associated currently attached [android.view.Window]
 * instances. The list returned by [attachedWindows] is a subset of the list of root views
 * returned by [attachedRootViews].
 *
 * All properties defined in this class must be accessed from the main thread and will otherwise
 * throw an [IllegalStateException].
 *
 * [rootViewAttachStateListeners] and [windowAttachStateListeners] allows apps to have a central
 * place where they can interact with newly added or removed windows. These are exposed as mutable
 * lists to allow apps to reorder or remove listeners added by libraries.
 *
 * Note: [Curtains] only works on Android API level 19+ and is otherwise a no op.
 */
object Curtains {

  private val rootViewsSpy by lazy(NONE) {
    RootViewsSpy.install()
  }

  private val windowSpy by lazy(NONE) { WindowSpy.install(rootViewsSpy) }

  /**
   * @returns a copy of the list of root views held by [android.view.WindowManagerGlobal].
   * @throws IllegalStateException if not called from the main thread.
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
   * @throws IllegalStateException if not called from the main thread.
   */
  val attachedWindows: List<Window>
    get() {
      checkMainThread()
      return windowSpy.windowListCopy()
    }

  /**
   * The list of listeners for newly attached or detached root views. It is safe to update this
   * list from within [ViewAttachStateListener.onViewAttachStateChanged].
   *
   * If you only care about the attached state, you can implement the SAM interface
   * [ViewAttachedListener] which extends [ViewAttachStateListener].
   *
   * If you only care about the detached state, you can implement the SAM interface
   * [ViewDetachedListener] which extends [ViewAttachStateListener].
   *
   * @throws IllegalStateException if not called from the main thread.
   */
  val rootViewAttachStateListeners: MutableList<ViewAttachStateListener>
    get() {
      checkMainThread()
      return rootViewsSpy.listeners
    }

  /**
   * The list of listeners for newly attached or detached [android.view.Window] instance. It is
   * safe to update this list from within [WindowAttachStateListener.onWindowAttachStateChanged].
   *
   * If you only care about the attached state, you can implement the SAM interface
   * [WindowAttachedListener] which extends [WindowAttachStateListener].
   *
   * If you only care about the detached state, you can implement the SAM interface
   * [WindowDetachedListener] which extends [WindowAttachStateListener].
   *
   * @throws IllegalStateException if not called from the main thread.
   */
  val windowAttachStateListeners: MutableList<WindowAttachStateListener>
    get() {
      checkMainThread()
      return windowSpy.listeners
    }
}
