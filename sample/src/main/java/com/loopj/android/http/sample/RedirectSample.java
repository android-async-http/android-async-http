package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestFactory;
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.utils.RequestHandle;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class RedirectSample extends SampleParentActivity {

    @Override
    public String getLogTag() {
        return "RedirectSample";
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        client.setEnableRedirectStrategy(true);
        return client.sendRequest(RequestFactory.get(URL, headers), responseHandler);
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_get_sample;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return true;
    }

    @Override
    public String getDefaultURL() {
        return PROTOCOL+"httpbin.org/status/301";
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return defaultResponseHandler;
    }
}
