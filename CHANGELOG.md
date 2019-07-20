# CHANGELOG

## 1.4.10

  - Fixed IP/name resolution errors #998
  - Fixed SNI compatibility
  - Upgraded library HttpClient 4.5.8 from 4.3.6

## 1.4.9 (released 19. 9. 2015)

Complete list of commits included is here [https://github.com/loopj/android-async-http/commits/1.4.9](https://github.com/loopj/android-async-http/commits/1.4.9)  
List of closed issues is here [https://github.com/loopj/android-async-http/issues?milestone=8&state=closed](https://github.com/loopj/android-async-http/issues?milestone=8&state=closed)

  - **IMPORTANT**, We've switched library from using `org.apache.http` to use `cz.msebera.android.httpclient`, you have to update all your code
  - Library is from now on using upstream version of HttpClient libraries, provided by repackaging project https://github.com/smarek/httpclient-android/
  - Achieved API23 Compatibility, see #830 for more info
  - Added HeadSample into sample application, to verify Head request works as it should
  - FileAsyncHttpResponseHandler now has constructor with `usePoolThread` param, which causes callbacks to be fired from ThreadPool instead of main looper

## 1.4.8 (released 17. 7. 2015)

Complete list of commits included is here [https://github.com/loopj/android-async-http/commits/1.4.8](https://github.com/loopj/android-async-http/commits/1.4.8)  
List of closed issues is here [https://github.com/loopj/android-async-http/issues?milestone=7&state=closed](https://github.com/loopj/android-async-http/issues?milestone=7&state=closed)

  - New constructor for BinaryHttpResponseHandler which takes Looper as argument (thanks to @ScottFrank)
  - SaxAsyncHttpResponseHandler can be now provided with custom charset, instead of just using default one
  - Library LogCat tags now use shorter form (forced through Lint checks), appendix "ResponseHandler" shortened to "RH"
  - Updated documentation on `RequestHandle.cancel(boolean)` and returning correct response according to handle state
  - SaxAsyncHttpResponseHandler onFailure(int, Header[], byte[], Throwable) used wrong fallback to onSuccess(int, Header[], T), fixed to onFailure(int, Header[], T), where T extends SAX DefaultHandler
  - Regression fix on onProgress(int,int) documentation
  - Sample application now can be built with LeakCanary, use i.e. `gradle :sample:installWithLeakCanaryDebug` to use it
  - Updated RequestParams documentation on handling arrays, sets and maps, along with new RequestParamsDebug sample
  - Added BlackholeHttpResponseHandler implementation, which discards all response contents and silents all various log messages (see #416)
  - Added LogInterface, it's default implementation and interface option to disable/enable logging library-wide and set logging verbosity
  - Added option to TAG RequestHandle and cancel all requests matching specified TAG through `AsyncHttpClient.cancelRequestsByTAG(Object TAG)`
  - Removed deprecated `getTimeout()` replaced by `getConnectTimeout()` and `getResponseTimeout()` respectively
  - Removed deprecated `clearBasicAuth()` replaced by `clearCredentialsProvider()`

## 1.4.7 (released 9. 5. 2015)

Complete list of commits included is here [https://github.com/loopj/android-async-http/commits/1.4.7](https://github.com/loopj/android-async-http/commits/1.4.7)  
List of closed issues is here [https://github.com/loopj/android-async-http/issues?milestone=6&state=closed](https://github.com/loopj/android-async-http/issues?milestone=6&state=closed)

  - Fixed crash when canceling through RequestHandle from UI Thread (NetworkOnMainThreadException)
  - Fixed URL encoding feature, that was breaking whole URL, not just path and query parts
  - FileAsyncHttpResponseHandler now checks that target file path is available or can be created
  - DataAsyncHttpResponseHandler was sending cancel notification instead of progress notification, fixed
  - Added support for HTTP PATCH requests
  - Fixed Assert exception when mkdirs in FileAsyncHttpResponseHandler tries to create dirs that already exists
  - Provided option to easily override ClientConnectionManager provision in AsyncHttpClient
  - Changed onProgress from (int,int) to (long,long) for dealing with large transfers
  - Renamed typo of `preemtive` to `preemptive` (preemptive basic auth)
  - Added option to put File array in RequestParams
  - RequestParams now support forcing Content-Type into `multipart/form-data` even if there are no files/streams to be multiparted
  - Gradle added support for installing to local maven repository, through `gradle installArchives` task
  - Added support for Json RFC5179 in JsonHttpResponseHandler

## 1.4.6 (released 7. 9. 2014)

Complete list of commits included is here [https://github.com/loopj/android-async-http/commits/1.4.6](https://github.com/loopj/android-async-http/commits/1.4.6)  
List of closed issues is here [https://github.com/loopj/android-async-http/issues?milestone=4&state=closed](https://github.com/loopj/android-async-http/issues?milestone=2&state=closed)

  - Fixed missing boundary when passing content-type as call param along with HttpEntity
  - Added warnings for not overriden calls in JsonHttpResponseHandler (and others)
  - RequestParams now implement Serializable, to support storing them and passing them along
  - Added option to add File part with custom file name (overriding the real file name)
  - Fixed not-escaped contents in JsonStreamEntity
  - Separated connect and response timeout settings
  - Allowed to pass Looper into *HttpResponseHandler classes
  - Fixed reporting progress on GZIP compressed down-streams
  - Added more samples (eg. AsyncBackgroundThreadSample.java, ContentTypeForHttpEntitySample.java, PrePostProcessingSample.java)
  - Added option to pre- and post- process data in AsyncHttpRequest.java via subclass (see PrePostProcessingSample.java)
  - Fixed ConcurrentModificationException on AsyncHttpClient.cancelRequests
  - Fixed handling BOM in decoding response in TextHttpResponseHandler and JsonHttpResponseHandler

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
    - Using [https://httpbin.org/](https://httpbin.org/) to test methods
  - Enforcing INTERNET permission
  - Support for Gradle buildscript
  - Support for Travis CI (Continuous Integration) testing
  - Dropped support for Eclipse ADT usage (obsolete)
  - Added HTTP HEAD method support
  - Releasing both AAR and JAR (+javadoc and sources) into Maven Central repository
  - Added tons of mising Javadoc for both classes and methods
