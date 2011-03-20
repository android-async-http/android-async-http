---
layout: project
title: Android Asynchronous Http Client
tagline: A Callback-Based Http Client Library for Android
version: 1.0
github_url: https://github.com/loopj/android-async-http
download_url: https://github.com/loopj/android-async-http/zipball/android-async-http-1.0
---


Overview
--------
An asynchronous callback-based Http client for Android built on top of Apache's
HttpClient libraries. All requests are made outside of your app's main UI
thread, but any callback logic will be executed on the same thread as the
callback was created (using Android's `Handler`s).


Features
--------
- Make asynchronous HTTP requests, handle responses in callbacks
- HTTP requests do not happen in the android UI thread
- Requests use a threadpool to cap concurrent resource usage
- GET/POST params builder (RequestParams)
- Optional built-in response parsing into JSON (JsonHttpResponseHandler)
- Optional persistent cookie store, saves cookies into your app's SharedPreferences


Installation & Setup
--------------------
Download the latest .jar file from github and place it in your Android app's
`lib/` folder.

Import the http package in your app's main Activity.

{% highlight java %}
import com.loopj.android.http.*;
{% endhighlight %}

Create a new `AsyncHttpClient` instance and make a request:
{% highlight java %}
AsyncHttpClient client = new AsyncHttpClient("My User Agent");

client.get("http://www.google.com", new AsyncHttpResponseHandler() {
    @Override
    public void onSuccess(String response) {
        System.out.println(response);
    }
});
{% endhighlight %}


Building from Source
--------------------
To build a `.jar` file from source, make a clone of the android-async-http
github repository and run:

{% highlight bash %}
ant package
{% endhighlight %}

This will generate a file named `android-async-http.jar`.


Reporting Bugs or Feature Requests
----------------------------------
Please report any bugs or feature requests on the github issues page for this
project here:

<https://github.com/loopj/android-async-http/issues>


License
-------
The Android Asynchronous Http Client is released under the Android-friendly
Apache License, Version 2.0. Read the full license here:

<http://www.apache.org/licenses/LICENSE-2.0>