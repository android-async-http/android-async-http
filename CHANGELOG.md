# CHANGELOG

## 1.4.5 (released 22. 6. 2014)

Complete list of commits included is here [https://github.com/loopj/android-async-http/commits/1.4.5](https://github.com/loopj/android-async-http/commits/1.4.5)  
List of closed issues is here [https://github.com/loopj/android-async-http/issues?milestone=2&state=closed](https://github.com/loopj/android-async-http/issues?milestone=2&state=closed)

  - Support for circular and relative redirects
  - Added support for SAX parsing, see `SaxAsyncHttpResponseHandler`
  - Fixed Threading issue when used in Service or non-UI Thread context
  - Fixed GZIPInputStream issue when running in StrictMode
  - Removed unnecessary (ambiguous) callback methods (were deprecated in 1.4.4)
  - Added JsonStreamerEntity to allow up streaming JSON data
  - Added possibility to modify blacklisted/whitelisted exceptions (see `RetryHandler`)
  - Using `newCachedThreadPool()` as default ExecutorService in library, with option to change it via main interface
  - Added `ResponseHandlerInterface` to support completely custom implementations of response handlers
  - Added `onProgress(int,int)` callback, which is used for upstream progress logging (eg. Progressbar dialogs)
  - Fixed "division by zero" in default response handler
  - Added DataAsyncHttpResponseHandler, which has extra callback method which receives partially received data
  - Fixed problem with uploading more than 2 files (changes in SimpleMultipartEntity)
  - Fixed problem where on GarbageCollectors activity there was no callback received
  - Added warning for cases, where headers overwrite each other (between common headers and per-request headers)
  - Safely closing IO streams (both upstream and downstream)
  - Fixed PersistentCookieStore issue, where non-persistent cookies were stored and added option to delete stored cookie
  - Fixed networkOnMainThreadException when canceling requests (`AsyncHttpClient#cancel(boolean)`)
  - Removed default User-Agent definition from library
  - Fixed handling null URLs in default interface calls
  - Allowed to subclass AsyncHttpClient and simply provide custom AsyncHttpRequest object (`AsyncHttpClient#newAsyncHttpRequest`)
  - Changed project structure to be default Intellij IDEA structure (eg. library/src/main/java)
  - Catching UnsupportedEncodingException default Async and Text response handlers
  - Added strict Lint checking for both Library and Sample application
  - Added example implementations in Sample application
    - Requests threading (ThreadPool usage, 6 seconds delay on response)
    - Synchronous request (from Activity and IntentService)
    - SAX Parsing the response
    - Retry request sample
    - Handling 302 redirects
    - RangeResponse (working with partially received data)
    - Basic usage of GET, POST, PUT, DELETE
    - JSON response parsing
    - GZIP compressed communication
    - Binary handler (receives `byte[]` without parsing/converting)
    - File response handler (saving response directly into given File)
    - Self-signed CA sample (how to pin SSL certificate or add custom trust-chain for requests)
    - Persistent cookies store (persisting cookies between requests)
    - Post multi-part encoded Files (SimpleMultipartEntity)
    - Jackson JSON integration

## 1.4.4 (released 28. 10. 2013)

Complete list of commits included is here [https://github.com/loopj/android-async-http/commits/1.4.4](https://github.com/loopj/android-async-http/commits/1.4.4)  
List of closed issues is here [https://github.com/loopj/android-async-http/issues?milestone=1&state=closed](https://github.com/loopj/android-async-http/issues?milestone=1&state=closed)

  - Added FileAsyncHttpResponseHandler for direct saving response into File instead of device memory
  - JsonHttpResponseHandler now parsing JSON in separate thread
  - Interface method to allow/deny handling of http redirects
  - Added method to delete previously set header (AsyncHttpClient.java)
  - Not creating new Thread if call initiated outside of UI Thread (broken, fixed in 1.4.5)
  - Support for changing response Charset (default still UTF-8)
  - Allow setting maximum retries count (AsyncHttpClient.java)
  - SimpleMultipartEntity now allows repeated usage (`HttpEntity.isRepeatable()`)
  - Added custom SSLSocketFactory to allow certificate pinning and accepting self-signed or untrusted SSL certificates
  - Callbacks to return HTTP status code and response Headers
  - Added support for unsetting Basic Auth params
  - Added support for non-standard HTTP and HTTPS ports (new constructors of `AsyncHttpClient`)
  - Allowed to change dynamically allowed content-types for BinaryHttpResponseHandler per-response-handler (was static previously)
  - Added support for setting proxy, optionally with authentication
    - `AsyncHttpClient#setProxy(String hostname, int port, String username, String password)`
  - Support for passing Maps, Sets and Lists via RequestParams
  - Properly chaining callback methods (onSuccess, onFailure, ...) in descendant order by number of function params
  - Fixed incorrect handling of URLs with spaces after redirect
    - now sanitizes spaces within URL both on-request and on-redirect
  - Added RequestHandle which can be used to cancel and/or check request status
    - Is returned for each call (`.post(...)`, `.get(...)`, `.head(...)`, `.put(...)`, etc..)
  - Added BaseJsonHttpResponseHandler to simplify integration with Jackson JSON, Gson and other JSON parsing libraries
  - Added Sample application to demonstrate functions and usage
    - Using [http://httpbin.org/](http://httpbin.org/) to test methods
  - Enforcing INTERNET permission
  - Support for Gradle buildscript
  - Support for Travis CI (Continuous Integration) testing
  - Dropped support for Eclipse ADT usage (obsolete)
  - Added HTTP HEAD method support
  - Releasing both AAR and JAR (+javadoc and sources) into Maven Central repository
  - Added tons of mising Javadoc for both classes and methods
