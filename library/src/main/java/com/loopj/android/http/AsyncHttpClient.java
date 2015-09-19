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

import com.loopj.android.http.interfaces.AsyncHttpClientInterface;
import com.loopj.android.http.interfaces.HttpClientProviderInterface;
import com.loopj.android.http.interfaces.LogInterface;
import com.loopj.android.http.interfaces.RequestInterface;
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.utils.LogHandler;
import com.loopj.android.http.utils.RequestHandle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.config.Registry;
import cz.msebera.android.httpclient.conn.HttpClientConnectionManager;
import cz.msebera.android.httpclient.conn.socket.ConnectionSocketFactory;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;

public final class AsyncHttpClient implements AsyncHttpClientInterface {

    public static final String LOG_TAG = "AsyncHttpClient";

    public static LogInterface log = new LogHandler();
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private HttpClientProviderInterface httpClientProvider;

    /**
     * Creates a new AsyncHttpClient with default constructor arguments values
     */
    public AsyncHttpClient() {
        this(null);
    }

    public AsyncHttpClient(HttpClientProviderInterface httpClientProvider) {
        if (httpClientProvider == null)
            httpClientProvider = new DefaultHttpClientProvider();

        setHttpClientProvider(httpClientProvider);
    }

    /**
     * Returns logging enabled flag from underlying LogInterface instance
     * Default setting is logging enabled.
     *
     * @return boolean whether is logging across the library currently enabled
     */
    public boolean isLoggingEnabled() {
        return log.isLoggingEnabled();
    }

    /**
     * Will set logging enabled flag on underlying LogInterface instance.
     * Default setting is logging enabled.
     *
     * @param loggingEnabled whether the logging should be enabled or not
     */
    public void setLoggingEnabled(boolean loggingEnabled) {
        log.setLoggingEnabled(loggingEnabled);
    }

    /**
     * Retrieves current log level from underlying LogInterface instance.
     * Default setting is VERBOSE log level.
     *
     * @return int log level currently in effect
     */
    public int getLoggingLevel() {
        return log.getLoggingLevel();
    }

    /**
     * Sets log level to be used across all library default implementation
     * Default setting is VERBOSE log level.
     *
     * @param logLevel int log level, either from LogInterface interface or from {@link android.util.Log}
     */
    public void setLoggingLevel(int logLevel) {
        log.setLoggingLevel(logLevel);
    }

    /**
     * Will return current LogInterface used in AsyncHttpClient instance
     *
     * @return LogInterface currently used by AsyncHttpClient instance
     */
    public LogInterface getLoggingInterface() {
        return log;
    }

    /**
     * Sets default LogInterface (similar to std Android Log util class) instance,
     * to be used in AsyncHttpClient instance
     *
     * @param logInterfaceInstance LogInterface instance, if null, nothing is done
     */
    public void setLogInterface(LogInterface logInterfaceInstance) {
        if (logInterfaceInstance != null) {
            log = logInterfaceInstance;
        }
    }

    /**
     * Returns the current executor service used. By default, Executors.newCachedThreadPool() is
     * used.
     *
     * @return current executor service used
     */
    public ExecutorService getThreadPool() {
        return threadPool;
    }

    @Override
    public RequestHandle sendRequest(CloseableHttpClient httpClient, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
        AsyncHttpRequest asyncRequest = new AsyncHttpRequest(httpClient, request, responseHandler);
        getThreadPool().submit(asyncRequest);
        return new RequestHandle(asyncRequest);
    }

    @Override
    public RequestHandle sendRequest(RequestInterface request, ResponseHandlerInterface responseHandler) {
        return sendRequest(getHttpClientProvider().provide(), request.build(), responseHandler);
    }

    @Override
    public void setHttpClientProvider(HttpClientProviderInterface provider) {
        if (provider == null) provider = new DefaultHttpClientProvider();
        this.httpClientProvider = provider;
    }

    @Override
    public HttpClientProviderInterface getHttpClientProvider() {
        return this.httpClientProvider;
    }

    /**
     * Overrides the threadpool implementation used when queuing/pooling requests. By default,
     * Executors.newCachedThreadPool() is used.
     *
     * @param threadPool an instance of {@link ExecutorService} to use for queuing/pooling
     *                   requests.
     */
    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    /**
     * Get the default threading pool to be used for this HTTP client.
     *
     * @return The default threading pool to be used
     */
    protected ExecutorService getDefaultThreadPool() {
        return Executors.newCachedThreadPool();
    }

    /**
     * Provided so it is easier for developers to provide custom ThreadSafeClientConnManager implementation
     *
     * @param schemeRegistry SchemeRegistry, usually provided by
     * @return ClientConnectionManager instance
     */
    protected HttpClientConnectionManager createConnectionManager(Registry<ConnectionSocketFactory> schemeRegistry) {
        return new PoolingHttpClientConnectionManager(schemeRegistry);
    }
}
