import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class TwitterRestClientUsage {
    public void getPublicTimeline() {
        TwitterRestClient.get("statuses/public_timeline.json", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Object timeline) {
                try {
                    JSONObject firstEvent = (JSONObject) ((JSONArray)timeline).get(0);
                    String tweetText = firstEvent.getString("text");

                    // Do something with the response
                    System.out.println(tweetText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}