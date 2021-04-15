package curtains

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.Window

/**
 * Listener added to [Curtains.onRootViewsChangedListeners].
 * If you only care about either attached or detached, consider implementing [OnRootViewAddedListener]
 * or [OnRootViewRemovedListener] instead.
 */
fun interface OnRootViewsChangedListener {
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
 * Listener added to [Curtains.onRootViewsChangedListeners].
 */
fun interface OnRootViewAddedListener : OnRootViewsChangedListener {
  /**
   * Called when [android.view.WindowManager.addView] is called.
   */
  fun onRootViewAdded(view: View)

  override fun onRootViewsChanged(
    view: View,
    added: Boolean
  ) {
    if (added) {
      onRootViewAdded(view)
    }
  }
}

/**
 * Listener added to [Curtains.onRootViewsChangedListeners].
 */
fun interface OnRootViewRemovedListener : OnRootViewsChangedListener {
  /**
   * Called when [android.view.WindowManager.removeView] is called.
   */
  fun onRootViewRemoved(view: View)

  override fun onRootViewsChanged(
    view: View,
    added: Boolean
  ) {
    if (!added) {
      onRootViewRemoved(view)
    }
  }
}

/**
 * Interceptor added to [Window.touchEventInterceptors].
 *
 * If you only care about logging touch events without intercepting, consider implementing
 * [OnTouchEventListener] instead.
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
fun interface OnTouchEventListener : TouchEventInterceptor {
  /**
   * Called when [android.view.Window.Callback.dispatchTouchEvent] is called.
   */
  fun onTouchEvent(motionEvent: MotionEvent)

  override fun intercept(
    motionEvent: MotionEvent,
    dispatch: (MotionEvent) -> DispatchState
  ): DispatchState {
    onTouchEvent(motionEvent)
    return dispatch(motionEvent)
  }
}

/**
 * Interceptor added to [Window.keyEventInterceptors].
 *
 * If you only care about logging key events without intercepting, consider implementing
 * [OnKeyEventListener] instead.
 */
fun interface KeyEventInterceptor {
  /**
   * Called when [android.view.Window.Callback.dispatchKeyEvent] is called.
   *
   * Implementations should either return [DispatchState.Consumed] (which intercepts the touch
   * event) or return the result of calling [dispatch]. Implementations can also pass through
   * a copy of [keyEvent].
   */
  fun intercept(
    keyEvent: KeyEvent,
    dispatch: (KeyEvent) -> DispatchState
  ): DispatchState
}

/**
 * Listener added to [Window.keyEventInterceptors].
 */
fun interface OnKeyEventListener : KeyEventInterceptor {
  /**
   * Called when [android.view.Window.Callback.dispatchKeyEvent] is called.
   */
  fun onKeyEvent(keyEvent: KeyEvent)

  override fun intercept(
    keyEvent: KeyEvent,
    dispatch: (KeyEvent) -> DispatchState
  ): DispatchState {
    onKeyEvent(keyEvent)
    return dispatch(keyEvent)
  }
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

/**
 * Listener added to [Window.onWindowFocusChangedListeners].
 */
fun interface OnWindowFocusChangedListener {
  /**
   * Called when [android.view.Window.Callback.onWindowFocusChanged] is called.
   */
  fun onWindowFocusChanged(hasFocus: Boolean)
}

fun interface OnWindowFocusGainedListener : OnWindowFocusChangedListener {

  fun onWindowFocusGained()

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    if (hasFocus) {
      onWindowFocusGained()
    }
  }
}

fun interface OnWindowFocusLostListener : OnWindowFocusChangedListener {

  fun onWindowFocusGained()

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    if (!hasFocus) {
      onWindowFocusGained()
    }
  }
}