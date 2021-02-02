package com.squareup.vasistas.sample

import android.app.Activity
import android.os.Bundle
import android.view.View
import vasistas.Vasistas
import vasistas.onDecorViewReady
import vasistas.onWindowFocusChangedListeners
import vasistas.window

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.main)
    val view = findViewById<View>(R.id.gone)
    view.window!!.onWindowFocusChangedListeners += { focusState ->
      if (focusState.focused) {

      }
    }
  }
}
