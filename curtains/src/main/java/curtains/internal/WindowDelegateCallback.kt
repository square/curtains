package curtains.internal

import android.view.MotionEvent
import android.view.Window
import curtains.DispatchState

internal class WindowDelegateCallback constructor(
  private val delegate: Window.Callback,
  private val listeners: WindowListeners
) : Window.Callback by delegate {

  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    return if (event != null) {
      val iterator = listeners.touchEventInterceptors.iterator()

      val proceed: (MotionEvent) -> DispatchState = object : (MotionEvent) -> DispatchState {
        override fun invoke(interceptedEvent: MotionEvent): DispatchState {
          return if (iterator.hasNext()) {
            val nextInterceptor = iterator.next()
            nextInterceptor.intercept(interceptedEvent, this)
          } else {
            DispatchState.from(delegate.dispatchTouchEvent(interceptedEvent))
          }
        }
      }

      if (iterator.hasNext()) {
        val firstInterceptor = iterator.next()
        firstInterceptor.intercept(event, proceed)
      } else {
        DispatchState.from(delegate.dispatchTouchEvent(event))
      }.consumed
    } else {
      delegate.dispatchTouchEvent(event)
    }
  }

  override fun onContentChanged() {
    listeners.onContentChangedListeners.forEach { it.onContentChanged() }
    delegate.onContentChanged()
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