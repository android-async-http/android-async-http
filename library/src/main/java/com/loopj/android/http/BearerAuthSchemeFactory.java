/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
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
package com.loopj.android.http;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.auth.AUTH;
import cz.msebera.android.httpclient.auth.AuthScheme;
import cz.msebera.android.httpclient.auth.AuthSchemeFactory;
import cz.msebera.android.httpclient.auth.AuthenticationException;
import cz.msebera.android.httpclient.auth.ContextAwareAuthScheme;
import cz.msebera.android.httpclient.auth.Credentials;
import cz.msebera.android.httpclient.auth.MalformedChallengeException;
import cz.msebera.android.httpclient.message.BufferedHeader;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.HttpContext;
import cz.msebera.android.httpclient.util.CharArrayBuffer;

public class BearerAuthSchemeFactory implements AuthSchemeFactory {

    @Override
    public AuthScheme newInstance(HttpParams params) {
        return new BearerAuthScheme();
    }

    public static class BearerAuthScheme implements ContextAwareAuthScheme {
        private boolean complete = false;

        @Override
        public void processChallenge(Header header) throws MalformedChallengeException {
            this.complete = true;
        }

        @Override
        public Header authenticate(Credentials credentials, HttpRequest request) throws AuthenticationException {
            return authenticate(credentials, request, null);
        }

        @Override
        public Header authenticate(Credentials credentials, HttpRequest request, HttpContext httpContext)
                throws AuthenticationException {
            CharArrayBuffer buffer = new CharArrayBuffer(32);
            buffer.append(AUTH.WWW_AUTH_RESP);
            buffer.append(": Bearer ");
            buffer.append(credentials.getUserPrincipal().getName());
            return new BufferedHeader(buffer);
        }

        @Override
        public String getSchemeName() {
            return "Bearer";
        }

        @Override
        public String getParameter(String name) {
            return null;
        }

        @Override
        public String getRealm() {
            return null;
        }

        @Override
        public boolean isConnectionBased() {
            return false;
        }

        @Override
        public boolean isComplete() {
            return this.complete;
        }
    }
}