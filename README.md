<h2><span style="font-weight: bold;"><span style="color:#fe000c;">A</span><span style="color:#fc000c;">T</span><span style="color:#fa000c;">T</span><span style="color:#f8000c;">E</span><span style="color:#f6000c;">N</span><span style="color:#f4000c;">T</span><span style="color:#f2000c;">I</span><span style="color:#f0000c;">O</span><span style="color:#ee000c;">N</span><span style="color:#ec000c;">:</span><span style="color:#ea000c;"> </span><span style="color:#e9010b;">t</span><span style="color:#e7010b;">h</span><span style="color:#e5010b;">i</span><span style="color:#e3010b;">s</span><span style="color:#e1010b;"> </span><span style="color:#df010b;">l</span><span style="color:#dd010b;">i</span><span style="color:#db010b;">b</span><span style="color:#d9010b;">r</span><span style="color:#d7010b;">a</span><span style="color:#d5010b;">r</span><span style="color:#d3010b;">y</span><span style="color:#d1010b;"> </span><span style="color:#cf010b;">i</span><span style="color:#cd010b;">s</span><span style="color:#cb010b;"> </span><span style="color:#c9010b;">i</span><span style="color:#c7010b;">n</span><span style="color:#c5010b;"> </span><span style="color:#c3010b;">e</span><span style="color:#c2020a;">a</span><span style="color:#c0020a;">r</span><span style="color:#be020a;">l</span><span style="color:#bc020a;">y</span><span style="color:#ba020a;"> </span><span style="color:#b8020a;">d</span><span style="color:#b6020a;">e</span><span style="color:#b4020a;">v</span><span style="color:#b2020a;">e</span><span style="color:#b0020a;">l</span><span style="color:#ae020a;">o</span><span style="color:#ad020a;">p</span><span style="color:#ac020a;">m</span><span style="color:#ab020a;">e</span><span style="color:#aa020a;">n</span><span style="color:#a9030a;">t</span><span style="color:#a7030a;">,</span><span style="color:#a6030b;"> </span><span style="color:#a5030b;">t</span><span style="color:#a4030b;">h</span><span style="color:#a3030b;">e</span><span style="color:#a2030b;">r</span><span style="color:#a1030b;">e</span><span style="color:#a0040b;"> </span><span style="color:#9f040b;">a</span><span style="color:#9e040b;">r</span><span style="color:#9c040b;">e</span><span style="color:#9b040b;"> </span><span style="color:#9a040b;">n</span><span style="color:#99040b;">o</span><span style="color:#98040b;"> </span><span style="color:#97050c;">b</span><span style="color:#96050c;">u</span><span style="color:#95050c;">i</span><span style="color:#94050c;">l</span><span style="color:#93050c;">d</span><span style="color:#91050c;">s</span><span style="color:#90050c;"> </span><span style="color:#8f050c;">a</span><span style="color:#8e060c;">v</span><span style="color:#8d060c;">a</span><span style="color:#8c060c;">i</span><span style="color:#8b060c;">l</span><span style="color:#8a060c;">a</span><span style="color:#89060c;">b</span><span style="color:#88060d;">l</span><span style="color:#86060d;">e</span><span style="color:#85070d;"> </span><span style="color:#84070d;">y</span><span style="color:#83070d;">e</span><span style="color:#82070d;">t</span><span style="color:#81070d;">.</span></span></h2>

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
        Log.d("ExampleApplication", "$window received $motionEvent")
        false
      }
    })
  }
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
