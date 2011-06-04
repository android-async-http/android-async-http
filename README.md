Asynchronous Http Client for Android
====================================

An asynchronous, callback-based Http client for Android built on top of Apache's [HttpClient](http://hc.apache.org/httpcomponents-client-ga/) libraries.


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


Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

http://loopj.com/android-async-http/