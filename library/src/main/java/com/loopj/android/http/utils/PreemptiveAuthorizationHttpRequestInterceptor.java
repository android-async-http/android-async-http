/*
    Android Asynchronous Http Client
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
    https://loopj.com

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

package com.loopj.android.http.utils;

import java.io.IOException;

import cz.msebera.android.httpclient.HttpException;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpRequestInterceptor;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.AuthState;
import cz.msebera.android.httpclient.auth.Credentials;
import cz.msebera.android.httpclient.client.CredentialsProvider;
import cz.msebera.android.httpclient.client.protocol.HttpClientContext;
import cz.msebera.android.httpclient.impl.auth.BasicScheme;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.protocol.HttpCoreContext;

public class PreemptiveAuthorizationHttpRequestInterceptor implements HttpRequestInterceptor {

    public void process(final HttpRequest request, final HttpContext context) {
        AuthState authState = (AuthState) context.getAttribute(HttpClientContext.TARGET_AUTH_STATE);
        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
                HttpClientContext.CREDS_PROVIDER);
        HttpHost targetHost = (HttpHost) context.getAttribute(HttpCoreContext.HTTP_TARGET_HOST);

        if (authState.getAuthScheme() == null) {
            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
            Credentials creds = credsProvider.getCredentials(authScope);
            if (creds != null) {
                authState.update(new BasicScheme(), creds);
            }
        }
    }

}
