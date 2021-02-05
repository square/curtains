package curtains.test.utilities

import android.app.Activity
import androidx.test.ext.junit.rules.ActivityScenarioRule
import java.util.concurrent.atomic.AtomicReference

interface HasActivityScenarioRule<T : Activity> {
  val rule: ActivityScenarioRule<T>

  fun onActivity(onActivity: (T) -> Unit) {
    rule.scenario.onActivity(onActivity)
  }

  fun <R> getOnActivity(getOnActivity: (T) -> R) = rule.scenario.getOnActivity(getOnActivity)

}