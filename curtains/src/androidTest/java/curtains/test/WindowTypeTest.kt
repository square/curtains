package curtains.test

import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.Curtains
import curtains.OnRootViewAddedListener
import curtains.WindowType.PHONE_WINDOW
import curtains.WindowType.POPUP_WINDOW
import curtains.WindowType.TOOLTIP
import curtains.WindowType.UNKNOWN
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import curtains.test.utilities.addUntilClosed
import curtains.test.utilities.assumeSdkAtLeast
import curtains.windowType
import org.junit.Rule
import org.junit.Test

class WindowTypeTest : HasActivityScenarioRule<TestActivity> {

  @get:Rule
  override val rule = ActivityScenarioRule(TestActivity::class.java)

  @Test fun activity_view_has_PHONE_WINDOW_type() {
    onActivity { activity ->
      val contentView = activity.findViewById<View>(android.R.id.content)
      assertThat(contentView.windowType).isEqualTo(PHONE_WINDOW)
    }
  }

  @Test fun dialog_view_has_PHONE_WINDOW_type() {
    onActivity { activity ->
      val dialogView = TextView(activity).apply { text = "Dialog!" }
      val dialog = AlertDialog.Builder(activity)
        .setView(dialogView)
        .show()
      assertThat(dialogView.windowType).isEqualTo(PHONE_WINDOW)
      dialog.dismiss()
    }
  }

  @Test fun view_from_WindowManager_addView_has_UNKNOWN_type() {
    onActivity { activity ->
      val windowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val params = LayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT,
        LayoutParams.TYPE_APPLICATION_PANEL,
        LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
      )
      val rootView = TextView(activity).apply { text = "Yo" }
      windowManager.addView(rootView, params)

      assertThat(rootView.windowType).isEqualTo(UNKNOWN)
      windowManager.removeView(rootView)
    }
  }

  @Test fun popup_view_has_POPUP_WINDOW_type() {
    onActivity { activity ->
      val popupContentView = TextView(activity).apply { text = "Popup" }
      val popupWindow = PopupWindow(popupContentView)
      val activityContentView = activity.findViewById<View>(android.R.id.content)
      popupWindow.showAsDropDown(activityContentView)
      assertThat(popupContentView.windowType).isEqualTo(POPUP_WINDOW)
      popupWindow.dismiss()
    }
  }

  @Test fun tooltip_view_has_TOOLTIP_type() {
    assumeSdkAtLeast(26, "View.setTooltipText() was added in API 26")
    onActivity { activity ->
      val contentView = TextView(activity).apply { text = "Yo" }
      contentView.tooltipText = "Tooltip"
      activity.setContentView(contentView)

      Curtains.onRootViewsChangedListeners.addUntilClosed(OnRootViewAddedListener { rootView ->
        assertThat(rootView.windowType).isEqualTo(TOOLTIP)
      }).use {
        contentView.performLongClick()
      }
    }
  }
}