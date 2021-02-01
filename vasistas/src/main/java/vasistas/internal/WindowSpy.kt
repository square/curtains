package vasistas.internal

import android.view.View
import android.view.Window
import vasistas.RootViewListener
import vasistas.WindowListener
import vasistas.window
import java.util.concurrent.CopyOnWriteArrayList

internal class WindowSpy private constructor(private val rootViewsSpy: RootViewsSpy) :
  RootViewListener {

  val listeners = CopyOnWriteArrayList<WindowListener>()

  val all: List<Window>
    get() = rootViewsSpy.all.mapNotNull { it.window }

  override fun onRootViewAdded(view: View) {
    view.window?.let { window ->
      listeners.forEach {
        it.onWindowAdded(window)
      }
    }
  }

  override fun onRootViewRemoved(view: View) {
    view.window?.let { window ->
      listeners.forEach {
        it.onWindowRemoved(window)
      }
    }
  }

  companion object {
    fun install(rootViewsSpy: RootViewsSpy): WindowSpy {
      return WindowSpy(rootViewsSpy).apply { rootViewsSpy.listeners += this }
    }
  }
}