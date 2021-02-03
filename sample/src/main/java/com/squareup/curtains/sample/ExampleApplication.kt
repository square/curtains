package com.squareup.curtains.sample

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import curtains.Curtains
import curtains.TouchEventListener
import curtains.WindowAttachedListener
import curtains.onNextDraw
import curtains.touchEventInterceptors
import curtains.windowAttachCount

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Curtains.windowAttachStateListeners += WindowAttachedListener { window ->
      if (window.decorView.windowAttachCount == 0) {
        window.touchEventInterceptors += TouchEventListener { motionEvent ->
          Log.d("ExampleApplication", "$window received $motionEvent")
        }
      }
    }

    val handler = Handler(Looper.getMainLooper())

    Curtains.windowAttachStateListeners += WindowAttachedListener { window ->
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