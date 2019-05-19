package com.loopj.android.http.sample;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestFactory;
import com.loopj.android.http.handlers.BaseJsonHttpResponseHandler;
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.sample.util.SampleJSON;
import com.loopj.android.http.sample.util.SampleJSONWrapper;
import com.loopj.android.http.utils.RequestHandle;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;

public class JsonSample extends SampleParentActivity {
    @Override
    public ResponseHandlerInterface getResponseHandler() {
        return new BaseJsonHttpResponseHandler<SampleJSON>() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, SampleJSON response) {
                debugHeaders(getLogTag(), headers);
                debugStatusCode(getLogTag(), statusCode);
                debugResponse(getLogTag(), rawJsonResponse);
                debugResponse(getLogTag(), formatResponse(response));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, SampleJSON errorResponse) {
                debugHeaders(getLogTag(), headers);
                debugStatusCode(getLogTag(), statusCode);
                debugThrowable(getLogTag(), throwable);
                debugResponse(getLogTag(), rawJsonData);
                debugResponse(getLogTag(), formatResponse(errorResponse));
            }

            @Override
            protected SampleJSON parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return new ObjectMapper().readValues(new JsonFactory().createParser(rawJsonData), SampleJSONWrapper.class).next().getHeaders();
            }
        };
    }

    protected String formatResponse(SampleJSON json) {
        StringBuilder buf = new StringBuilder();

        buf.append("Parsed SampleJSON object is ").append(json == null ? "null" : "not-null");
        if (json != null) {
            buf.append("\nAccepts: ").append(json.getAccept());
            buf.append("\nAcceptLanguage: ").append(json.getAcceptLanguage());
            buf.append("\nReferer: ").append(json.getReferer());
            buf.append("\nUserAgent: ").append(json.getUserAgent());
            buf.append("\nConnection: ").append(json.getConnection());
        }
        return buf.toString();
    }

    @Override
    public String getDefaultURL() {
        return PROTOCOL + "httpbin.org/headers";
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
        return R.string.title_json_sample;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        return client.sendRequest(RequestFactory.get(URL, headers), responseHandler);
    }
}
