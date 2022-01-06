package curtains.internal

import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import curtains.DispatchState
import curtains.DispatchState.Consumed
import java.lang.ref.WeakReference
import java.util.WeakHashMap
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Replaces the default Window callback to allows adding listeners / interceptors
 * for interesting events.
 */
internal class WindowCallbackWrapper constructor(
  private val delegate: Window.Callback
) : FixedWindowCallback(delegate) {

  private val listeners = WindowListeners()

  override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
    return if (event != null) {
      val iterator = listeners.keyEventInterceptors.iterator()

      val dispatch: (KeyEvent) -> DispatchState = object : (KeyEvent) -> DispatchState {
        override fun invoke(interceptedEvent: KeyEvent): DispatchState {
          return if (iterator.hasNext()) {
            val nextInterceptor = iterator.next()
            nextInterceptor.intercept(interceptedEvent, this)
          } else {
            DispatchState.from(delegate.dispatchKeyEvent(interceptedEvent))
          }
        }
      }

      if (iterator.hasNext()) {
        val firstInterceptor = iterator.next()
        firstInterceptor.intercept(event, dispatch)
      } else {
        DispatchState.from(delegate.dispatchKeyEvent(event))
      } is Consumed
    } else {
      delegate.dispatchKeyEvent(event)
    }
  }

  override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
    return if (event != null) {
      val iterator = listeners.touchEventInterceptors.iterator()

      val dispatch: (MotionEvent) -> DispatchState = object : (MotionEvent) -> DispatchState {
        override fun invoke(interceptedEvent: MotionEvent): DispatchState {
          return if (iterator.hasNext()) {
            val nextInterceptor = iterator.next()
            nextInterceptor.intercept(interceptedEvent, this)
          } else {
            DispatchState.from(delegate.dispatchTouchEvent(interceptedEvent))
          }
        }
      }

      if (iterator.hasNext()) {
        val firstInterceptor = iterator.next()
        firstInterceptor.intercept(event, dispatch)
      } else {
        DispatchState.from(delegate.dispatchTouchEvent(event))
      } is Consumed
    } else {
      delegate.dispatchTouchEvent(event)
    }
  }

  override fun onContentChanged() {
    listeners.onContentChangedListeners.forEach { it.onContentChanged() }
    delegate.onContentChanged()
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    listeners.onWindowFocusChangedListeners.forEach { it.onWindowFocusChanged(hasFocus) }
    delegate.onWindowFocusChanged(hasFocus)
  }

  companion object {

    private val jetpackWrapperClass by lazy(NONE) {
      try {
        Class.forName("androidx.appcompat.view.WindowCallbackWrapper")
      } catch (ignored: Throwable) {
        try {
          Class.forName("android.support.v7.view.WindowCallbackWrapper")
        } catch (ignored: Throwable) {
          null
        }
      }
    }

    private val jetpackWrappedField by lazy(NONE) {
      jetpackWrapperClass?.let { jetpackWrapperClass ->
        try {
          jetpackWrapperClass.getDeclaredField("mWrapped").apply { isAccessible = true }
        } catch (ignored: Throwable) {
          null
        }
      }
    }

    private val (Window.Callback?).isJetpackWrapper: Boolean
      get() = jetpackWrapperClass?.isInstance(this) ?: false

    private val (Window.Callback?).jetpackWrapped: Window.Callback?
      get() = jetpackWrappedField!![this] as Window.Callback?

    /**
     * Note: Ideally this would be a map of Window to WindowCallbackWrapper, however
     * the values of a WeakHashMap are strongly held and the callback chain typically holds a
     * strong ref back to the window (e.g. Activity is a Callback). To prevent leaks, we keep
     * a weak ref to the callback. The callback weak ref won't be cleared too early as the callback
     * is also held as part of the window callback chain.
     */
    private val callbackCache = WeakHashMap<Window, WeakReference<WindowCallbackWrapper>>()

    // window callback wrapper has a weak ref to window. keys have a weak ref to window. window
    // has a strong ref to callbacks.

    private val listenersLock = Any()

    val Window.listeners: WindowListeners
      get() {
        synchronized(listenersLock) {
          val existingWrapper = callbackCache[this]?.get()
          if (existingWrapper != null) {
            return existingWrapper.listeners
          }

          val currentCallback = callback
          return if (currentCallback == null) {
            // We expect a window to always have a default callback
            // that we can delegate to, but who knows what apps can be up to.
            WindowListeners()
          } else {
            val windowCallbackWrapper = WindowCallbackWrapper(currentCallback)
            callback = windowCallbackWrapper
            callbackCache[this] = WeakReference(windowCallbackWrapper)
            windowCallbackWrapper.listeners
          }
        }
      }

    tailrec fun Window.Callback?.unwrap(): Window.Callback? {
      return when {
        this == null -> null
        this is WindowCallbackWrapper -> delegate.unwrap()
        isJetpackWrapper -> jetpackWrapped.unwrap()
        else -> this
      }
    }
  }
}