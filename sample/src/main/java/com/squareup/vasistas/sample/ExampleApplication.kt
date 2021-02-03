package com.squareup.curtains.sample

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import curtains.DispatchState
import curtains.DispatchState.NOT_CONSUMED
import curtains.Curtains
import curtains.beforeDispatchTouchEventListeners
import curtains.onNextDraw

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    class LoggingListener(val window: Window) : (MotionEvent) -> DispatchState {
      override fun invoke(motionEvent: MotionEvent): DispatchState {
        Log.d("ExampleApplication", "$window received $motionEvent")
        return NOT_CONSUMED
      }
    }

    Curtains.windowAttachListeners += { window, attachState ->
      if (attachState.attached) {
        val listeners = window.beforeDispatchTouchEventListeners
        if (listeners.none { it is LoggingListener }) {
          window.beforeDispatchTouchEventListeners += LoggingListener(window)
        }
      }
    }

    val handler = Handler(Looper.getMainLooper())

    Curtains.windowAttachListeners += { window, attachState ->
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