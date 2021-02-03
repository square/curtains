package android.view;

import android.content.Context;

public class JavaViewSpy extends View{

  public JavaViewSpy(Context context) {
    super(context);
  }

  public static int windowAttachCount(View view) {
    return view.getWindowAttachCount();
  }
}
