package curtains.internal

import curtains.OnContentChangedListener
import curtains.OnWindowFocusChangedListener
import curtains.TouchEventInterceptor
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Listeners held by [WindowCallbackWrapper].
 */
internal class WindowListeners {
  val touchEventInterceptors = CopyOnWriteArrayList<TouchEventInterceptor>()

  val onContentChangedListeners = CopyOnWriteArrayList<OnContentChangedListener>()

  val onWindowFocusChangedListeners = CopyOnWriteArrayList<OnWindowFocusChangedListener>()
}