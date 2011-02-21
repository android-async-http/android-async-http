package com.loopj.android.http;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONTokener;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;

public class JsonHttpResponseHandler extends AsyncHttpResponseHandler {
    protected void handleResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        if(status.getStatusCode() >= 300) {
            onFailure(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
        } else {
            try {
                String responseBody = getResponseBody(response);
                onSuccess(responseBody);

                Object jsonResponse = new JSONTokener(responseBody).nextValue();
                onSuccess(jsonResponse);
            } catch(JSONException e) {
                onFailure(e);
            } catch(IOException e) {
                onFailure(e);
            }
        }
    }

    // Public callbacks
    public void onSuccess(Object response) {}
}