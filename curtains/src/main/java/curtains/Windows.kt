package curtains

import android.view.MotionEvent
import android.view.View
import android.view.Window
import curtains.internal.DecorViewSpy
import curtains.internal.NextDrawListener.Companion.onNextDraw
import curtains.internal.WindowDelegateCallback.Companion.listeners
import curtains.internal.checkMainThread

val View.window: Window?
  get() {
    checkMainThread()
    return DecorViewSpy.pullDecorViewWindow(rootView)
  }

val Window.beforeDispatchTouchEventListeners: MutableList<(MotionEvent) -> DispatchState>
  get() {
    checkMainThread()
    return listeners.beforeDispatchTouchEventListeners
  }

val Window.afterDispatchTouchEventListeners: MutableList<(MotionEvent, DispatchState) -> Unit>
  get() {
    checkMainThread()
    return listeners.afterDispatchTouchEventListeners
  }

val Window.onContentChangedListeners: MutableList<() -> Unit>
  get() {
    checkMainThread()
    return listeners.onContentChangedListeners
  }

val Window.onWindowFocusChangedListeners: MutableList<(FocusState) -> Unit>
  get() {
    checkMainThread()
    return listeners.onWindowFocusChangedListeners
  }

fun Window.onDecorViewReady(onDecorViewReady: (View) -> Unit) {
  checkMainThread()
  val decorViewOrNull = peekDecorView()
  if (decorViewOrNull != null) {
    onDecorViewReady(decorViewOrNull)
  } else {
    listeners.run {
      onContentChangedListeners += object : () -> Unit {
        override fun invoke() {
          onDecorViewReady(peekDecorView())
          onContentChangedListeners -= this
        }
      }
    }
  }
}

fun Window.onNextDraw(onNextDraw: () -> Unit) {
  onDecorViewReady { decorView ->
    decorView.onNextDraw(onNextDraw)
  }
}