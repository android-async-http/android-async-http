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
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.HttpRequestRetryHandler;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.conn.HttpClientConnectionManager;
import cz.msebera.android.httpclient.impl.client.BasicCredentialsProvider;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;

public class DefaultHttpClientProvider implements HttpClientProviderInterface {

    protected HttpHost proxy;
    protected CookieStore cookieStore;
    protected CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    protected final Collection<? extends Header> commonHeaders = new ArrayList<Header>();
    protected HttpRequestRetryHandler retryHandler;
    protected boolean enableRedirects = false, enableRelativeRedirects = false, enableCircularRedirects = false;

    public DefaultHttpClientProvider() {
    }

    /**
     * Simple interface method, to enable or disable redirects. If you set manually RedirectHandler
     * on underlying HttpClient, effects of this method will be canceled. <p>&nbsp;</p> Default
     * setting is to disallow redirects.
     *
     * @param enableRedirects         boolean
     * @param enableRelativeRedirects boolean
     * @param enableCircularRedirects boolean
     * @deprecated
     */
    public void setEnableRedirects(final boolean enableRedirects, final boolean enableRelativeRedirects, final boolean enableCircularRedirects) {
        this.enableRedirects = enableRedirects;
        this.enableCircularRedirects = enableCircularRedirects;
        this.enableRelativeRedirects = enableRelativeRedirects;
    }

    @Override
    public final CloseableHttpClient provide() {
        HttpClientBuilder builder = HttpClients.custom()
                .setConnectionManager(getConnectionManager())
                .setProxy(getProxy())
                .setDefaultCookieStore(getCookieStore())
                .setUserAgent(getUserAgent())
                .setDefaultHeaders(getHeaders())
                .setDefaultCredentialsProvider(getCredentialsProvider())
                .setRetryHandler(getRetryHandler())
                .setDefaultCookieStore(getCookieStore());

        RequestConfig requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(enableCircularRedirects).setRelativeRedirectsAllowed(enableRelativeRedirects).setRedirectsEnabled(enableRedirects).build();
        builder.setDefaultRequestConfig(requestConfig);

        return builder.build();
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

    public void setCookieStore(CookieStore store) {
        cookieStore = store;
    }

    public Collection<? extends Header> getHeaders() {
        return commonHeaders;
    }

    public CredentialsProvider getCredentialsProvider() {
        return credentialsProvider;
    }


}
