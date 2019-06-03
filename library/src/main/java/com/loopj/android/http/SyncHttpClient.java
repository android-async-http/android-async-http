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

import com.loopj.android.http.interfaces.RequestInterface;
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.utils.RequestHandle;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;

/**
 * Processes http requests in synchronous mode, so your caller thread will be blocked on each
 * request
 *
 * @see com.loopj.android.http.AsyncHttpClient
 */
public class SyncHttpClient extends AsyncHttpClient {

    /**
     * Creates a new SyncHttpClient with default constructor arguments values
     */
    public SyncHttpClient() {
        super();
    }

    @Override
    public RequestHandle sendRequest(RequestInterface request, ResponseHandlerInterface responseHandler) {
        return sendRequest(getHttpClientProvider().provide(), request.build(), responseHandler);
    }

    @Override
    public RequestHandle sendRequest(CloseableHttpClient httpClient, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
        responseHandler.setUseSynchronousMode(true);
        AsyncHttpRequest asyncRequest = new AsyncHttpRequest(httpClient, request, responseHandler);
        asyncRequest.run();
        return new RequestHandle(null);
    }
}
