package curtains

import android.view.MotionEvent
import android.view.View
import android.view.Window

/**
 * Listener added to [Curtains.rootViewAttachStateListeners].
 * If you only care about either attached or detached, consider implementing [ViewAttachedListener]
 * or [ViewDetachedListener] instead.
 */
fun interface ViewAttachStateListener {
  /**
   * Called when [android.view.WindowManager.addView] and [android.view.WindowManager.removeView]
   * are called.
   */
  fun onViewAttachStateChanged(
    view: View,
    attached: Boolean
  )
}

/**
 * Listener added to [Curtains.rootViewAttachStateListeners].
 */
fun interface ViewAttachedListener : ViewAttachStateListener {
  override fun onViewAttachStateChanged(
    view: View,
    attached: Boolean
  ) {
    if (attached) {
      onViewAttached(view)
    }
  }

  /**
   * Called when [android.view.WindowManager.addView] is called.
   */
  fun onViewAttached(view: View)
}

/**
 * Listener added to [Curtains.rootViewAttachStateListeners].
 */
fun interface ViewDetachedListener : ViewAttachStateListener {
  override fun onViewAttachStateChanged(
    view: View,
    attached: Boolean
  ) {
    if (!attached) {
      onViewDetached(view)
    }
  }

  /**
   * Called when [android.view.WindowManager.removeView] is called.
   */
  fun onViewDetached(view: View)
}

/**
 * Listener added to [Curtains.windowAttachStateListeners].
 * If you only care about either attached or detached, consider implementing [WindowAttachedListener]
 * or [WindowDetachedListener] instead.
 */
fun interface WindowAttachStateListener {
  /**
   * Called when [android.view.WindowManager.addView] and [android.view.WindowManager.removeView]
   * are called and the provided view is a decor view wrapping a [android.view.Window] instance.
   */
  fun onWindowAttachStateChanged(
    window: Window,
    attached: Boolean
  )
}

/**
 * Listener added to [Curtains.windowAttachStateListeners].
 */
fun interface WindowAttachedListener : WindowAttachStateListener {
  override fun onWindowAttachStateChanged(
    window: Window,
    attached: Boolean
  ) {
    if (attached) {
      onWindowAttached(window)
    }
  }

  /**
   * Called when [android.view.WindowManager.addView] is called and the provided view is a decor
   * view wrapping a [android.view.Window] instance.
   */
  fun onWindowAttached(window: Window)
}

/**
 * Listener added to [Curtains.windowAttachStateListeners].
 */
fun interface WindowDetachedListener : WindowAttachStateListener {
  override fun onWindowAttachStateChanged(
    window: Window,
    attached: Boolean
  ) {
    if (!attached) {
      onWindowDetached(window)
    }
  }

  /**
   * Called when [android.view.WindowManager.removeView] is called and the provided view is a decor
   * view wrapping a [android.view.Window] instance.
   */
  fun onWindowDetached(window: Window)
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
