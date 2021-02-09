package curtains.internal

import android.os.Build
import android.view.View
import android.view.View.OnAttachStateChangeListener
import android.view.ViewTreeObserver.OnDrawListener
import androidx.annotation.RequiresApi

/**
 * A utility class to listen to the next ondraw call on a view hierarchy, working around AOSP bugs.
 */
@RequiresApi(16)
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
    if (Build.VERSION.SDK_INT >= 26 || (view.viewTreeObserver.isAlive && view.isAttachedToWindowCompat)) {
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

private val View.isAttachedToWindowCompat: Boolean
  get() {
    if (Build.VERSION.SDK_INT >= 19) {
      return isAttachedToWindow
    }
    return windowToken != null
  }