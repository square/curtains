package vasistas

import android.view.Window

interface WindowListener {
  fun onWindowAdded(window: Window) {}
  fun onWindowRemoved(window: Window) {}
}

fun onWindowAddedListener(block: (Window) -> Unit): WindowListener {
  return object : WindowListener {
    override fun onWindowAdded(window: Window) {
      block(window)
    }
  }
}

fun onWindowRemovedListener(block: (Window) -> Unit): WindowListener {
  return object : WindowListener {
    override fun onWindowRemoved(window: Window) {
      block(window)
    }
  }
}