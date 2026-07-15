package com.squareup.curtains.sample

import android.os.Build
import android.util.Log
import android.view.View

/**
 * Repro helper: simulates a SECOND window-observing SDK (e.g. a relocated copy of Curtains vendored
 * by another analytics/replay SDK) that swaps WindowManagerGlobal.mViews with its OWN non-delegating
 * observing list — the same reflection technique Curtains itself uses.
 *
 * Installed BEFORE Curtains so we can observe whether Curtains preserves it:
 *  - Without the RootViewsSpy fix: Curtains discards this list on install -> RIVAL logs stop.
 *  - With the fix: Curtains forwards to this list -> RIVAL logs continue alongside CURTAINS.
 */
object RivalWindowObserver {
  private const val TAG = "RIVAL"

  @Suppress("PrivateApi", "DiscouragedPrivateApi", "UNCHECKED_CAST")
  fun install() {
    if (Build.VERSION.SDK_INT < 19) return
    try {
      val wmgClass = Class.forName("android.view.WindowManagerGlobal")
      val instance = wmgClass.getMethod("getInstance").invoke(null)
      val field = wmgClass.getDeclaredField("mViews").apply { isAccessible = true }
      val current = field.get(instance) as ArrayList<View>
      val observing = object : ArrayList<View>() {
        override fun add(element: View): Boolean {
          Log.d(TAG, "root view added: $element")
          return super.add(element)
        }

        override fun removeAt(index: Int): View {
          val removed = super.removeAt(index)
          Log.d(TAG, "root view removed: $removed")
          return removed
        }
      }.apply { addAll(current) }
      field.set(instance, observing)
      Log.d(TAG, "installed (mViews swapped, non-delegating)")
    } catch (t: Throwable) {
      Log.w(TAG, "install failed", t)
    }
  }
}
