package curtains.test

import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.BaseAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import curtains.test.utilities.checkAwait
import curtains.test.utilities.onAttachedToWindow
import curtains.window
import curtains.wrappedCallback
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class PullWindowFromViewTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun contentViewPulledWindow_Is_ActivityWindow() {
    onActivity { activity ->
      val contentView = activity.findViewById<View>(android.R.id.content)
      assertThat(contentView.window).isSameInstanceAs(activity.window)
    }
  }

  @Test fun dialogViewPulledWindow_Is_DialogWindow() {
    onActivity { activity ->
      val dialogView = TextView(activity).apply { text = "Dialog!" }
      val dialog = AlertDialog.Builder(activity)
        .setView(dialogView)
        .show()
      assertThat(dialogView.window).isSameInstanceAs(dialog.window)
      assertThat(dialogView.window!!.wrappedCallback).isInstanceOf(AlertDialog::class.java)
      dialog.dismiss()
    }
  }

  @Test fun viewPulledWindow_from_WindowManager_addView_Is_Null() {
    onActivity { activity ->
      val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val params = LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT,
        LayoutParams.TYPE_APPLICATION_PANEL,
        LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
      )
      val toastView = TextView(activity).apply { text = "Yo" }
      windowManager.addView(toastView, params)

      assertThat(toastView.window).isNull()
      windowManager.removeView(toastView)
    }
  }

  @Test fun viewPulledWindow_from_PopupWindow_Is_Null() {
    onActivity { activity ->
      val popupContentView = TextView(activity).apply { text = "Popup" }
      val popupWindow = PopupWindow(popupContentView)
      val activityContentView = activity.findViewById<View>(android.R.id.content)
      popupWindow.showAsDropDown(activityContentView)
      assertThat(popupContentView.window).isNull()
      popupWindow.dismiss()
    }
  }

  @Test fun spinnerViewPulledWindow_Is_DialogWindow() {
    val itemViewCreated = CountDownLatch(1)
    val itemViewRef = AtomicReference<View>()

    onActivity { activity ->
      val spinner = Spinner(activity, Spinner.MODE_DIALOG)
      spinner.adapter = object : BaseAdapter() {
        override fun getCount() = 1
        override fun getItem(position: Int) = Unit
        override fun getItemId(position: Int) = 1L

        override fun getView(
          position: Int,
          convertView: View?,
          parent: ViewGroup?
        ): View {
          val itemView = convertView ?: View(activity)
          // Spinner leverages the adapter to show the selected entry.
          if (parent !== spinner) {
            itemView.onAttachedToWindow {
              itemViewRef.set(itemView)
              itemViewCreated.countDown()
            }
          }
          return itemView
        }
      }
      activity.setContentView(spinner)

      check(spinner.performClick())
    }

    itemViewCreated.checkAwait()

    onActivity {
      val spinnerItemView = itemViewRef.get()!!
      assertThat(spinnerItemView.window!!.wrappedCallback).isInstanceOf(
        AlertDialog::class.java
      )
    }
  }
}