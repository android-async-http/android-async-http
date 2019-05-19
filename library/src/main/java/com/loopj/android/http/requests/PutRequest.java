package com.loopj.android.http.requests;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public class PutRequest extends BaseRequestWithEntity {
    public PutRequest(boolean synchronous, String url, Header[] headers, HttpEntity entity, Object TAG) {
        super(synchronous, url, headers, entity, TAG);
    }

    @Override
    public HttpUriRequest build() {
        HttpPut put = new HttpPut(getURL());
        put.setEntity(getEntity());
        return put;
    }
}
