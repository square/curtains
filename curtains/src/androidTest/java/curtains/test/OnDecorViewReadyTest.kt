package curtains.test

import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import curtains.OnActivityCreated
import curtains.onDecorViewReady
import curtains.test.utilities.CountDownLatchSubject.Companion.assertThat
import curtains.test.utilities.TestActivity
import curtains.test.utilities.application
import curtains.test.utilities.registerUntilClosed
import org.junit.Test
import java.util.concurrent.CountDownLatch

class OnDecorViewReadyTest {

  @Test fun onDecorViewReady_triggers_when_contentView_set() {
    val decorViewReady = CountDownLatch(1)
    application.registerUntilClosed(OnActivityCreated { activity, _ ->
      if (activity !is TestActivity) {
        return@OnActivityCreated
      }
      assertThat(activity.window.peekDecorView()).isNull()
      activity.window.onDecorViewReady {
        decorViewReady.countDown()
      }
      assertThat(decorViewReady.count).isEqualTo(1)
    }).use {
      ActivityScenario.launch(TestActivity::class.java).use {
        assertThat(decorViewReady).countsToZero()
      }
    }
  }

  @Test fun onDecorViewReady_triggers_immediately_if_contentView_set() {
    ActivityScenario.launch(TestActivity::class.java).use { scenario ->
      scenario.onActivity { activity ->
        val decorViewReady = CountDownLatch(1)
        activity.window.onDecorViewReady {
          decorViewReady.countDown()
        }
        assertThat(decorViewReady).countsToZero()
      }
    }
  }
}