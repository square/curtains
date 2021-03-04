# Curtains

_Lift the curtain on Android Windows!_

Curtains provides centralized APIs for dealing with Android windows.

Here are a few use cases that Curtains enables:

* Intercepting touch events on all activities and dialogs: for logging, detecting frozen frames on touch,
fixing known [bugs](https://issuetracker.google.com/issues/156666934) or ignoring touch events
during transitions.
* Knowing when root views are detached, e.g. to detect if they might be leaking ([LeakCanary](https://github.com/square/leakcanary)).
* Listing all attached root views for debugging ([Radiography](https://github.com/square/radiography)) or test purposes ([Espresso](https://github.com/android/android-test/blob/master/espresso/core/java/androidx/test/espresso/base/RootsOracle.java)).

## Table of contents

* [Usage](#usage)
* [FAQ](#faq)
* [License](#license)

![logo_512.png](assets/logo_512.png)

## Usage

Add the `curtains` dependency to your library or app's `build.gradle` file:

```gradle
dependencies {
  implementation 'com.squareup.curtains:curtains:1.0.1'
}
```

The library has two main entry points, [Curtains.kt](https://github.com/square/curtains/blob/main/curtains/src/main/java/curtains/Curtains.kt) and [Windows.kt](https://github.com/square/curtains/blob/main/curtains/src/main/java/curtains/Windows.kt).

### Curtains.kt

[Curtains.kt](https://github.com/square/curtains/blob/main/curtains/src/main/java/curtains/Curtains.kt)
provides access to the current root views (`Curtains.rootViews`), as well as the ability to set
listeners to get notified of additions and removals:

```kotlin
Curtains.onRootViewsChangedListeners += OnRootViewsChangedListener { view, added ->
  println("root $view ${if (added) "added" else "removed"}")
}
```

### Windows.kt

[Windows.kt](https://github.com/square/curtains/blob/main/curtains/src/main/java/curtains/Windows.kt)
provides window related extension functions.

New Android windows are created by calling
`[WindowManager.addView()](https://developer.android.com/reference/android/view/WindowManager)`,
and the Android Framework calls `WindowManager.addView()` for you in many different places.
`View.windowType` helps figure out what widget added a root view:

```kotlin
when(view.windowType) {
  PHONE_WINDOW -> TODO("View attached to an Activity or Dialog")
  POPUP_WINDOW -> TODO("View attached to a PopupWindow")
  TOOLTIP -> TODO("View attached to a tooltip")
  TOAST -> TODO("View attached to a toast")
  UNKNOWN -> TODO("?!? is this view attached? Is this Android 42?")
}
```

If `View.windowType` returns `PHONE_WINDOW`, you can then retrieve the corresponding
`android.view.Window` instance:

[Windows.kt](https://github.com/square/curtains/blob/main/curtains/src/main/java/curtains/Windows.kt)
provides window related extension functions.

```kotlin
val window: Window? = view.phoneWindow
```

Once you have a `android.view.Window` instance, you can easily intercept touch events:

```kotlin
window.touchEventInterceptors += TouchEventInterceptor { event, dispatch ->
  dispatch(event)
}
```

Or set a callback to avoid the side effects of calling Window.getDecorView() too early:

```kotlin
window.onDecorViewReady { decorView ->
}
```

Or react when `setContentView()` is called:

```kotlin
window.onContentChangedListeners += OnContentChangedListener {
}
```

### All together

We can combine these APIs to log touch events for all `android.view.Window` instances:
```kotlin
class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Curtains.onRootViewsChangedListeners += OnRootViewAddedListener { view ->
      view.phoneWindow?.let { window ->
        if (view.windowAttachCount == 0) {
          window.touchEventInterceptors += OnTouchEventListener { motionEvent ->
            Log.d("ExampleApplication", "$window received $motionEvent")
          }
        }
      }
    }

  }
}
```

Or measure the elapsed time from when a window is added to when it is fully draw:

```kotlin
// Measure the time from when a window is added to when it is fully drawn.
class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    val handler = Handler(Looper.getMainLooper())

    Curtains.onRootViewsChangedListeners += OnRootViewAddedListener { view ->
      view.phoneWindow?.let { window ->
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

## FAQ

### What's an Android window anyway?

No one knows exactly. Here are some window facts:

* Every floating thing you see on your phone is managed by a distinct window. Every activity, every
dialog, every floating menu, every toast
([until Android Q](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/widget/Toast.java;l=108-114;drc=8fe35e5f2195e416f250ba5332bce676c362b210)),
the status bar, the notification bar, the keyboard, the text selection toolbar, etc.
* Every window is associated to a surface, in which a view hierarchy can draw.
* Every window is associated to an input event socket. As touch events come in, the window manager
service dispatches them to the right window and corresponding input event socket.
* Android apps don't have anything that represents the concept of a window within their
own process. That concept lives within the WindowManager service which sits in the `system_server` process.
* The Android Framework offers an API to create a new Window: [WindowManager.addView()](https://developer.android.com/reference/android/view/WindowManager).
Notice how the API to create a _window_ is named `addView()`. This means _please create a window and
let this view be the root of its view hierarchy_.
* All standard Android components (Activity, dialog, menus) take care of creating a window for you.
* [android.view.Window](https://developer.android.com/reference/android/view/Window) is not a window.
It provides shared helper code and public API surface for [Activity](https://developer.android.com/reference/android/app/Activity), [Dialog](https://developer.android.com/reference/android/app/Dialog) and [DreamService](https://developer.android.com/reference/android/service/dreams/DreamService) (lol).
**This is important**: some Android widgets create floating windows using a `Dialog` (which wraps a `android.view.Window`) while others use a [PopupWindow](https://developer.android.com/reference/android/widget/PopupWindow).
`android.widget.PopupWindow` is entirely separate from `android.view.Window`.
* Inside an Android app, the class that best represents a window is
[ViewRootImpl](https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/android/view/ViewRootImpl.java;l=194;drc=d31ee388115d17c2fd337f2806b37390c7d29834).
Every call to `WindowManager.addView()` triggers the creation of a new `ViewRootImpl` instance which
sits in between WindowManager and the view provided to `WindowManager.addView()`. This class is internal and
you will be yelled at if you mess with it.

### Will this library break my app?

First things first, see the [License](#license): unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

The hooks leveraged by this library are also used by [Espresso](https://github.com/android/android-test/blob/master/espresso/core/java/androidx/test/espresso/base/RootsOracle.java),
which makes it unlikely that they'll break in the future. On top of that, Curtains has
comprehensive UI test coverage across API levels 16 to 30.

### Does the Android Framework provide official APIs we can use instead of this?

Sadly, no.

Android developers are never in control of the entirety of their code:
* App developers constantly leverage 3rd party libraries and work in code bases which high
complexity and many collaborators.
* Library developers write code that gets integrated within app code they do not control.

Android developers need APIs to manage components in a centralized way, unfortunately, the Android
Framework lacks many such APIs: tracking the lifecycle of Android windows (e.g. you can't know if a
library shows a dialog), tracking the lifecycle of Android manifest components (services, providers,
broadcast receiver) or accessing view state without subclassing.

### Who named this library?

I ([@pyricau](http://github.com/pyricau)) initially named it
[vasistas](https://www.grammarphobia.com/blog/2013/11/vasistas.html) but that was too hard to
pronounce for English speakers. [Christina Lee](https://github.com/christinalee) suggested that
curtains are useful add-ons to windows in the real world and hence this library is now _Curtains_.

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
