Asynchronous Http Client for Android
====================================

An asynchronous callback-based Http client for Android built on top of Apache's [HttpClient](http://hc.apache.org/httpcomponents-client-ga/) libraries.

Features
--------
* Make asynchronous HTTP requests, handle responses in callbacks
* HTTP requests do not happen in the android UI thread
* Requests use a threadpool to cap concurrent resource usage
* Retry requests to help with bad connectivity
* GET/POST params builder (RequestParams)
* Optional built-in response parsing into JSON (JsonHttpResponseHandler)
* Optional persistent cookie store, saves cookies into your app's SharedPreferences

Basic Example
-------------
    import com.loopj.android.http.*;

    public class ExampleUsage {
        public static void makeRequest() {
            AsyncHttpClient client = new AsyncHttpClient("My User Agent");

            client.get("http://www.google.com", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                }
            });
        }
    }

How to Build a Basic Twitter Rest Client
----------------------------------------
    // Build a static wrapper library around AsyncHttpClient
    import com.loopj.android.http.*;

    public class TwitterRestClient {
        private static final String USER_AGENT = "Example Twitter Rest Client";
        private static final String BASE_URL = "http://api.twitter.com/1/";

        private static AsyncHttpClient client = new AsyncHttpClient(USER_AGENT);

        public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
            client.get(getAbsoluteUrl(url), params, responseHandler);
        }

        private static String getAbsoluteUrl(String relativeUrl) {
            return BASE_URL + relativeUrl;
        }
    }

    // Use the TwitterRestClient in your app
    import org.json.*;
    import com.loopj.android.http.*;

    class TwitterRestClientUsage {
        public void getPublicTimeline() {
            TwitterRestClient.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(Object response) {
                    JSONArray timeline = (JSONArray)response;

                    try {
                        JSONObject firstEvent = timeline.get(0);
                        String tweetText = firstEvent.getString("text");

                        // Do something with the response
                        System.out.println(tweetText);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }