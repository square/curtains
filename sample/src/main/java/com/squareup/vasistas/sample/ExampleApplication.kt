package com.squareup.vasistas.sample

import android.app.Application
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import vasistas.Vasistas
import vasistas.addTouchEventListener
import vasistas.onNextDraw
import vasistas.onWindowAddedListener

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Vasistas.addWindowListener(onWindowAddedListener { window ->
      window.addTouchEventListener { motionEvent ->
        Log.d("ExampleApplication", "$window received $motionEvent")
        false
      }
    })


    val handler = Handler()

    Vasistas.addWindowListener(onWindowAddedListener { window ->
      val windowAddedAt = SystemClock.uptimeMillis()
      window.onNextDraw {
        // Post at front to fully account for drawing time.
        handler.postAtFrontOfQueue {
          val duration = SystemClock.uptimeMillis() - windowAddedAt
          Log.d("ExampleApplication", "$window fully drawn in $duration ms")
        }
      }
    })
  }
}