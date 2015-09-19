/*
    Copyright (c) 2015 Marek Sebera <marek.sebera@gmail.com>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
package com.loopj.android.http;

import com.loopj.android.http.interfaces.HttpClientProviderInterface;

import java.util.ArrayList;
import java.util.Collection;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.conn.HttpClientConnectionManager;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;

public class DefaultHttpClientProvider implements HttpClientProviderInterface {

    protected HttpHost proxy;
    protected CookieStore cookieStore;
    protected final Collection<? extends Header> commonHeaders = new ArrayList<Header>();
    protected HttpRequestRetryHandler retryHandler;

    @Override
    public final CloseableHttpClient provide() {
        return HttpClients.custom()
                .setConnectionManager(getConnectionManager())
                .setProxy(getProxy())
                .setDefaultCookieStore(getCookieStore())
                .setUserAgent(getUserAgent())
                .setDefaultHeaders(getHeaders())
                .setRetryHandler(getRetryHandler())
                .build();
    }

    private HttpRequestRetryHandler getRetryHandler() {
        return retryHandler;
    }

    public String getUserAgent() {
        return "AsyncHttpClient ".concat(BuildConfig.VERSION_NAME);
    }

    public HttpClientConnectionManager getConnectionManager() {
        return new PoolingHttpClientConnectionManager();
    }

    public HttpHost getProxy() {
        return proxy;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }

    public Collection<? extends Header> getHeaders() {
        return commonHeaders;
    }

}
