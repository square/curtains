
package curtains.test.utilities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.curtains.test.R

class TestCompatActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.test)
  }
}
