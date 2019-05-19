package com.loopj.android.http.requests;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpDelete;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public class DeleteRequest extends BaseRequest {

    public DeleteRequest(boolean synchronous, String url, Header[] headers, Object TAG) {
        super(synchronous, url, headers, TAG);
    }

    @Override
    public HttpUriRequest build() {
        return new HttpDelete(getURL());
    }
}
