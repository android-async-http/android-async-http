package com.loopj.android.http.requests;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpHead;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public class HeadRequest extends BaseRequest {
    public HeadRequest(boolean synchronous, String url, Header[] headers, Object TAG) {
        super(synchronous, url, headers, TAG);
    }

    @Override
    public HttpUriRequest build() {
        return new HttpHead(getURL());
    }
}
