package curtains.test

import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.ArrayAdapter
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.TextView
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import curtains.Curtains
import curtains.ViewAttachedListener
import curtains.WindowAttachedListener
import curtains.onDecorViewReady
import curtains.test.utilities.BlockingResult
import curtains.test.utilities.HasActivityScenarioRule
import curtains.test.utilities.TestActivity
import curtains.test.utilities.addUntilClosed
import curtains.test.utilities.checkAwait
import curtains.test.utilities.getOnMain
import curtains.window
import curtains.wrappedCallback
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch

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
    val listeners = getOnMain { Curtains.rootViewAttachStateListeners }
    val viewAttachedLatch = CountDownLatch(1)
    listeners.addUntilClosed(ViewAttachedListener {
      viewAttachedLatch.countDown()
    }).use {
      val (dialogContentView, popupWindow) = getOnActivity { activity ->
        val activityContentView = activity.findViewById<View>(android.R.id.content)
        val dialogContentView = TextView(activity).apply { text = "Yo" }
        val popupWindow = PopupWindow(dialogContentView)
        popupWindow.showAsDropDown(activityContentView)
        dialogContentView to popupWindow
      }

      viewAttachedLatch.checkAwait()

      onActivity {
        assertThat(dialogContentView.window).isNull()
        popupWindow.dismiss()
      }
    }
  }

  @Test fun spinnerViewPulledWindow_Is_DialogWindow() {
    val listeners = getOnMain { Curtains.windowAttachStateListeners }

    val blockingResult = BlockingResult<View>()

    listeners.addUntilClosed(WindowAttachedListener { window ->
      window.onDecorViewReady { decorView ->
        decorView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
          override fun onGlobalLayout() {
            val spinnerItemView = checkNotNull(decorView.findViewById(android.R.id.text1))
            decorView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            blockingResult.release(spinnerItemView)
          }
        })
      }
    }).use {
      onActivity { activity ->
        val spinner = Spinner(activity, Spinner.MODE_DIALOG)
        spinner.adapter = ArrayAdapter(
          activity, android.R.layout.simple_list_item_1, arrayOf("Spinner item")
        )
        activity.setContentView(spinner)

        check(spinner.performClick())
      }
    }

    val spinnerItemView = blockingResult.await()

    onActivity {
      assertThat(spinnerItemView.window!!.wrappedCallback).isInstanceOf(
        AlertDialog::class.java
      )
    }
  }
}