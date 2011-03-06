// Static wrapper library around AsyncHttpClient

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