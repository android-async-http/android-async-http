package com.loopj.android.http;

import org.apache.http.client.RedirectHandler;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

public class NoRedirectHttpClient extends DefaultHttpClient {
    public NoRedirectHttpClient(ClientConnectionManager conman, HttpParams params) {
        super(conman, params);
    }

    public NoRedirectHttpClient(HttpParams params) {
        super(params);
    }

    public NoRedirectHttpClient() {
        super();
    }

    @Override
    protected RedirectHandler createRedirectHandler() {
        return new BlockingRedirectHandler();
    }
}
