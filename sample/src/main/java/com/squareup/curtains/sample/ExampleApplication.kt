package com.squareup.curtains.sample

import android.app.Application
import android.util.Log
import curtains.Curtains
import curtains.OnKeyEventListener
import curtains.OnRootViewsChangedListener
import curtains.OnTouchEventListener
import curtains.keyEventInterceptors
import curtains.phoneWindow
import curtains.touchEventInterceptors
import curtains.windowAttachCount
import curtains.windowType
import curtains.wrappedCallback

class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Curtains.onRootViewsChangedListeners += OnRootViewsChangedListener { view, added ->
      val verb = if (added) "added" else "removed"
      Log.d(
        "ExampleApplication",
        "Root view $verb ${view.windowType} ${view.phoneWindow} ${
          view.phoneWindow?.callback.wrappedCallback
        }  $view"
      )
      if (added) {
        view.phoneWindow?.let { window ->
          if (view.windowAttachCount == 0) {
            window.touchEventInterceptors += OnTouchEventListener { motionEvent ->
              Log.d("ExampleApplication", "$window received $motionEvent")
            }
            window.keyEventInterceptors += OnKeyEventListener { keyEvent ->
              Log.d("ExampleApplication", "$window received $keyEvent")
            }
          }
        }
      }
    }
  }
}