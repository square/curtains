package curtains.test

import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import curtains.Curtains
import curtains.test.utilities.TestActivity
import curtains.test.utilities.getOnMain
import org.junit.Test

class CurtainsRootViewsTest {
  @Test fun no_root_views_when_no_activity() {
    val attachedRootViews = getOnMain { Curtains.rootViews }

    assertThat(attachedRootViews).isEmpty()
  }

  @Test fun one_root_view_when_activity_launched() {
    ActivityScenario.launch(TestActivity::class.java).use {
      val attachedRootViews = getOnMain { Curtains.rootViews }

      assertThat(attachedRootViews).hasSize(1)
    }
  }
}
