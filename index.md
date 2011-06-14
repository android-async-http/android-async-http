---
layout: project
title: Android Asynchronous Http Client
tagline: A Callback-Based Http Client Library for Android
version: 1.3.0
github_url: https://github.com/loopj/android-async-http
download_url: https://github.com/downloads/loopj/android-async-http/android-async-http-1.3.0.jar
---


Overview
--------
An asynchronous callback-based Http client for Android built on top of Apache's
[HttpClient](http://hc.apache.org/httpcomponents-client-ga/) libraries.
All requests are made outside of your app's main UI thread, but any callback
logic will be executed on the same thread as the callback was created using
Android's Handler message passing.


Features
--------
- Make **asynchronous** HTTP requests, handle responses in **anonymous callbacks**
- HTTP requests happen **outside the UI thread**
- Requests use a **threadpool** to cap concurrent resource usage
- GET/POST **params builder** (RequestParams)
- **Multipart file uploads** with no additional third party libraries
- Tiny size overhead to your application, only **19kb** for everything
- Automatic smart **request retries** optimized for spotty mobile connections
- Automatic **gzip** response decoding support for super-fast requests
- Optional built-in response parsing into **JSON** (JsonHttpResponseHandler)
- Optional **persistent cookie store**, saves cookies into your app's SharedPreferences


Who is Using It?
----------------
[Heyzap for Android](https://market.android.com/details?id=com.heyzap.android)
:   Social game discovery app with 800,000+ installs

Send me a [message](https://github.com/inbox/new?to=loopj) on github to let me
know if you are using this library in a released android application!


Installation & Basic Usage
--------------------------
Download the latest .jar file from github and place it in your Android app's
`libs/` folder.

Import the http package in your app's main Activity.

{% highlight java %}
import com.loopj.android.http.*;
{% endhighlight %}

Create a new `AsyncHttpClient` instance and make a request:
{% highlight java %}
AsyncHttpClient client = new AsyncHttpClient();
client.get("http://www.google.com", new AsyncHttpResponseHandler() {
    @Override
    public void onSuccess(String response) {
        System.out.println(response);
    }
});
{% endhighlight %}


Recommended Usage: Make a Static Http Client
--------------------------------------------
In this example, we'll make a http client class with static accessors to make
it easy to communicate with Twitter's API.

{% highlight java %}
import com.loopj.android.http.*;

public class TwitterRestClient {
  private static final String BASE_URL = "http://api.twitter.com/1/";

  private static AsyncHttpClient client = new AsyncHttpClient();

  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
      client.get(getAbsoluteUrl(url), params, responseHandler);
  }

  private static String getAbsoluteUrl(String relativeUrl) {
      return BASE_URL + relativeUrl;
  }
}
{% endhighlight %}

This then makes it very easy to work with the Twitter API in your code:
{% highlight java %}
import org.json.*;
import com.loopj.android.http.*;

class TwitterRestClientUsage {
    public void getPublicTimeline() throws JSONException {
        TwitterRestClient.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONArray response) {
                // Pull out the first event on the public timeline
                JSONObject firstEvent = timeline.get(0);
                String tweetText = firstEvent.getString("text");

                // Do something with the response
                System.out.println(tweetText);
            }
        });
    }
}
{% endhighlight %}

Check out the
[AsyncHttpClient](http://loopj.com/android-async-http/doc/com/loopj/android/http/AsyncHttpClient.html),
[RequestParams](http://loopj.com/android-async-http/doc/com/loopj/android/http/RequestParams.html) and
[AsyncHttpResponseHandler](http://loopj.com/android-async-http/doc/com/loopj/android/http/AsyncHttpResponseHandler.html)
Javadocs for more details.


Persistent Cookie Storage with `PersistentCookieStore`
------------------------------------------------------
This library also includes a `PersistentCookieStore` which is an implementation
of the Apache HttpClient `CookieStore` interface that automatically saves
cookies to `SharedPreferences` storage on the Android device.

This is extremely useful if you want to use cookies to manage authentication
sessions, since the user will remain logged in even after closing and
re-opening your app.

First, create an instance of `AsyncHttpClient`:
{% highlight java %}
AsyncHttpClient myClient = new AsyncHttpClient();
{% endhighlight %}

Now set this client's cookie store to be a new instance of
`PersistentCookieStore`, constructed with an activity or application context
(usually `this` will suffice):
{% highlight java %}
PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
myClient.setCookieStore(myCookieStore);
{% endhighlight %}

Any cookies received from servers will now be stored in the persistent cookie
store.

To add your own cookies to the store, simply construct a new cookie and
call `addCookie`:

{% highlight java %}
BasicClientCookie newCookie = new BasicClientCookie("cookiesare", "awesome");
newCookie.setVersion(1);
newCookie.setDomain("mydomain.com");
newCookie.setPath("/");
myCookieStore.addCookie(newCookie);
{% endhighlight %}

See the [PersistentCookieStore Javadoc](http://loopj.com/android-async-http/doc/com/loopj/android/http/PersistentCookieStore.html)
for more information.


Adding GET/POST Parameters with `RequestParams`
-----------------------------------------------
The `RequestParams` class is used to add optional GET or POST parameters to
your requests. `RequestParams` can be built and constructed in various ways:

Create empty `RequestParams` and immediately add some parameters:
{% highlight java %}
RequestParams params = new RequestParams();
params.put("key", "value");
params.put("more", "data");
{% endhighlight %}

Create `RequestParams` for a single parameter:
{% highlight java %}
RequestParams params = new RequestParams("single", "value");
{% endhighlight %}

Create `RequestParams` from an existing Map of key/value strings:
{% highlight java %}
HashMap<String, String> paramMap = new HashMap<String, String>();
paramMap.put("key", "value");
RequestParams params = new RequestParams(paramMap);
{% endhighlight %}

See the [RequestParams Javadoc](http://loopj.com/android-async-http/doc/com/loopj/android/http/RequestParams.html)
for more information.


Uploading Files with `RequestParams`
------------------------------------
The `RequestParams` class additionally supports multipart file uploads as
follows:

Add an `InputStream` to the `RequestParams` to upload:
{% highlight java %}
InputStream myInputStream = blah;
RequestParams params = new RequestParams();
params.put("secret_passwords", myInputStream, "passwords.txt");
{% endhighlight %}

Add a `File` object to the `RequestParams` to upload:
{% highlight java %}
File myFile = new File("/path/to/file.png");
RequestParams params = new RequestParams();
try {
    params.put("profile_picture", myFile);
} catch(FileNotFoundException e) {}
{% endhighlight %}

Add a byte array to the `RequestParams` to upload:
{% highlight java %}
byte[] myByteArray = blah;
RequestParams params = new RequestParams();
params.put("soundtrack", new ByteArrayInputStream(myByteArray), "she-wolf.mp3");
{% endhighlight %}

See the [RequestParams Javadoc](http://loopj.com/android-async-http/doc/com/loopj/android/http/RequestParams.html)
for more information.


Building from Source
--------------------
To build a `.jar` file from source, first make a clone of the 
android-async-http github repository. You'll then need to copy the 
`local.properties.dist` file to `local.properties` and edit the `sdk.dir` 
setting to point to where you have the android sdk installed. You can then run:

{% highlight bash %}
ant package
{% endhighlight %}

This will generate a file named `android-async-http-version.jar`.


Reporting Bugs or Feature Requests
----------------------------------
Please report any bugs or feature requests on the github issues page for this
project here:

<https://github.com/loopj/android-async-http/issues>


Credits & Contributors
----------------------
James Smith (<http://github.com/loopj>)
:   Creator and Maintainer

Micah Fivecoate (<http://github.com/m5>)
:   Major Contributor, including the original `RequestParams`

The Droid Fu Project (<https://github.com/kaeppler/droid-fu>)
:   Inspiration and code for better http retries

Rafael Sanches (<http://blog.rafaelsanches.com>)
:   Original `SimpleMultipartEntity` code


License
-------
The Android Asynchronous Http Client is released under the Android-friendly
Apache License, Version 2.0. Read the full license here:

<http://www.apache.org/licenses/LICENSE-2.0>