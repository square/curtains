## ⚠️ ATTENTION: this library is in early development, there are no builds available yet. ⚠️

# Vasistas

[![Maven Central](https://img.shields.io/maven-central/v/com.squareup.vasistas/vasistas.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.squareup.vasistas%22)
[![GitHub license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
![Android CI](https://github.com/square/vasistas/workflows/Android%20CI/badge.svg)

Spy on your Android windows.

```kotlin
class ExampleApplication : Application() {
  override fun onCreate() {
    super.onCreate()

    Vasistas.addWindowListener(onWindowAddedListener { window ->
      window.addTouchEventListener { motionEvent ->
        // Use this to log or intercept all touch events in the app.
        Log.d("ExampleApplication", "$window received $motionEvent")
        false // consumed
      }
    })
  }
}
```

```
view.window?.let { window ->
  window.addWindowFocusListener { hasFocus ->
    // handle window focus changes without
    // having to subclass view or activity
  }
}
```

```
activity.window.onDecorViewReady { decorView ->
  // Avoid the side effects of calling Window.getDecorView() too early
}
```


* [Usage](#usage)
* [FAQ](#faq)
* [License](#license)

## Usage

Add the `vasistas` dependency to your app's `build.gradle` file:

```gradle
dependencies {
  implementation 'com.squareup.vasistas:vasistas:1.0'
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
