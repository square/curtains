package vasistas

import android.view.View

interface RootViewListener {
  fun onRootViewAdded(view: View) {}
  fun onRootViewRemoved(view: View) {}
}