package curtains.internal

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

internal val mainHandler by lazy { Handler(Looper.getMainLooper()) }

internal val frameMetricsHandler by lazy {
  val thread = HandlerThread("frame_metrics")
  thread.start()
  Handler(thread.looper)
}

internal fun checkMainThread() {
  check(Looper.getMainLooper().thread === Thread.currentThread()) {
    "Should be called from the main thread, not ${Thread.currentThread()}"
  }
}