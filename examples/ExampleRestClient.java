import java.util.Locale;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.AsyncHttpParams;

class ExampleRestClient {
    private static final String USER_AGENT = "Example Android Rest Client";
    private static final String BASE_URL = "http://api.example.com/";

    private static AsyncHttpClient client;

    static {
        client = new AsyncHttpClient(USER_AGENT);
        client.setCookieStore(new PersistentCookieStore(AndroidApplication.getContext()));
    }

    public static void get(String url, AsyncHttpParams params, AsyncHttpRequest.OnResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), augmentParams(params), responseHandler);
    }

    public static void post(String url, AsyncHttpParams params, AsyncHttpRequest.OnResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), augmentParams(params), responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    private static AsyncHttpParams augmentParams(AsyncHttpParams params) {
        if(params == null) {
            params = new AsyncHttpParams();
        }

        // Add locale info to every request
        Locale currentLocale = Locale.getDefault();
        params.put("country_code", currentLocale.getISO3Country());
        params.put("language_code", currentLocale.getISO3Language());

        return params;
    }
}