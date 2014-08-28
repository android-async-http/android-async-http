CONTRIBUTING
============
AsyncHttpClient is an open-source project made by developers for developers!

If you would like to contribute to the project, it's really great. You can contribute in a variety of ways:

  * Help us with test cases and examples for the Wiki (and kindly follow our [Coding Standards](#coding-standards))
  * If you have a good idea/patch for the project, create a [pull request](#pull-requests)
  * Found a bug? You're more than welcome to [submit an issue](#issues)
  * Help other fellow developers solve their problems, you're welcome to do so in issues

We do require certain guidelines to be followed so that the quality of the project remains top-notch:

PULL requests
-------------
When you submit a patch or a new functionality for the project, you must open a pull request. We will get to the pull request as soon as possible, investigate what functionality or bug fixes have been added and decide whether to include it in the library or not -- for the benefit of everyone.

**You agree that all contributions that you make to the library will be distributed further under the same license as the library itself (Apache V2).**

Don't be discouraged if your pull request is rejected. This is not a deadline and sometimes with a proper explanation on your side, we are persuaded to merge in the request. Just remember that this is a library for everyone and as such must meet certain, generic rules that we would like to believe are following.  

ISSUES
---------

![Read the ISSUES?](http://i.imgur.com/LPWyLe7.jpg "Read the ISSUES?")

The issues system is the place to report bugs and not for submitting patches or new functionality. As helpful as we would like to be, we cannot replace the developer and we certainly do not see what you're seeing. So when you come to report an issue, follow these simple rules:  

  * Report bugs in the English language only
  * Use Markdown to format your issue in a fashionable way (easier to read): [Familiarize yourself](https://help.github.com/articles/github-flavored-markdown)
  * If the issue is due to a crash, include the stack trace -- `throwable.printStackTrace()` -- and any other detail that will shed light on the problem
  * We need to see the source code (minus certain details that you think are confidential) that caused the problem in the first place, so include it too

Opening issues without providing us with the information necessary to debug and fix it is useless; so we will close such issues within 7 days period  

CODING STANDARDS
----------------
We need you to follow certain rules when sending source code contributions. These are the basic principles that we ourselves abide to and we require that you do so as well:

  * Do not use the Tab character (it's in first place for a reason)
  * Indentation is 4 spaces
  * Include the copyright info (as in other files) at the top of the class file
  * You must provide proper Javadoc, including description, in English for both public and protected methods, classes and properties
  * Group packages that belong to the same top-level package together, followed by an empty line
  * Add an empty line after and before class/interface declarations, methods and constructors
  * Add an empty line before and after a group of properties
  * Do not catch generic Exception/Throwable errors, but always catch the most specific type of the exception/error
