package curtains.internal

import android.os.Handler
import android.os.Looper
import android.view.FrameMetrics
import android.view.FrameMetrics.VSYNC_TIMESTAMP
import android.view.Window
import android.view.Window.OnFrameMetricsAvailableListener
import androidx.annotation.RequiresApi
import kotlin.LazyThreadSafetyMode.NONE

@RequiresApi(26)
internal class CurrentFrameMetricsListener(
  private val frameTimeNanos: Long,
  private val callback: (FrameMetrics) -> Unit
) : OnFrameMetricsAvailableListener {

  private var removed = false

  override fun onFrameMetricsAvailable(
    window: Window,
    frameMetrics: FrameMetrics,
    dropCountSinceLastInvocation: Int
  ) {
    if (!removed) {
      removed = true
      // We're on the frame metrics threads, the listener is stored in a non thread
      // safe list so we need to jump back to the main thread to remove.
      mainThreadHandler.post {
        window.removeOnFrameMetricsAvailableListener(this)
      }
    }

    val vsyncTimestamp = frameMetrics.getMetric(VSYNC_TIMESTAMP)
    if (vsyncTimestamp == frameTimeNanos) {
      callback(frameMetrics)
    }
  }

  companion object {
    private val mainThreadHandler by lazy(NONE) {
      Handler(Looper.getMainLooper())
    }
  }
}