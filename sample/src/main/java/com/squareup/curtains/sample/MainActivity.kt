package com.squareup.curtains.sample

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import curtains.OnContentChangedListener
import curtains.onContentChangedListeners

class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window.onContentChangedListeners += OnContentChangedListener {
      val newContentView = findViewById<View>(android.R.id.content)
      Log.d("MainActivity", "Content changed to $newContentView")
    }

    setContentView(R.layout.main)
  }
}