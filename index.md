---
layout: project
title: Android Asynchronous Http Client
tagline: A Callback-Based Http Client Library for Android
version: 1.2.0
github_url: https://github.com/loopj/android-async-http
download_url: https://github.com/downloads/loopj/android-async-http/android-async-http-1.2.0.jar
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
- Automatic smart **request retries** optimized for spotty mobile connections
- Automatic **gzip** response decoding support for super-fast requests
- Optional built-in response parsing into **JSON** (JsonHttpResponseHandler)
- Optional **persistent cookie store**, saves cookies into your app's SharedPreferences


Installation & Basic Usage
--------------------------
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


Recommended Usage: Make a Static Http Client
--------------------------------------------
In this example, we'll make a http client class with static accessors to make
it easy to communicate with Twitter's API.

{% highlight java %}
import com.loopj.android.http.*;

public class TwitterRestClient {
  private static final String USER_AGENT = "Example Twitter Rest Client";
  private static final String BASE_URL = "http://api.twitter.com/1/";

  private static AsyncHttpClient client = new AsyncHttpClient(USER_AGENT);

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


Persistent Cookie Storage
-------------------------
This library also includes a `PersistentCookieStore` which is an implementation
of the Apache HttpClient `CookieStore` interface that automatically saves
cookies to the user's `SharedPreferences`.

This is extremely useful if you want to use cookies to manage authentication
sessions, since the user will remain logged in even after closing and
re-opening your app.

First, create an instance of `AsyncHttpClient`:
{% highlight java %}
AsyncHttpClient myClient = new AsyncHttpClient("Awesome User Agent");
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


Using `RequestParams` to Build GET/POST Parameters
--------------------------------------------------
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