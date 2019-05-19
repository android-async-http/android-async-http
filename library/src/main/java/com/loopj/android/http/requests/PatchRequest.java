package com.loopj.android.http.requests;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.client.methods.HttpPatch;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;

public class PatchRequest extends BaseRequestWithEntity {
    public PatchRequest(boolean synchronous, String url, Header[] headers, HttpEntity entity, Object TAG) {
        super(synchronous, url, headers, entity, TAG);
    }

    @Override
    public HttpUriRequest build() {
        HttpPatch patch = new HttpPatch(getURL());
        patch.setEntity(getEntity());
        return patch;
    }
}
