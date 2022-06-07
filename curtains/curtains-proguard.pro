-keep class androidx.appcompat.view.WindowCallbackWrapper {
    android.view.Window$Callback mWrapped;
}

-keep class android.support.v7.view.WindowCallbackWrapper {
    android.view.Window$Callback mWrapped;
}

-keepclassmembers class android.view.JavaViewSpy {
    static int windowAttachCount(android.view.View);
}
