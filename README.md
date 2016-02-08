Asynchronous Http Client for Android
====================================
[![Build Status](https://travis-ci.org/loopj/android-async-http.png?branch=master)](https://travis-ci.org/loopj/android-async-http)

An asynchronous, callback-based Http client for Android built on top of Apache's [HttpClient](https://hc.apache.org/httpcomponents-client-ga/) libraries.

Changelog
---------

See what is new in version 1.4.9 released on 19th September 2015

https://github.com/loopj/android-async-http/blob/1.4.9/CHANGELOG.md

Javadoc
-------

Latest Javadoc for 1.4.9 release are available here (also included in Maven repository):

https://loopj.com/android-async-http/doc/

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
See individual samples [here on Github](https://github.com/loopj/android-async-http/tree/1.4.9/sample/src/main/java/com/loopj/android/http/sample)  
To run Sample application, simply clone the repository and run this command, to install it on connected device  

```java
gradle :sample:installDebug
```

Maven
-----
You can now integrate this library in your project via Maven. There are available two kind of builds.

**releases, maven central**

https://repo1.maven.org/maven2/com/loopj/android/android-async-http/
```
Maven URL: https://repo1.maven.org/maven2/
GroupId: com.loopj.android
ArtifactId: android-async-http
Version: 1.4.9
Packaging: JAR or AAR
```
Gradle
```groovy
repositories {
  mavenCentral()
}

dependencies {
  compile 'com.loopj.android:android-async-http:1.4.9'
}
```

**development snapshots**

https://oss.sonatype.org/content/repositories/snapshots/com/loopj/android/android-async-http/
```
Maven URL: https://oss.sonatype.org/content/repositories/snapshots/
GroupId: com.loopj.android
ArtifactId: android-async-http
Version: 1.5.0-SNAPSHOT
Packaging: JAR or AAR
```
Gradle
```groovy
repositories {
  maven {
    url 'https://oss.sonatype.org/content/repositories/snapshots/'
  }
}
dependencies {
  compile 'com.loopj.android:android-async-http:1.5.0-SNAPSHOT'
}
```

Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

https://loopj.com/android-async-http/

