## ⚠️ ATTENTION: this library is in early development, there are no builds available yet. ⚠️

# Curtains

_Was ist das?_ The missing Android Window APIs!

```kotlin
class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    class LoggingListener(val window: Window) : (MotionEvent) -> DispatchState {
      override fun invoke(motionEvent: MotionEvent): DispatchState {
        Log.d("ExampleApplication", "$window received $motionEvent")
        return NOT_CONSUMED
      }
    }

    Curtains.windowAttachListeners += { window, attachState ->
      if (attachState.attached) {
        val listeners = window.beforeDispatchTouchEventListeners
        if (listeners.none { it is LoggingListener }) {
          window.beforeDispatchTouchEventListeners += LoggingListener(window)
        }
      }
    }
  }
}
```

```kotlin
view.window!!.onWindowFocusChangedListeners += { focusState ->
  if (focusState.focused) {
    // handle window focus changes without
    // having to subclass view or activity
  }
}
```

```kotlin
activity.window.onDecorViewReady { decorView ->
  // Avoid the side effects of calling Window.getDecorView() too early
}
```

```kotlin
class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    val handler = Handler()

    Curtains.windowAttachListeners += { window, attachState ->
      if (attachState.attached) {
        val windowAddedAt = SystemClock.uptimeMillis()
        window.onNextDraw {
          // Post at front to fully account for drawing time.
          handler.postAtFrontOfQueue {
            val duration = SystemClock.uptimeMillis() - windowAddedAt
            Log.d("ExampleApplication", "$window fully drawn in $duration ms")
          }
        }
      }
    }
  }
}
```


* [Usage](#usage)
* [FAQ](#faq)
* [License](#license)

## Usage

Add the `curtains` dependency to your app's `build.gradle` file:

```gradle
dependencies {
  implementation 'com.squareup.curtains:curtains:1.0'
}
```

TODO

## FAQ

TODO

## License

<pre>
Copyright 2021 Square Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
