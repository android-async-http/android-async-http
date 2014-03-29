package com.loopj.android.http.sample;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.List;

public interface SampleInterface {

    List<RequestHandle> getRequestHandles();

    void addRequestHandle(RequestHandle handle);

    void onRunButtonPressed();

    void onCancelButtonPressed();

    Header[] getRequestHeaders();

    HttpEntity getRequestEntity();

    AsyncHttpClient getAsyncHttpClient();

    void setAsyncHttpClient(AsyncHttpClient client);

    ResponseHandlerInterface getResponseHandler();

    String getDefaultURL();

    boolean isRequestHeadersAllowed();

    boolean isRequestBodyAllowed();

    int getSampleTitle();

    boolean isCancelButtonAllowed();

    RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler);
}
