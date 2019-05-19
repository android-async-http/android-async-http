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
package com.loopj.android.http.interfaces;

import com.loopj.android.http.utils.RequestHandle;

import java.util.concurrent.ExecutorService;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;

public interface AsyncHttpClientInterface {

    void setHttpClientProvider(HttpClientProviderInterface provider);

    HttpClientProviderInterface getHttpClientProvider();

    void setThreadPool(ExecutorService threadPool);

    ExecutorService getThreadPool();

    RequestHandle sendRequest(CloseableHttpClient httpClient, HttpUriRequest request, ResponseHandlerInterface responseHandler);

    RequestHandle sendRequest(RequestInterface request, ResponseHandlerInterface responseHandler);

    boolean isLoggingEnabled();

    void setLoggingEnabled(boolean loggingEnabled);

    void setLoggingLevel(int loggingLevel);

    LogInterface getLoggingInterface();

}
