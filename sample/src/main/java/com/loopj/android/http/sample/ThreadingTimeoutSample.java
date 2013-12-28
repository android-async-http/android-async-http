package com.loopj.android.http.sample;

import android.util.SparseArray;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class ThreadingTimeoutSample extends SampleParentActivity {

    private static final String LOG_TAG = "ThreadingTimeoutSample";
    private SparseArray<String> states = new SparseArray<String>();
    private int counter = 0;

    @Override
    protected int getSampleTitle() {
        return R.string.app_name;
    }

    @Override
    protected boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    protected boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    protected boolean isCancelButtonAllowed() {
        return true;
    }

    @Override
    protected String getDefaultURL() {
        return "http://httpbin.org/delay/6";
    }

    private synchronized void setStatus(int id, String status) {
        String current = states.get(id, null);
        states.put(id, current == null ? status : current + "," + status);
        clearOutputs();
        for (int i = 0; i < states.size(); i++) {
            debugResponse(LOG_TAG, states.keyAt(i) + ": " + states.get(states.keyAt(i)));
        }
    }

    @Override
    protected AsyncHttpResponseHandler getResponseHandler() {
        return new AsyncHttpResponseHandler() {

            private int id = counter++;

            @Override
            public void onStart() {
                setStatus(id, "START");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                setStatus(id, "SUCCESS");
            }

            @Override
            public void onFinish() {
                setStatus(id, "FINISH");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                setStatus(id, "FAILURE");
            }

            @Override
            public void onCancel() {
                setStatus(id, "CANCEL");
            }
        };
    }

    @Override
    protected void executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.get(this, URL, headers, null, responseHandler);
    }
}
