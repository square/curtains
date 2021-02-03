package com.squareup.curtains.sample

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.squareup.curtains.sample.R.id
import com.squareup.curtains.sample.R.layout
import curtains.onWindowFocusChangedListeners
import curtains.window

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(layout.main)
    val view = findViewById<View>(id.gone)
    view.window!!.onWindowFocusChangedListeners += { focusState ->
      if (focusState.focused) {
      }
    }
  }
}