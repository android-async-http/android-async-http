import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

public class ExampleUsage {
    public static void makeRequest() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get("http://www.google.com", new TextHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                System.out.println(response);
            }
        });
    }
}