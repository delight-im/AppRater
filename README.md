# AppRater

 * Android library that lets you prompt users to rate your application
 * The prompting dialog will be displayed (if adequate) as soon as you call ```show()``` on your AppRater instance
 * The dialog will only be shown if at least one application is available on the user's phone to handle the Intent that is defined by the target URI
 * Adapts to your application's styles and themes
 * Minimum API level: 8 (Android 2.2)

## Installation

 * Copy this Java package to your project's source folder
 * or
 * Create a new library project from this Java package and reference it in your apps

## Usage

Decide which Activity you want to appear the rating prompt in (usually your ```MainActivity.java```).

At the end of your Activity's ```onCreate(...)``` or ```onResume()```, add the following:

```new AppRater(this).show();```

If you want to call the AppRater from a Fragment (e.g. at the end of `onCreateView(...)`), use the following line instead:

```new AppRater(getActivity()).show();```

This is the basic usage. Make sure to provide your correct application package.

## Customization

You can customize the AppRater by using any of the following calls before ```show()``` (which are all optional):

```java
AppRater appRater = new AppRater(this);
appRater.setDaysBeforePrompt(3);
appRater.setLaunchesBeforePrompt(7);
appRater.setPhrases(R.string.rate_title, R.string.rate_explanation, R.string.rate_now, R.string.rate_later, R.string.rate_never);
mAlertDialog = appRater.show();
```

There are three additional methods which you won't need to call, usually:
```java
appRater.setPhrases("Rate this app", "This is the explanation why you should rate our app.", "Rate now", "Later", "No, thanks");
appRater.setTargetUri("https://play.google.com/store/apps/details?id=%1$s");
appRater.setPreferenceKeys("app_rater", "flag_dont_show", "launch_count", "first_launch_time");
```

The first one lets you set the phrases as Strings directly, without referencing resources. The second one lets you enter an alternative target URI if you want to redirect the user to another appstore than Google Play (e.g. Amazon Appstore). The third method lets you change the name of the preferences, which you won't need to do, usually.

Be sure to check the JavaDoc for all these methods when using them. Don't forget to call ```show()``` which is the most important part.

In addition to that, please keep in mind that you should call `setDaysBeforePrompt(0)` and `setLaunchesBeforePrompt(0)` for debugging purposes, so that you can see the dialog right away.

## Contributing

We welcome any contribution, no matter how small or large. Please fork this repository, apply your changes, and submit your contributions by sending a pull request.

## License

```
Copyright 2013 www.delight.im <info@delight.im>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```