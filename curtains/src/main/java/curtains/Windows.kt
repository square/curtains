package curtains

import android.view.JavaViewSpy
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

val Window.touchEventInterceptors: MutableList<TouchEventInterceptor>
  get() {
    checkMainThread()
    return listeners.touchEventInterceptors
  }

val Window.onContentChangedListeners: MutableList<OnContentChangedListener>
  get() {
    checkMainThread()
    return listeners.onContentChangedListeners
  }

fun Window.onDecorViewReady(onDecorViewReady: (View) -> Unit) {
  checkMainThread()
  val decorViewOrNull = peekDecorView()
  if (decorViewOrNull != null) {
    onDecorViewReady(decorViewOrNull)
  } else {
    listeners.run {
      onContentChangedListeners += object : OnContentChangedListener {
        override fun onContentChanged() {
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

val View.windowAttachCount: Int
  get() {
    checkMainThread()
    return JavaViewSpy.windowAttachCount(this)
  }
