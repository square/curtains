package curtains.test.utilities

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.squareup.curtains.test.R

class TestActivity : Activity() {

  var onKeyDown: ((Int, KeyEvent) -> Boolean) = { keyCode, event ->
    super.onKeyDown(keyCode, event)
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.test)
  }

  override fun onKeyDown(
    keyCode: Int,
    event: KeyEvent
  ): Boolean = onKeyDown.invoke(keyCode, event)
}
