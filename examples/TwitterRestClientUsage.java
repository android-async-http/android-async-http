import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.JsonHttpResponseHandler;

class TwitterRestClientUsage {
    public void getPublicTimeline() {
        TwitterRestClient.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(Object response) {
                JSONArray timeline = (JSONArray)response;

                try {
                    JSONObject firstEvent = timeline.get(0);
                    String tweetText = firstEvent.getString("text");

                    System.out.println(tweetText);
                } catch(JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}