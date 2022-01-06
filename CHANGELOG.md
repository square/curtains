Change Log
==========

Version 1.2.3
-------------

_2022-01-06_

* [#30](https://github.com/square/curtains/pull/30) Detecting window tooltip type when language isn't English.

Version 1.2.2
-------------

_2021-9-15_

* Stop throwing when not on main thread. While Curtains APIs should be used from the main thread, there are a number edge cases as well as bugs in consuming SDKs / apps and the Android Framework SDKs that can trigger calls from the wrong thread, and in many cases there isn't much developers can do, so Curtains is relaxing the constraint and doing a best effort approach.

Version 1.2.1
-------------

_2021-5-20_

* Fixed crash when showing a non support overflow menu. This crash is caused by an incorrect `@NonNull` annotation in AOSP ([issue]( https://issuetracker.google.com/issues/188568911), [PR](https://github.com/square/curtains/pull/22)).

Version 1.2
-------------

_2021-5-11_

* New window extension function: `window.onNextFrameMetrics(frameTimeNanos){}` (based on [this blog](https://dev.to/pyricau/tap-response-time-jetpack-navigation-4738)).
* Fixed bug: unless added last, the window callback would always be replaced. This didn't play well with fragments calling `AppCompatActivity.setSupportActionBar()` all the time (see [this AppCompat issue](https://issuetracker.google.com/issues/186791590)).


Version 1.1
-------------

_2021-4-14_

Added support for [KeyEvent interceptors](https://github.com/square/curtains/pull/15).

Version 1.0.1
-------------

_2021-3-3_

First bugfix release!

Fixed [crash](https://github.com/square/curtains/pull/11) when no Android X or support library in the classpath.


Version 1.0
-------------

_2021-3-2_

Initial release.

Special thanks to [consp1racy](https://github.com/consp1racy) for the window focus contributions!