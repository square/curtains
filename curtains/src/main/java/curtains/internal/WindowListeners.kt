package curtains.internal

import android.view.MotionEvent
import curtains.DispatchState
import curtains.FocusState
import java.util.concurrent.CopyOnWriteArrayList

internal class WindowListeners {
  val beforeDispatchTouchEventListeners = CopyOnWriteArrayList<(MotionEvent) -> DispatchState>()

  val afterDispatchTouchEventListeners =
    CopyOnWriteArrayList<(MotionEvent, DispatchState) -> Unit>()

  val onContentChangedListeners = CopyOnWriteArrayList<() -> Unit>()

  val onWindowFocusChangedListeners = CopyOnWriteArrayList<(FocusState) -> Unit>()
}