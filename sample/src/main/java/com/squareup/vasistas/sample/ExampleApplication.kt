package com.squareup.vasistas.sample

import android.app.Application
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import vasistas.AttachState.ATTACHED
import vasistas.AttachState.DETACHED
import vasistas.DispatchState
import vasistas.DispatchState.NOT_CONSUMED
import vasistas.Vasistas
import vasistas.beforeDispatchTouchEventListeners
import vasistas.onNextDraw
import vasistas.onWindowFocusChangedListeners

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()


    class LoggingListener(val window: Window) : (MotionEvent) -> DispatchState {
      override fun invoke(motionEvent: MotionEvent): DispatchState {
        Log.d("ExampleApplication", "$window received $motionEvent")
        return NOT_CONSUMED
      }
    }

    Vasistas.windowAttachListeners += { window, attachState ->
      if (attachState.attached) {
        val listeners = window.beforeDispatchTouchEventListeners
        if (listeners.none { it is LoggingListener }) {
          window.beforeDispatchTouchEventListeners += LoggingListener(window)
        }
      }
    }

    val handler = Handler()

    Vasistas.windowAttachListeners += { window, attachState ->
      if (attachState.attached) {
        val windowAddedAt = SystemClock.uptimeMillis()
        window.onNextDraw {
          // Post at front to fully account for drawing time.
          handler.postAtFrontOfQueue {
            val duration = SystemClock.uptimeMillis() - windowAddedAt
            Log.d("ExampleApplication", "$window fully drawn in $duration ms")
          }
        }
      }
    }
  }
}