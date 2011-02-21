import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;

public class ExampleUsage {    
    public static void makeRequest() {
        AsyncHttpClient client = new AsyncHttpClient("My User Agent");
        client.get("http://www.google.com", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.d("ExampleUsage", response);
            }
        });
    }
}