## ⚠️ ATTENTION: this library is in early development, there are no builds available yet. ⚠️

# Curtains

_Lift the curtain on Android Windows!_

The Android Framework is missing critical Window related APIs. This library fills the gaps.

Developers are not in control of the entirety of their code and therefore need APIs for
managing components in a centralized way. App developers constantly leverage 3rd party libraries,
work in codebases which high complexity and a large number of collaborators. Library developers
write code that gets integrated within code they do not control.

In practice, this manifests as a lack of central API for tracking the lifecycle of Android windows
(e.g. you can't know if a library shows a dialog), Android manifest components (services, providers,
 broadcast receiver) or accessing view state without subclassing.

The *Curtains* library provides centralized APIs for managing Window related concerns.
Its internals are a happy mix of hacks, workarounds for known Android bugs and glue code for
simplifying APIs.

Here are a few use cases that _Curtains_ enables:

* TODO Add link to touch event bug
* TODO frozen touch
* TODO Detecting first draw
* TODO intercept / cancel touch events / layers
* [LeakCanary](https://github.com/square/leakcanary) needs to know when root views are detached to
detect if they might be leaking.
* The [Espresso](https://developer.android.com/training/testing/espresso) UI test library needs
access to the list of attached root views to state view expectations, interactions, and assertions.\
Espresso does not use _Curtains_ but internally relies on the exact same hacks (see [RootsOracle](https://github.com/android/android-test/blob/master/espresso/core/java/androidx/test/espresso/base/RootsOracle.java)).



```kotlin
class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Curtains.windowAttachStateListeners += WindowAttachedListener { window ->
      if (window.decorView.windowAttachCount == 0) {
        window.touchEventInterceptors += TouchEventListener { motionEvent ->
          Log.d("ExampleApplication", "$window received $motionEvent")
        }
      }
    }
  }
}
```

```kotlin
class MainActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    window.onContentChangedListeners += OnContentChangedListener {
      val newContentView = findViewById<View>(android.R.id.content)
      Log.d("MainActivity", "Content changed to $newContentView")
    }

    setContentView(R.layout.main)
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

    val handler = Handler(Looper.getMainLooper())

    Curtains.windowAttachStateListeners += WindowAttachedListener { window ->
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
