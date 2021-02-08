package curtains.internal

import android.view.MotionEvent
import android.view.Window
import curtains.DispatchState
import curtains.DispatchState.Consumed

/**
 * Replaces the default Window callback to allows adding listeners / interceptors
 * for interesting events.
 */
internal class WindowCallbackWrapper constructor(
  private val delegate: Window.Callback,
  private val listeners: WindowListeners
) : Window.Callback by delegate {

  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    return if (event != null) {
      val iterator = listeners.touchEventInterceptors.iterator()

      val dispatch: (MotionEvent) -> DispatchState = object : (MotionEvent) -> DispatchState {
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
        firstInterceptor.intercept(event, dispatch)
      } else {
        DispatchState.from(delegate.dispatchTouchEvent(event))
      } is Consumed
    } else {
      delegate.dispatchTouchEvent(event)
    }
  }

  override fun onContentChanged() {
    listeners.onContentChangedListeners.forEach { it.onContentChanged() }
    delegate.onContentChanged()
  }

  companion object {
    val Window.listeners: WindowListeners
      get() {
        return when (val currentCallback = callback) {
          // We expect a window to always have a default callback
          // that we can delegate to, but who knows what apps can be up to.
          null -> WindowListeners()
          is WindowCallbackWrapper -> currentCallback.listeners
          else -> {
            WindowListeners().apply {
              callback = WindowCallbackWrapper(currentCallback, this)
            }
          }
        }
      }

    val Window.wrappedCallbackOrNull: Window.Callback?
      get() {
        return when (val currentCallback = callback) {
          // We expect a window to always have a default callback
          // that we can delegate to, but who knows what apps can be up to.
          null -> null
          is WindowCallbackWrapper -> currentCallback.delegate
          else -> currentCallback
        }
      }
  }
}