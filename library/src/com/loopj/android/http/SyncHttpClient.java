package com.loopj.android.http;

import android.content.Context;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public class SyncHttpClient extends AsyncHttpClient {

    @Override
    protected void sendRequest(DefaultHttpClient client,
                               HttpContext httpContext, HttpUriRequest uriRequest,
                               String contentType, AsyncHttpResponseHandler responseHandler,
                               Context context) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        responseHandler.setForceSynchronous(true);

		/*
         * will execute the request directly
		*/
        new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler).run();
    }
}
