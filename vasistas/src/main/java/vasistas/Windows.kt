package vasistas

import android.view.MotionEvent
import android.view.View
import android.view.Window
import vasistas.internal.DecorViewSpy
import vasistas.internal.NextDrawListener.Companion.onNextDraw
import vasistas.internal.WindowDelegateCallback.Companion.wrapCallback
import vasistas.internal.checkMainThread

val View.window: Window?
  get() {
    checkMainThread()
    return DecorViewSpy.pullDecorViewWindow(rootView)
  }

fun Window.addTouchEventListener(block: (MotionEvent) -> Boolean) {
  checkMainThread()
  wrapCallback()?.let {
    it.dispatchTouchEventListener += block
  }
}

fun Window.removeTouchEventListener(block: (MotionEvent) -> Boolean) {
  checkMainThread()
  wrapCallback()?.let {
    it.dispatchTouchEventListener -= block
  }
}

fun Window.addAfterDispatchTouchEventListener(block: (MotionEvent, Boolean) -> Unit) {
  checkMainThread()
  wrapCallback()?.let {
    it.afterDispatchTouchEventListener += block
  }
}

fun Window.removeAfterDispatchTouchEventListener(block: (MotionEvent, Boolean) -> Unit) {
  checkMainThread()
  wrapCallback()?.let {
    it.afterDispatchTouchEventListener -= block
  }
}

fun Window.addContentChangedListener(block: () -> Unit) {
  checkMainThread()
  wrapCallback()?.let {
    it.onContentChangedListeners += block
  }
}

fun Window.removeContentChangedListener(block: () -> Unit) {
  checkMainThread()
  wrapCallback()?.let {
    it.onContentChangedListeners -= block
  }
}

fun Window.addWindowFocusListener(block: (Boolean) -> Unit) {
  checkMainThread()
  wrapCallback()?.let {
    it.onWindowFocusChangedListeners += block
  }
}

fun Window.removeWindowFocusListener(block: (Boolean) -> Unit) {
  checkMainThread()
  wrapCallback()?.let {
    it.onWindowFocusChangedListeners -= block
  }
}

fun Window.onDecorViewReady(block: (View) -> Unit) {
  checkMainThread()
  val decorViewOrNull = peekDecorView()
  if (decorViewOrNull != null) {
    block(decorViewOrNull)
  } else {
    addContentChangedListener(object: () -> Unit {
      override fun invoke() {
        block(peekDecorView())
        removeContentChangedListener(this)
      }
    })
  }
}

fun Window.onNextDraw(block: () -> Unit) {
  onDecorViewReady { decorView ->
    decorView.onNextDraw(block)
  }
}