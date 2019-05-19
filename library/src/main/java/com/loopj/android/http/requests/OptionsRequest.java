package com.loopj.android.http.requests;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.methods.HttpOptions;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public class OptionsRequest extends BaseRequest {
    public OptionsRequest(boolean synchronous, String url, Header[] headers, Object TAG) {
        super(synchronous, url, headers, TAG);
    }

    @Override
    public HttpUriRequest build() {
        return new HttpOptions(getURL());
    }
}
