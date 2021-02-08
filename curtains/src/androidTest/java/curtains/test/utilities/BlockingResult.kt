package curtains.test.utilities

import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class BlockingResult<T> {
  private val latch = CountDownLatch(1)

  private val result = AtomicReference<T>()

  fun release(resultValue: T) {
    val previousValue = result.getAndSet(resultValue)
    check(previousValue == null) {
      "Expected release() to only be called once, result already set: $previousValue"
    }
    latch.countDown()
  }

  fun await(): T {
    latch.checkAwait()
    return result.get()!!
  }
}