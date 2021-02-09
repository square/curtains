package curtains.test

import androidx.test.core.app.ActivityScenario
import com.google.common.truth.Truth.assertThat
import curtains.Curtains
import curtains.test.utilities.TestActivity
import curtains.test.utilities.getOnMain
import org.junit.Test

class CurtainsAttachedListTest {
  @Test fun no_root_views_when_no_activity() {
    assertThat(getOnMain { Curtains.attachedRootViews }).isEmpty()
    assertThat(getOnMain { Curtains.attachedWindows }).isEmpty()
  }

  @Test fun one_window_when_activity_launched() {
    ActivityScenario.launch(TestActivity::class.java).use {
      assertThat(getOnMain { Curtains.attachedRootViews }).hasSize(1)
      assertThat(getOnMain { Curtains.attachedWindows }).hasSize(1)
    }
  }
}
