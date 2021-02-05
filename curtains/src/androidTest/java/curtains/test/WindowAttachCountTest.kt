package curtains.test

import android.app.AlertDialog
import android.app.Dialog
import android.view.View
import android.view.View.OnAttachStateChangeListener
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import curtains.test.utilities.checkAwait
import curtains.windowAttachCount
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

class WindowAttachCountTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun activityContentView_has_attach_count_1() {
    val windowAttachCount = getOnActivity { activity ->
      val contentView = activity.findViewById<View>(android.R.id.content)
      contentView.windowAttachCount
    }

    assertThat(windowAttachCount).isEqualTo(1)
  }

  @Test fun never_attached_view_has_attach_count_0() {
    val windowAttachCount = getOnActivity { activity ->
      View(activity).windowAttachCount
    }

    assertThat(windowAttachCount).isEqualTo(0)
  }

  @Test fun dialog_shown_twice_view_has_attach_count_2() {
    val dialogAttached = CountDownLatch(2)
    val dialogView = getOnActivity { activity ->
      View(activity).apply {
        val dialog = AlertDialog.Builder(activity).setView(this).create()
        onAttachedToWindow { dialogAttached.countDown() }
        dialog.showTwice()
      }
    }

    dialogAttached.checkAwait()

    val windowAttachCount = getOnActivity {
      dialogView.windowAttachCount
    }

    assertThat(windowAttachCount).isEqualTo(2)
  }

  private fun Dialog.showTwice() {
    show()
    var toggleDialog = true
    window!!.decorView.addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
      override fun onViewAttachedToWindow(v: View) {
        if (toggleDialog) {
          dismiss()
        }
      }

      override fun onViewDetachedFromWindow(v: View) {
        if (toggleDialog) {
          toggleDialog = false
          show()
        }
      }
    })
  }
}

private fun View.onAttachedToWindow(onAttachedToWindow: () -> Unit) {
  addOnAttachStateChangeListener(object : OnAttachStateChangeListener {
    override fun onViewAttachedToWindow(v: View) = onAttachedToWindow()

    override fun onViewDetachedFromWindow(v: View) = Unit
  })
}