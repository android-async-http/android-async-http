package com.loopj.android.http;

import org.apache.http.client.RedirectHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class NoRedirectHttpClient extends DefaultHttpClient {
    @Override
    protected RedirectHandler createRedirectHandler() {
        return new BlockingRedirectHandler();
    }
}
