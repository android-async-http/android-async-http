Asynchronous Http Client for Android
====================================

Travis CI state : [![Build Status](https://travis-ci.org/loopj/android-async-http.png?branch=master)](https://travis-ci.org/loopj/android-async-http)

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

Maven
-----
You can now integrate this library in your project via Maven. There are available two kind of builds.

**development snapshots**
https://oss.sonatype.org/content/repositories/snapshots/com/loopj/android/android-async-http/
```
Maven URL: https://oss.sonatype.org/content/repositories/snapshots/
GroupId: com.loopj.android
ArtifactId: async-http-client
Version: 1.4.5-SNAPSHOT
Packaging: JAR or AAR
```

**releases, maven central**

http://central.maven.org/maven2/com/loopj/android/android-async-http/
```
Maven URL: http://repo1.maven.org/maven2/
GroupId: com.loopj.android
ArtifactId: android-async-http
Version: 1.4.4
Packaging: JAR or AAR
```

Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

http://loopj.com/android-async-http/

Recommended Usage
------------------------------------
Codes Example:

public final class AsyncHttpUtil {
	private static final AsyncHttpClient mClient = new AsyncHttpClient();
	private static PersistentCookieStore mCookie;

	private AsyncHttpUtil() {
	}
	
	public static void setSessionAvailable(boolean sessionAvailable){
	  mClient.setKeepSessionId(sessionAvailable);
	}

	public static void addCookie(Context context) {
		mCookie = new PersistentCookieStore(context);
		mClient.setCookieStore(mCookie);
	}

	public static void addHeader(String header, String value) {
		mClient.addHeader(header, value);
	}

	public static RequestHandle get(String url,
			AsyncHttpResponseHandler responseHandler) {
		return get(url, null, responseHandler);
	}

	public static RequestHandle get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		return mClient.get(url, params, responseHandler);
	}

	public static RequestHandle post(String url,
			AsyncHttpResponseHandler responseHandler) {
		return post(url, null, responseHandler);
	}

	public static RequestHandle post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		return mClient.post(url, params, responseHandler);
	}
	}
