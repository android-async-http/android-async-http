import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;

class ExampleUsage {    
    public static void makeRequest() {
        AsyncHttpClient client = new AsyncHttpClient("My User Agent");
        client.get("http://www.google.com", new AsyncHttpRequest.OnResponseHandler() {
            @Override
            public void onSuccess(String response) {
                Log.d("ExampleUsage", response);
            }
        });
    }
}