package vasistas.internal

import android.os.Looper

internal fun checkMainThread() {
  check(Looper.getMainLooper().thread === Thread.currentThread()) {
    "Should be called from the main thread, not ${Thread.currentThread()}"
  }
}