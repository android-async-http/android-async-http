Asynchronous Http Client for Android
====================================
[![Build Status](https://travis-ci.org/loopj/android-async-http.png?branch=master)](https://travis-ci.org/loopj/android-async-http)

An asynchronous, callback-based Http client for Android built on top of Apache's [HttpClient](http://hc.apache.org/httpcomponents-client-ga/) libraries.

Changelog
---------

See what is new in version 1.4.7 released on 9th May 2015

https://github.com/loopj/android-async-http/blob/1.4.7/CHANGELOG.md

Javadoc
-------

Latest Javadoc for 1.4.7 release are available here (also included in Maven repository):

http://loopj.com/android-async-http/doc/

Features
--------
- Make **asynchronous** HTTP requests, handle responses in **anonymous callbacks**
- HTTP requests happen **outside the UI thread**
- Requests use a **threadpool** to cap concurrent resource usage
- GET/POST **params builder** (RequestParams)
- **Multipart file uploads** with no additional third party libraries
- Tiny size overhead to your application, only **60kb** for everything
- Automatic smart **request retries** optimized for spotty mobile connections
- Automatic **gzip** response decoding support for super-fast requests
- Optional built-in response parsing into **JSON** (JsonHttpResponseHandler)
- Optional **persistent cookie store**, saves cookies into your app's SharedPreferences

Examples
--------

For inspiration and testing on device we've provided Sample Application.  
See individual samples [here on Github](https://github.com/loopj/android-async-http/tree/1.4.7/sample/src/main/java/com/loopj/android/http/sample)  
To run Sample application, simply clone the repository and run this command, to install it on connected device  

```java
gradle :sample:installDebug
```

Maven
-----
You can now integrate this library in your project via Maven. There are available two kind of builds.

**releases, maven central**

http://central.maven.org/maven2/com/loopj/android/android-async-http/
```
Maven URL: http://repo1.maven.org/maven2/
GroupId: com.loopj.android
ArtifactId: android-async-http
Version: 1.4.7
Packaging: JAR or AAR
```
Gradle: `com.loopj.android:android-async-http:1.4.7`

**development snapshots**

https://oss.sonatype.org/content/repositories/snapshots/com/loopj/android/android-async-http/
```
Maven URL: https://oss.sonatype.org/content/repositories/snapshots/
GroupId: com.loopj.android
ArtifactId: android-async-http
Version: 1.4.8-SNAPSHOT
Packaging: JAR or AAR
```
Gradle: `com.loopj.android:android-async-http:1.4.8-SNAPSHOT`

Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

http://loopj.com/android-async-http/


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/loopj/android-async-http/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

