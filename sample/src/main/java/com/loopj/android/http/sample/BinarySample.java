package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class BinarySample extends SampleParentActivity {
    private static final String LOG_TAG = "BinarySample";

    @Override
    protected int getSampleTitle() {
        return R.string.title_binary_sample;
    }

    @Override
    protected boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    protected boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    protected String getDefaultURL() {
        return "http://httpbin.org/gzip";
    }

    @Override
    protected AsyncHttpResponseHandler getResponseHandler() {
        return new BinaryHttpResponseHandler() {
            @Override
            public void onStart() {
                clearOutputs();
            }

            @Override
            public String[] getAllowedContentTypes() {
                // Allowing all data for debug purposes
                return new String[]{".*"};
            }

            public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                debugStatusCode(LOG_TAG, statusCode);
                debugHeaders(LOG_TAG, headers);
                debugResponse(LOG_TAG, "Received response is " + binaryData.length + " bytes");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, e);
                if (errorResponse != null) {
                    debugResponse(LOG_TAG, "Received response is " + errorResponse.length + " bytes");
                }
            }
        };
    }

    @Override
    protected void executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.get(this, URL, headers, null, responseHandler);
    }
}
