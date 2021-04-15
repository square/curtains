package curtains.internal

import curtains.KeyEventInterceptor
import curtains.OnContentChangedListener
import curtains.OnWindowFocusChangedListener
import curtains.TouchEventInterceptor
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Listeners held by [WindowCallbackWrapper].
 */
internal class WindowListeners {
  val touchEventInterceptors = CopyOnWriteArrayList<TouchEventInterceptor>()

  val keyEventInterceptors = CopyOnWriteArrayList<KeyEventInterceptor>()

  val onContentChangedListeners = CopyOnWriteArrayList<OnContentChangedListener>()

  val onWindowFocusChangedListeners = CopyOnWriteArrayList<OnWindowFocusChangedListener>()
}