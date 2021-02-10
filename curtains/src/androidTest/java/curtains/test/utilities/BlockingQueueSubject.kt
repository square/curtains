package curtains.test.utilities

import com.google.common.truth.Fact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.IterableSubject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit

class BlockingQueueSubject<E : Any> private constructor(
    metadata: FailureMetadata,
    private val actual: BlockingQueue<E>
) : IterableSubject(metadata, actual) {

  fun polls(expected: E) {
    val actual = actual.poll(30, TimeUnit.SECONDS)
        ?: return failWithoutActual(
            Fact.simpleFact(
                "Expected an element available within 30 seconds"
            )
        )
    assertThat(actual).isEqualTo(expected)
  }

  fun pollsNull() {
    val actual = actual.poll()
    assertThat(actual).isNull()
  }

  companion object {
    fun <E : Any> queues(): Factory<BlockingQueueSubject<E>, BlockingQueue<E>> {
      return Factory<BlockingQueueSubject<E>, BlockingQueue<E>> { metadata, actual ->
        BlockingQueueSubject(
            metadata, actual
        )
      }
    }

    fun <E : Any> assertThat(actual: BlockingQueue<E>): BlockingQueueSubject<E> {
      return Truth.assertAbout(queues<E>()).that(actual)
    }
  }
}