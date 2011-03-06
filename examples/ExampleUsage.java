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