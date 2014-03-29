package com.loopj.android.http.sample;

import android.util.SparseArray;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class ThreadingTimeoutSample extends SampleParentActivity {

    private static final String LOG_TAG = "ThreadingTimeoutSample";
    private SparseArray<String> states = new SparseArray<>();
    private int counter = 0;

    @Override
    public int getSampleTitle() {
        return R.string.title_threading_timeout;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public boolean isCancelButtonAllowed() {
        return true;
    }

    @Override
    public String getDefaultURL() {
        return "http://httpbin.org/delay/6";
    }

    private synchronized void setStatus(int id, String status) {
        String current = states.get(id, null);
        states.put(id, current == null ? status : current + "," + status);
        clearOutputs();
        for (int i = 0; i < states.size(); i++) {
            debugResponse(LOG_TAG, String.format("%d (from %d): %s", states.keyAt(i), getCounter(), states.get(states.keyAt(i))));
        }
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
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

    public int getCounter() {
        return counter;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.get(this, URL, headers, null, responseHandler);
    }
}
