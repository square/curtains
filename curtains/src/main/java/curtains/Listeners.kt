package curtains

import android.view.MotionEvent
import android.view.View
import android.view.Window

/**
 * Listener added to [Curtains.rootViewListeners].
 * If you only care about either attached or detached, consider implementing [RootViewAddedListener]
 * or [RootViewRemovedListener] instead.
 */
fun interface RootViewListener {
  /**
   * Called when [android.view.WindowManager.addView] and [android.view.WindowManager.removeView]
   * are called.
   */
  fun onRootViewsChanged(
    view: View,
    added: Boolean
  )
}

/**
 * Listener added to [Curtains.rootViewListeners].
 */
fun interface RootViewAddedListener : RootViewListener {
  override fun onRootViewsChanged(
    view: View,
    added: Boolean
  ) {
    if (added) {
      onRootViewAdded(view)
    }
  }

  /**
   * Called when [android.view.WindowManager.addView] is called.
   */
  fun onRootViewAdded(view: View)
}

/**
 * Listener added to [Curtains.rootViewListeners].
 */
fun interface RootViewRemovedListener : RootViewListener {
  override fun onRootViewsChanged(
    view: View,
    added: Boolean
  ) {
    if (!added) {
      onRootViewRemoved(view)
    }
  }

  /**
   * Called when [android.view.WindowManager.removeView] is called.
   */
  fun onRootViewRemoved(view: View)
}

/**
 * Interceptor added to [Window.touchEventInterceptors].
 *
 * If you only care about logging touch events without intercepting, consider implementing
 * [TouchEventListener] instead.
 */
fun interface TouchEventInterceptor {
  /**
   * Called when [android.view.Window.Callback.dispatchTouchEvent] is called.
   *
   * Implementations should either return [DispatchState.Consumed] (which intercepts the touch
   * event) or return the result of calling [dispatch]. Implementations can also pass through
   * a copy of [motionEvent] (for example to fix bugs where the OS sends broken events).
   */
  fun intercept(
    motionEvent: MotionEvent,
    dispatch: (MotionEvent) -> DispatchState
  ): DispatchState
}

/**
 * Listener added to [Window.touchEventInterceptors].
 */
fun interface TouchEventListener : TouchEventInterceptor {
  override fun intercept(
    motionEvent: MotionEvent,
    dispatch: (MotionEvent) -> DispatchState
  ): DispatchState {
    onTouchEvent(motionEvent)
    return dispatch(motionEvent)
  }

  /**
   * Called when [android.view.Window.Callback.dispatchTouchEvent] is called.
   */
  fun onTouchEvent(motionEvent: MotionEvent)
}

/**
 * Listener added to [Window.onContentChangedListeners].
 */
fun interface OnContentChangedListener {
  /**
   * Called when [android.view.Window.Callback.onContentChanged] is called.
   */
  fun onContentChanged()
}
