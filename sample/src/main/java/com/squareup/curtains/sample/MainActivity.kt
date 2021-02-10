package com.squareup.curtains.sample

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.main)

    findViewById<View>(R.id.show_dialog).setOnClickListener {
      AlertDialog.Builder(this)
        .setTitle("Hi!")
        .show()
    }
    findViewById<View>(R.id.show_toast).setOnClickListener {
      Toast.makeText(this, "Hi!", Toast.LENGTH_SHORT).show()
    }
    findViewById<Spinner>(R.id.spinner).adapter =
      ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayOf("Item 1", "Item 2"))
  }
}