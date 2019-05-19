package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestFactory;
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.utils.RequestHandle;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class DeleteSample extends SampleParentActivity {
    protected String LOG_TAG = "DeleteSample";

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return defaultResponseHandler;
    }

    @Override
    public String getDefaultURL() {
        return "https://httpbin.org/delete";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return true;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_delete_sample;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.sendRequest(RequestFactory.delete(URL, headers), responseHandler);
    }
}
