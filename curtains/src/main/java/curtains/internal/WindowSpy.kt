package curtains.internal

import android.view.View
import android.view.Window
import curtains.AttachState
import curtains.window
import java.util.concurrent.CopyOnWriteArrayList

internal class WindowSpy private constructor(private val rootViewsSpy: RootViewsSpy) :
  (View, AttachState) -> Unit {

  val listeners = CopyOnWriteArrayList<(Window, AttachState) -> Unit>()

  fun windowListCopy() = rootViewsSpy.delegatingViewList.mapNotNull { view ->
    DecorViewSpy.pullDecorViewWindow(view)
  }

  override fun invoke(
    view: View,
    attachState: AttachState
  ) {
    view.window?.let { window ->
      listeners.forEach {
        it(window, attachState)
      }
    }
  }

  companion object {
    fun install(rootViewsSpy: RootViewsSpy): WindowSpy {
      return WindowSpy(rootViewsSpy).apply { rootViewsSpy.listeners += this }
    }
  }


}