package vasistas.internal

import android.view.MotionEvent
import android.view.Window
import java.util.concurrent.CopyOnWriteArrayList

internal class WindowDelegateCallback constructor(
  private val delegate: Window.Callback
) : Window.Callback by delegate {

  val dispatchTouchEventListener = CopyOnWriteArrayList<(MotionEvent) -> Boolean>()

  val afterDispatchTouchEventListener = CopyOnWriteArrayList<(MotionEvent, Boolean) -> Unit>()

  val onContentChangedListeners = CopyOnWriteArrayList<() -> Unit>()

  val onWindowFocusChangedListeners = CopyOnWriteArrayList<(Boolean) -> Unit>()

  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    return if (event != null) {
      val consumedBy = dispatchTouchEventListener.firstOrNull {
        it(event)
      }
      val consumed = if (consumedBy == null) {
        delegate.dispatchTouchEvent(event)
      } else {
        true
      }
      afterDispatchTouchEventListener.forEach { it(event, consumed) }
      consumed
    } else {
      delegate.dispatchTouchEvent(event)
    }
  }

  override fun onContentChanged() {
    onContentChangedListeners.forEach { it() }
    delegate.onContentChanged()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    onWindowFocusChangedListeners.forEach { it(hasFocus) }
    delegate.onWindowFocusChanged(hasFocus)
  }

  companion object {

    internal fun Window.wrapCallback(): WindowDelegateCallback? {
      return when (val currentCallback = callback) {
        // We expect a window to always have a default callback
        // that we can delegate to, but who knows what apps can be up to.
        null -> null
        is WindowDelegateCallback -> currentCallback
        else -> {
          val newCallback = WindowDelegateCallback(currentCallback)
          callback = newCallback
          newCallback
        }
      }
    }
  }
}