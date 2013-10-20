package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class GetSample extends SampleParentActivity {

    @Override
    protected void executeSample(AsyncHttpClient client, String URL, AsyncHttpResponseHandler responseHandler) {
        client.get(this, URL, responseHandler);
    }

    @Override
    protected int getSampleTitle() {
        return R.string.title_get_sample;
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
        return "https://raw.github.com/loopj/android-async-http/master/README.md";
    }

    @Override
    protected AsyncHttpResponseHandler getResponseHandler() {
        return new AsyncHttpResponseHandler() {

        };
    }
}
