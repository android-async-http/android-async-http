Asynchronous Http Client for Android
====================================

An asynchronous, callback-based Http client for Android built on top of Apache's [HttpClient](http://hc.apache.org/httpcomponents-client-ga/) libraries.


Features
--------
- Make **asynchronous** HTTP requests, handle responses in **anonymous callbacks**
- HTTP requests happen **outside the UI thread**
- Requests use a **threadpool** to cap concurrent resource usage
- GET/POST/PUT/PATCH **params builder** (RequestParams)
- **Multipart file uploads** with no additional third party libraries
- Tiny size overhead to your application, only **19kb** for everything
- Automatic smart **request retries** optimized for spotty mobile connections
- Automatic **gzip** response decoding support for super-fast requests
- Optional built-in response parsing into **JSON** (JsonHttpResponseHandler)
- Optional **persistent cookie store**, saves cookies into your app's SharedPreferences


Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

http://loopj.com/android-async-http/


Installation notes
------------------
If you want to use the HTTP PATCH-methods you have to include the [httpclient-4.2.2.jar](http://apache.cs.uu.nl/dist//httpcomponents/httpclient/binary/httpcomponents-client-4.2.2-bin.zip) or [newer](http://hc.apache.org/downloads.cgi). This makes the required HttpPatch-class available to the library as this class is not included by default on Android.

Note that all other HTTP methods are available without adding the httpclient to your project.
