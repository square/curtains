package curtains.internal

import android.view.MotionEvent
import android.view.Window
import curtains.DispatchState
import curtains.DispatchState.CONSUMED
import curtains.FocusState

internal class WindowDelegateCallback constructor(
  private val delegate: Window.Callback,
  private val listeners: WindowListeners
) : Window.Callback by delegate {

  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    return if (event != null) {
      val consumedBy = listeners.beforeDispatchTouchEventListeners.firstOrNull {
        it(event).consumed
      }
      val dispatchState = if (consumedBy == null) {
        DispatchState.from(delegate.dispatchTouchEvent(event))
      } else {
        CONSUMED
      }
      listeners.afterDispatchTouchEventListeners.forEach { it(event, dispatchState) }
      dispatchState.consumed
    } else {
      delegate.dispatchTouchEvent(event)
    }
  }

  override fun onContentChanged() {
    listeners.onContentChangedListeners.forEach { it() }
    delegate.onContentChanged()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    val state = FocusState.from(hasFocus)
    listeners.onWindowFocusChangedListeners.forEach { it(state) }
    delegate.onWindowFocusChanged(hasFocus)
  }

  companion object {
    internal val Window.listeners: WindowListeners
      get() {
        return when (val currentCallback = callback) {
          // We expect a window to always have a default callback
          // that we can delegate to, but who knows what apps can be up to.
          null -> WindowListeners()
          is WindowDelegateCallback -> currentCallback.listeners
          else -> {
            WindowListeners().apply {
              callback = WindowDelegateCallback(currentCallback, this)
            }
          }
        }
      }
  }
}