package curtains

import android.view.MotionEvent
import android.view.View
import android.view.Window

fun interface ViewAttachStateListener {
  fun onViewAttachStateChanged(
    view: View,
    attachState: AttachState
  )
}

fun interface ViewAttachedListener : ViewAttachStateListener {
  override fun onViewAttachStateChanged(
    view: View,
    attachState: AttachState
  ) {
    if (attachState.attached) {
      onViewAttached(view)
    }
  }

  fun onViewAttached(view: View)
}

fun interface ViewDetachedListener : ViewAttachStateListener {
  override fun onViewAttachStateChanged(
    view: View,
    attachState: AttachState
  ) {
    if (!attachState.attached) {
      onViewDetached(view)
    }
  }

  fun onViewDetached(view: View)
}

fun interface WindowAttachStateListener {
  fun onWindowAttachStateChanged(
    window: Window,
    attachState: AttachState
  )
}

fun interface WindowAttachedListener : WindowAttachStateListener {
  override fun onWindowAttachStateChanged(
    window: Window,
    attachState: AttachState
  ) {
    if (attachState.attached) {
      onWindowAttached(window)
    }
  }

  fun onWindowAttached(window: Window)
}

fun interface WindowDetachedListener : WindowAttachStateListener {
  override fun onWindowAttachStateChanged(
    window: Window,
    attachState: AttachState
  ) {
    if (!attachState.attached) {
      onWindowDetached(window)
    }
  }

  fun onWindowDetached(window: Window)
}

fun interface OnContentChangedListener {
  fun onContentChanged()
}

fun interface TouchEventInterceptor {
  fun intercept(
    motionEvent: MotionEvent,
    proceed: (MotionEvent) -> DispatchState
  ): DispatchState
}

fun interface TouchEventListener : TouchEventInterceptor {
  override fun intercept(
    motionEvent: MotionEvent,
    proceed: (MotionEvent) -> DispatchState
  ): DispatchState {
    onTouchEvent(motionEvent)
    return proceed(motionEvent)
  }
  fun onTouchEvent(motionEvent: MotionEvent)
}