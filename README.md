# This project under develop, and is currently insecure to use

Since there were no active maintainers for this project for a long time, and issues got stale, 
security issues pile up and it's not viable to maintain this project further, given there are 
quality replacements, this project is closing down.

This library has many issues handling modern TLS/SSL security protocols using and problems with validating 
chain-of-trust of remote services, it communicates with. It also suffers from high-memory-usage issues,
when handling large upstream or downstream jobs.

It is not suitable for modern projects, and thus, unless someone takes over the maintenance and invests big time
into making it performance and secure again, it is not recommended to use the library further.

For issues with using this library or migrating to different one, use appropriate forum, for example
[https://stackoverflow.com/questions/tagged/android-async-http](https://stackoverflow.com/questions/tagged/android-async-http)

## Use these alternatives, as replacement in your app please:

  - [OkHttp](https://square.github.io/okhttp/) https://square.github.io/okhttp/
  - [Volley](https://developer.android.com/training/volley) https://developer.android.com/training/volley
  - [RetroFit](https://square.github.io/retrofit/) https://square.github.io/retrofit/

*or don't, i'm not a cop*

---
---
---
---



Asynchronous Http Client for Android
====================================
[![Build Status](https://travis-ci.org/android-async-http/android-async-http.png?branch=master)](https://travis-ci.org/android-async-http/android-async-http)

An asynchronous, callback-based Http client for Android built on top of Apache's [HttpClient](https://hc.apache.org/httpcomponents-client-ga/) libraries.

Changelog
---------

See what is new in version 1.4.10 released on 20th July 2019

https://github.com/android-async-http/android-async-http/blob/1.4.10/CHANGELOG.md

Javadoc
-------

Latest Javadoc for 1.4.10 release are available here (also included in Maven repository):

https://android-async-http.github.io/android-async-http/doc/

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
See individual samples [here on Github](https://github.com/android-async-http/android-async-http/tree/1.4.10/sample/src/main/java/com/loopj/android/http/sample)  
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
Version: 1.4.10
Packaging: JAR or AAR
```
Gradle
```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation 'com.loopj.android:android-async-http:1.4.10'
}
```

**development snapshots**

https://oss.sonatype.org/content/repositories/snapshots/com/loopj/android/android-async-http/
```
Maven URL: https://oss.sonatype.org/content/repositories/snapshots/
GroupId: com.loopj.android
ArtifactId: android-async-http
Version: 1.4.11-SNAPSHOT
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
  implementation 'com.loopj.android:android-async-http:1.4.11-SNAPSHOT'
}
```

Documentation, Features and Examples
------------------------------------
Full details and documentation can be found on the project page here:

https://android-async-http.github.io/android-async-http/

