package android.view;

import android.content.Context;

/**
 * Provides access to [View.getWindowAttachCount] which is protected.
 * In theory, protected implies that the method can be called from a subclass OR from a class in
 * the same package. However, Android is being annoying on API 29 and only allows access from
 * subclasses, so a simple static method in a class in the right package doesn't work.
 *
 * Weirdly enough, if the static method is defined in a class that extends View, then it works.
 * So that's what we do. If you work on AOSP and are reading this, please add a
 * getWindowAttachCount() public API to android.view.Window.
 */
class JavaViewSpy extends View {

  JavaViewSpy(Context context) {
    super(context);
    throw new UnsupportedOperationException("This class isn't meant to be instantiated");
  }

  static int windowAttachCount(View view) {
    return view.getWindowAttachCount();
  }
}
