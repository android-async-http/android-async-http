package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class GetSample extends SampleParentActivity {

    @Override
    protected void executeSample() {
        getAsyncHttpClient().get(this, getDefaultURL(), getResponseHandler());
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
