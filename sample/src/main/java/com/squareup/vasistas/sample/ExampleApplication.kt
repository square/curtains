package com.squareup.vasistas.sample

import android.app.Application
import android.util.Log
import vasistas.Vasistas
import vasistas.addTouchEventListener
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
  }
}