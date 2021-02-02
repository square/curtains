package vasistas.internal

import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewTreeObserver.OnDrawListener

internal class NextDrawListener(
  private val view: View,
  private val onDrawCallback: () -> Unit
) : OnDrawListener, OnAttachStateChangeListener {
  private var invoked = false

  override fun onDraw() {
    if (invoked) return
    invoked = true
    view.removeOnAttachStateChangeListener(this)
    // ViewTreeObserver.removeOnDrawListener() throws if called from the onDraw() callback
    mainHandler.post {
      view.viewTreeObserver.let { viewTreeObserver ->
        if (viewTreeObserver.isAlive) {
          viewTreeObserver.removeOnDrawListener(this)
        }
      }
    }
    onDrawCallback()
  }

  fun safelyRegisterForNextDraw() {
    // Prior to API 26, OnDrawListener wasn't merged back from the floating ViewTreeObserver into
    // the real ViewTreeObserver.
    // https://android.googlesource.com/platform/frameworks/base/+/9f8ec54244a5e0343b9748db3329733f259604f3
    if (view.viewTreeObserver.isAlive && view.isAttachedToWindow) {
      view.viewTreeObserver.addOnDrawListener(this)
    } else {
      view.addOnAttachStateChangeListener(this)
    }
  }

  override fun onViewAttachedToWindow(view: View) {
    view.viewTreeObserver.addOnDrawListener(this)
    // Backed by CopyOnWriteArrayList, ok to self remove from onViewDetachedFromWindow()
    view.removeOnAttachStateChangeListener(this)
  }

  override fun onViewDetachedFromWindow(view: View) {
    view.viewTreeObserver.removeOnDrawListener(this)
    // Backed by CopyOnWriteArrayList, ok to self remove from onViewDetachedFromWindow()
    view.removeOnAttachStateChangeListener(this)
  }

  companion object {
    fun View.onNextDraw(onDrawCallback: () -> Unit) {
      val nextDrawListener = NextDrawListener(this, onDrawCallback)
      nextDrawListener.safelyRegisterForNextDraw()
    }
  }
}