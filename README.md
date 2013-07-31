AppRater
========

 * Android library that prompts the user to rate your app on Google Play
 * Features: dialog title, explanation message and three buttons: [Rate now], [Remind later], [No, thanks]
 * API level: 8+

Installation
========

 1. Copy AppRater.java to your Android project's ```src``` folder (inside one of your packages)
 2. Enable the prompt in your application by calling ```AppRater.getInstance().show(...)```
 3. Place this call at the end of your ```MainActivity```'s ```onCreate(...)```, for example

Examples
========

 * ```AppRater.getInstance().show(this, "com.my.package");```
 * ```AppRater.getInstance().show(this, "com.my.package", 2, 5);```
 * ```AppRater.getInstance().show(this, "com.my.package", 2, 5, R.string.apprater_title, R.string.apprater_explanation, R.string.apprater_now, R.string.apprater_later, R.string.apprater_never);```


License
=======

   Copyright 2013 Marco W. (https://github.com/marcow)

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
