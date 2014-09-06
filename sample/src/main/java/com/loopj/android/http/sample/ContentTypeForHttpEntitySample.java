package com.loopj.android.http.sample;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.File;
import java.io.IOException;

public class ContentTypeForHttpEntitySample extends SampleParentActivity {
    private static final String LOG_TAG = "ContentTypeForHttpEntitySample";

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugResponse(LOG_TAG, responseString);
                debugThrowable(LOG_TAG, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugResponse(LOG_TAG, responseString);
            }
        };
    }

    @Override
    public String getDefaultURL() {
        return "https://httpbin.org/post";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_content_type_http_entity;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        RequestParams rParams = new RequestParams();
        rParams.put("sample_key", "Sample String");
        try {
            File sample_file = File.createTempFile("temp_", "_handled", getCacheDir());
            rParams.put("sample_file", sample_file);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Cannot add sample file", e);
        }
        return client.post(this, URL, headers, rParams, "multipart/form-data", responseHandler);
    }
}
