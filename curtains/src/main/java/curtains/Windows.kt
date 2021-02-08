package curtains

import android.view.View
import android.view.Window
import android.view.windowAttachCount
import curtains.internal.DecorViewSpy
import curtains.internal.NextDrawListener.Companion.onNextDraw
import curtains.internal.WindowCallbackWrapper.Companion.listeners
import curtains.internal.WindowCallbackWrapper.Companion.wrappedCallbackOrNull
import curtains.internal.checkMainThread

/**
 * Extracts the [android.view.Window] instance associated to this view hierarchy using
 * reflection, or null if there isn't one.
 *
 * If this view is part of the view hierarchy from a [android.app.Activity], [android.app.Dialog] or
 * [android.services.Dream], then the root of its hierarchy is a decor view and that
 * decor view holds the [android.view.Window] instance that will be returned.
 *
 * @throws IllegalStateException if not called from the main thread.
 */
val View.window: Window?
  get() {
    checkMainThread()
    return DecorViewSpy.pullDecorViewWindow(rootView)
  }

/**
 * The list of touch event interceptors, inserted in [Window.Callback.dispatchTouchEvent].
 *
 * If you only care about logging touch events without intercepting, you can implement the SAM
 * interface [TouchEventListener] which extends [TouchEventInterceptor].
 *
 * Calling this has a side effect of wrapping the window callback (on first call).
 *
 * @throws IllegalStateException if not called from the main thread.
 */
val Window.touchEventInterceptors: MutableList<TouchEventInterceptor>
  get() {
    checkMainThread()
    return listeners.touchEventInterceptors
  }

/**
 * The list of content changed listeners, inserted in [Window.Callback.onContentChanged].
 *
 * Calling this has a side effect of wrapping the window callback (on first call).
 *
 * @throws IllegalStateException if not called from the main thread.
 */
val Window.onContentChangedListeners: MutableList<OnContentChangedListener>
  get() {
    checkMainThread()
    return listeners.onContentChangedListeners
  }

/**
 * Calls [onDecorViewReady] with the decor view when the [android.view.Window] has a decor view
 * available.
 *
 * When an activity Window is created, it initially doesn't have a decor view set. Calling
 * [android.view.Window.getDecorView] has the side effect of creating that decor view if it hasn't
 * been created yet, which prevents further configuration that needs to happen before creation
 * of the decor view.
 *
 * This utility allows getting access to the decor view as soon as its ready (i.e. as soon as
 * [android.view.Window.setContentView] is called).
 *
 * Calling this has a side effect of wrapping the window callback (on first call), unless
 * the decor view was already set.
 *
 * @throws IllegalStateException if not called from the main thread.
 */
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

/**
 * Calls [onNextDraw] the next time this window is drawn, i.e. the next time
 * [android.view.ViewTreeObserver.OnDrawListener.onDraw] is called.
 *
 * The most common use for this is to measure the first draw time from
 * [android.app.Activity.onCreate] or
 * [android.app.Application.ActivityLifecycleCallbacks.onActivityCreated].
 *
 * This utility exists to work around [android.view.ViewTreeObserver] bugs: if the window
 * isn't attached then prior to Android API 26 all [android.view.ViewTreeObserver.OnDrawListener]
 * listeners are lost when the window gets attached:
 * https://android.googlesource.com/platform/frameworks/base/+/9f8ec54244a5e0343b9748db3329733f259604f3
 *
 * Also, [android.view.ViewTreeObserver.removeOnDrawListener] cannot be called from within the
 * [android.view.ViewTreeObserver.OnDrawListener.onDraw] callback, so this works around that
 * by posting the removal.
 *
 * Calling this has a side effect of wrapping the window callback (on first call), unless
 * the decor view was already set.
 *
 * @throws IllegalStateException if not called from the main thread.
 */
fun Window.onNextDraw(onNextDraw: () -> Unit) {
  onDecorViewReady { decorView ->
    decorView.onNextDraw(onNextDraw)
  }
}

/**
 * Returns [View.getWindowAttachCount] which has protected visibility and is normally only
 * accessible from within view subclasses.
 *
 * @throws IllegalStateException if not called from the main thread.
 */
val View.windowAttachCount: Int
  get() {
    checkMainThread()
    return windowAttachCount(this)
  }

/**
 * Returns the original window callback.
 *
 * The helper functions provided in this file replace the original window callback and delegate to
 * it. [wrappedCallback] returns the callback that was replaced. This is useful to check its
 * type.
 *
 * Note that this may be null if the Window didn't have a callback set, which normally doesn't
 * happen.
 */
val Window.wrappedCallback: Window.Callback?
  get() {
    checkMainThread()
    return wrappedCallbackOrNull
  }