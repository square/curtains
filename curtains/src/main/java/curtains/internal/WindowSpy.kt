package curtains.internal

import android.view.View
import android.view.Window
import com.squareup.curtains.R
import curtains.AttachState
import curtains.ViewAttachStateListener
import curtains.WindowAttachStateListener
import curtains.window
import java.util.concurrent.CopyOnWriteArrayList

internal class WindowSpy private constructor(private val rootViewsSpy: RootViewsSpy) :
  ViewAttachStateListener {

  val listeners = CopyOnWriteArrayList<WindowAttachStateListener>()

  fun windowListCopy() = rootViewsSpy.delegatingViewList.mapNotNull { view ->
    DecorViewSpy.pullDecorViewWindow(view)
  }

  override fun onViewAttachStateChanged(
    view: View,
    attachState: AttachState
  ) {
    if (attachState.attached) {
      val previousAttachCount = view.getTag(R.id.curtain_window_attach_count) as Int? ?: 0
      view.setTag(R.id.curtain_window_attach_count, previousAttachCount + 1)
    }
    view.window?.let { window ->
      listeners.forEach {
        it.onWindowAttachStateChanged(window, attachState)
      }
    }
  }

  companion object {
    fun install(rootViewsSpy: RootViewsSpy): WindowSpy {
      return WindowSpy(rootViewsSpy).apply { rootViewsSpy.listeners += this }
    }
  }
}