package com.loopj.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;


public class BlockingRedirectHandler extends DefaultRedirectHandler {
    @Override
    public boolean isRedirectRequested(HttpResponse httpResponse, HttpContext httpContext) {
        return false;
    }
}