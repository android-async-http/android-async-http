# Publish to oss.sonatype.org staging repository

```
gradle clean
# edit build.gradle to update allprojects.version
# edit gradle.properties to update VERSION_NAME and VERSION_CODE
# edit CHANGELOG.md and add changes for published version
# edit sample/src/main/AndroidManifest.xml and update both versionCode and versionName attributes
# edit README.md and update paths, latest version, repository links and sample codes
gradle check
# fix all possible errors and warnings before publishing
cd library
# publishing only library, so following tasks are run in "library" sub-folder
gradle generateJavadocJar
# this will create javadoc archive check the contents via following cmd (use different name and/or path if needed)
# jar -tf ./library/build/libs/android-async-http-null-Release-1.4.11-javadoc.jar
gradle publish
```

# Publish to maven central

*For Nexus Repository Manager 2.14+*

 - Login into https://oss.sonatype.org/
 - Navigation, choose Build Promotion > Staging Repositories
 - Explore if repo was automatically created and if contents do match expectations
 - Select repository and use "Close" action, to run pre-publishing checks
 - Wait a bit
 - Refresh the panel with repositories
 - Select repository and use "Release" action, if not available, there are issues, that need to be fixed before publishing

# In GIT


**example code using 1.4.11 as released version**
```
git tag 1.4.11
git push origin --tags
```

# Github

in *releases* https://github.com/android-async-http/android-async-http/releases

 - Create new release from appropriate tag (see GIT above)
 - Describe in similar terms as in CHANGELOG.md what is being done
 - Upload JAR (library, sources and javadoc) and AAR (library) along with the release
 - Publish by saving form
