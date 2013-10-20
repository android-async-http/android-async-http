package com.loopj.android.http.sample;

import android.widget.Toast;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.sample.util.SampleJSON;

import org.apache.http.Header;

public class JsonSample extends SampleParentActivity {

    private static final String LOG_TAG = "JsonSample";

    @Override
    protected void executeSample() {
        getAsyncHttpClient().get(this, getDefaultURL(), getResponseHandler());
    }

    @Override
    protected int getSampleTitle() {
        return R.string.title_json_sample;
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
    protected String getDefaultURL() {
        return "http://www.jsonip.com/";
    }

    @Override
    protected AsyncHttpResponseHandler getResponseHandler() {
        return new BaseJsonHttpResponseHandler<SampleJSON>() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, SampleJSON response) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                if (response != null) {
                    Toast.makeText(JsonSample.this, response.getIp(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, SampleJSON errorResponse) {
                debugHeaders(LOG_TAG, headers);
                debugStatusCode(LOG_TAG, statusCode);
                debugThrowable(LOG_TAG, e);
                if (errorResponse != null) {
                    Toast.makeText(JsonSample.this, errorResponse.getAbout(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected SampleJSON parseResponse(String responseBody) throws Throwable {
                return new ObjectMapper().readValues(new JsonFactory().createParser(responseBody), SampleJSON.class).next();
            }
        };
    }
}
