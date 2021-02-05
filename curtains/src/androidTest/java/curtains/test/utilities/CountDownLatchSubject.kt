package curtains.test.utilities

import com.google.common.truth.Fact.simpleFact
import com.google.common.truth.FailureMetadata
import com.google.common.truth.Subject
import com.google.common.truth.Subject.Factory
import com.google.common.truth.Truth.assertAbout
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CountDownLatchSubject private constructor(
  metadata: FailureMetadata,
  private val actual: CountDownLatch
) : Subject(metadata, actual) {

  fun countsToZero() {
    if (!actual.await(30, TimeUnit.SECONDS)) {
      failWithActual(
        simpleFact(
          "Expected latch to count to zero within 30 seconds, count is still at ${actual.count}"
        )
      );
    }
  }

  companion object {
    fun latches(): Factory<CountDownLatchSubject, CountDownLatch> {
      return Factory<CountDownLatchSubject, CountDownLatch> { metadata, actual ->
        CountDownLatchSubject(
          metadata, actual
        )
      }
    }

    fun assertThat(actual: CountDownLatch): CountDownLatchSubject {
      return assertAbout(latches()).that(actual)
    }
  }
}