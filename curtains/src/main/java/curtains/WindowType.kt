package curtains

enum class WindowType {
  /**
   * Window created by an [android.app.Activity], [android.app.Dialog] or
   * [android.service.dreams.DreamService]. The corresponding [android.view.Window]
   * instance can be retrieved via [phoneWindow].
   */
  PHONE_WINDOW,

  /**
   * Window created by a [android.widget.PopupWindow], used by most Android widgets
   * to display floating windows.
   */
  POPUP_WINDOW,

  /**
   * Window created for view tooltips (see [android.view.View.setTooltipText]).
   */
  TOOLTIP,

  /**
   * Window created when showing a toast. Note: starting with Q, text toasts are rendered by
   * SystemUI instead of in-app.
   */
  TOAST,

  UNKNOWN
}