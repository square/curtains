package curtains

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle

/**
 * Helper interface for implementing [ActivityLifecycleCallbacks] without
 * having to implement all methods.
 *
 * If you care about one callback, see also the related functional interfaces
 * that extend this interface, e.g. [OnActivityCreated].
 */
interface ActivityLifecycleCallbacksAdapter : ActivityLifecycleCallbacks {
  override fun onActivityCreated(
    activity: Activity,
    savedInstanceState: Bundle?
  ) = Unit

  override fun onActivityStarted(activity: Activity) = Unit

  override fun onActivityResumed(activity: Activity) = Unit

  override fun onActivityPaused(activity: Activity) = Unit

  override fun onActivityStopped(activity: Activity) = Unit

  override fun onActivitySaveInstanceState(
    activity: Activity,
    outState: Bundle
  ) = Unit

  override fun onActivityDestroyed(activity: Activity) = Unit
}

fun interface OnActivityCreated : ActivityLifecycleCallbacksAdapter {
  override fun onActivityCreated(
    activity: Activity,
    savedInstanceState: Bundle?
  )
}

/**
 * Functional (SAM) interface to allow implementing only
 * [ActivityLifecycleCallbacksAdapter.onActivityStarted].
 */
fun interface OnActivityStarted : ActivityLifecycleCallbacksAdapter {
  override fun onActivityStarted(activity: Activity)
}

/**
 * Functional (SAM) interface to allow implementing only
 * [ActivityLifecycleCallbacksAdapter.onActivityResumed].
 */
fun interface OnActivityResumed : ActivityLifecycleCallbacksAdapter {
  override fun onActivityResumed(activity: Activity)
}

/**
 * Functional (SAM) interface to allow implementing only
 * [ActivityLifecycleCallbacksAdapter.onActivityPaused].
 */
fun interface OnActivityPaused : ActivityLifecycleCallbacksAdapter {
  override fun onActivityPaused(activity: Activity)
}

/**
 * Functional (SAM) interface to allow implementing only
 * [ActivityLifecycleCallbacksAdapter.onActivityStopped].
 */
fun interface OnActivityStopped : ActivityLifecycleCallbacksAdapter {
  override fun onActivityStopped(activity: Activity)
}

/**
 * Functional (SAM) interface to allow implementing only
 * [ActivityLifecycleCallbacksAdapter.onActivitySaveInstanceState].
 */
fun interface OnActivitySaveInstanceState : ActivityLifecycleCallbacksAdapter {
  override fun onActivitySaveInstanceState(
    activity: Activity,
    outState: Bundle
  )
}

/**
 * Functional (SAM) interface to allow implementing only
 * [ActivityLifecycleCallbacksAdapter.onActivityDestroyed].
 */
fun interface OnActivityDestroyed : ActivityLifecycleCallbacksAdapter {
  override fun onActivityDestroyed(activity: Activity)
}