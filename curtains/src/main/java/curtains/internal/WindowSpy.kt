package curtains.internal

import android.os.Build
import android.view.View
import android.view.Window
import curtains.ViewAttachStateListener
import curtains.WindowAttachStateListener
import curtains.window
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Builds on top of [RootViewsSpy] and [DecorViewSpy] to listen to updates to the
 * list of attached windows.
 */
internal class WindowSpy private constructor(private val rootViewsSpy: RootViewsSpy) :
  ViewAttachStateListener {

  val listeners = CopyOnWriteArrayList<WindowAttachStateListener>()

  fun windowListCopy(): List<Window> {
    return if (Build.VERSION.SDK_INT >= 19) {
      rootViewsSpy.delegatingViewList.mapNotNull { view ->
        DecorViewSpy.pullDecorViewWindow(view)
      }
    } else {
      WindowManagerSpy.windowManagerMViewsArray()
        .mapNotNull { view ->
          DecorViewSpy.pullDecorViewWindow(view)
        }
    }
  }

  override fun onViewAttachStateChanged(
    view: View,
    attached: Boolean
  ) {
    view.window?.let { window ->
      listeners.forEach {
        it.onWindowAttachStateChanged(window, attached)
      }
    }
  }

  companion object {
    fun install(rootViewsSpy: RootViewsSpy): WindowSpy {
      return WindowSpy(rootViewsSpy).apply { rootViewsSpy.listeners += this }
    }
  }
}