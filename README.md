PasswordEditText
================

![Screenshot](http://i.imgur.com/UyDYJUk.gif)

An `EditText` subclass that allows the user to toggle the visibility of the password.

Features
--------

* Clicking on a drawable within the password field toggles visibility of password
* Custom drawables can be set for shown/hidden states with `passwordShown` and `passwordHidden` XML attributes
* `passwordPeek` attribute for making the password visible only as long as the drawable is tapped down
* To use the included drawables, apply the `PasswordEditTextStyle` to your view

Usage
-----

    <com.github.itwenty.passwordedittext.PasswordEditText
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:inputType="textPassword"
        app:passwordShown="@android:drawable/star_big_on"
        app:passwordHidden="@android:drawable/star_big_off"
        app:passwordPeek="false"/>

Including in your project
-------------------------

    dependencies {
        ...
        compile 'com.github.itwenty:passwordedittext:1.0.0'
        ...
    }

License
-------

    Copyright 2015 Jaydeep Joshi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
