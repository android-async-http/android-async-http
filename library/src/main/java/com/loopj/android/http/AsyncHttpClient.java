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

import android.content.Context;
import android.os.Looper;

import com.loopj.android.http.interfaces.AsyncHttpClientInterface;
import com.loopj.android.http.interfaces.HttpClientProviderInterface;
import com.loopj.android.http.interfaces.LogInterface;
import com.loopj.android.http.interfaces.RequestInterface;
import com.loopj.android.http.interfaces.RequestParamInterface;
import com.loopj.android.http.interfaces.ResponseHandlerInterface;
import com.loopj.android.http.utils.LogHandler;
import com.loopj.android.http.utils.PersistentCookieStore;
import com.loopj.android.http.utils.RequestHandle;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.Credentials;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.config.Registry;
import cz.msebera.android.httpclient.conn.HttpClientConnectionManager;
import cz.msebera.android.httpclient.conn.socket.ConnectionSocketFactory;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.conn.PoolingHttpClientConnectionManager;

public class AsyncHttpClient implements AsyncHttpClientInterface {

    public static final String LOG_TAG = "AsyncHttpClient";

    public static LogInterface log = new LogHandler();
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    private HttpClientProviderInterface httpClientProvider;
    private final Map<Context, List<RequestHandle>> requestMap = new ConcurrentHashMap<Context, List<RequestHandle>>();

    /**
     * @deprecated
     */
    private boolean isUrlEncodingEnabled = true;

    /**
     * @deprecated
     */
    public boolean isUrlEncodingEnabled() {
        return isUrlEncodingEnabled;
    }

    /**
     * Sets state of URL encoding feature, see bug #227, this method allows you to turn off and on
     * this auto-magic feature on-demand.
     *
     * @param enabled desired state of feature
     * @deprecated
     */
    public void setURLEncodingEnabled(boolean enabled) {
        isUrlEncodingEnabled = enabled;
    }

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
        httpClientProvider = provider;
    }

    @Override
    public HttpClientProviderInterface getHttpClientProvider() {
        return httpClientProvider;
    }

    /**
     * Overrides the threadpool implementation used when queuing/pooling requests. By default,
     * Executors.newCachedThreadPool() is used.
     *
     * @param threadPool an instance of {@link ExecutorService} to use for queuing/pooling
     *                   requests.
     */
    public void setThreadPool(ExecutorService threadPool) {
        threadPool = threadPool;
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

    /**
     * @deprecated
     */
    public RequestHandle post(Context context, String url, Header[] headers, RequestParams params, String contentType,
                              ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.post(url, headers, params.getEntity(responseHandler)), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle post(Context context, String url, Header[] headers, HttpEntity entity, String contentType,
                              ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.post(url, headers, entity), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle post(Context context, String url, RequestParams params,
                              ResponseHandlerInterface responseHandler) {
        return post(context, url, null, params, null, responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle get(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.get(params == null ? url : getUrlWithQueryString(isUrlEncodingEnabled(), url, params), headers), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle get(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.get(params == null ? url : getUrlWithQueryString(isUrlEncodingEnabled(), url, params), null), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle head(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.head(params == null ? url : getUrlWithQueryString(isUrlEncodingEnabled(), url, params), headers), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle put(Context context, String url, Header[] headers, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.put(url, headers, entity), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle patch(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.patch(url, null, params.getEntity(responseHandler)), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle patch(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
        log.w(LOG_TAG, "contentType param ignored");
        return sendRequest(RequestFactory.patch(url, null, entity), responseHandler);
    }

    /**
     * @deprecated
     */
    public RequestHandle delete(Context context, String url, Header[] headers, RequestParams params, ResponseHandlerInterface responseHandler) {
        return sendRequest(RequestFactory.delete(params == null ? url : getUrlWithQueryString(isUrlEncodingEnabled(), url, params), headers), responseHandler);
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
    public void setEnableRedirects(final boolean enableRedirects, final boolean enableRelativeRedirects, final boolean enableCircularRedirects) throws IllegalAccessException {
        if (getHttpClientProvider() instanceof DefaultHttpClientProvider) {
            ((DefaultHttpClientProvider) getHttpClientProvider()).setEnableRedirects(enableRedirects, enableRelativeRedirects, enableCircularRedirects);
        }
        throw new IllegalAccessException("This method shall not be used with non-default credentials provider set");
    }

    /**
     * Circular redirects are enabled by default
     *
     * @param enableRedirects         boolean
     * @param enableRelativeRedirects boolean
     * @see #setEnableRedirects(boolean, boolean, boolean)
     * @deprecated
     */
    public void setEnableRedirects(final boolean enableRedirects, final boolean enableRelativeRedirects) throws IllegalAccessException {
        setEnableRedirects(enableRedirects, enableRelativeRedirects, true);
    }

    /**
     * @param enableRedirects boolean
     * @see #setEnableRedirects(boolean, boolean, boolean)
     * @deprecated
     */
    public void setEnableRedirects(final boolean enableRedirects) throws IllegalAccessException {
        setEnableRedirects(enableRedirects, enableRedirects, enableRedirects);
    }

    /**
     * Sets an optional CookieStore to use when making requests
     *
     * @param cookieStore The CookieStore implementation to use, usually an instance of {@link
     *                    PersistentCookieStore}
     */
    public void setCookieStore(CookieStore cookieStore) {
        if (getHttpClientProvider() instanceof DefaultHttpClientProvider) {
            ((DefaultHttpClientProvider) getHttpClientProvider()).setCookieStore(cookieStore);
        }
    }

    /**
     * @deprecated
     */
    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, RequestParams params) {
        if (url == null) {
            return null;
        }

        if (params == null) {
            return url;
        }

        if (shouldEncodeUrl) {
            try {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                URL _url = new URL(decodedURL);
                URI _uri = new URI(_url.getProtocol(), _url.getUserInfo(), _url.getHost(), _url.getPort(), _url.getPath(), _url.getQuery(), _url.getRef());
                url = _uri.toASCIIString();
            } catch (Exception ex) {
                // Should not really happen, added just for sake of validity
                log.e(LOG_TAG, "getUrlWithQueryString encoding URL", ex);
            }
        }

        // Construct the query string and trim it, in case it
        // includes any excessive white spaces.
        StringBuilder paramString = new StringBuilder();
        for (Map.Entry<String, RequestParamInterface> entry : params.getParams()) {
            paramString.append(entry.getKey()).append("=").append(entry.getValue().getValue());
        }

        // Only add the query string if it isn't empty and it
        // isn't equal to '?'.
        if (!paramString.toString().trim().equals("")) {
            url += url.contains("?") ? "&" : "?";
            url += paramString;
        }

        return url;
    }


    /**
     * A utility function to close an input stream without raising an exception.
     *
     * @param is input stream to close safely
     */
    public static void silentCloseInputStream(InputStream is) {
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            log.w(LOG_TAG, "Cannot close input stream", e);
        }
    }

    /**
     * @deprecated
     */
    public void clearCredentialsProvider() throws IllegalAccessException {
        if (getHttpClientProvider() instanceof DefaultHttpClientProvider) {
            ((DefaultHttpClientProvider) getHttpClientProvider()).getCredentialsProvider().clear();
        }
        throw new IllegalAccessException("This method shall not be used with non-default credentials provider set");
    }

    /**
     * @deprecated
     */
    public void setCredentials(AuthScope scope, Credentials credentials) throws IllegalAccessException {
        if (getHttpClientProvider() instanceof DefaultHttpClientProvider) {
            ((DefaultHttpClientProvider) getHttpClientProvider()).getCredentialsProvider().setCredentials(scope, credentials);
        }
        throw new IllegalAccessException("This method shall not be used with non-default credentials provider set");
    }


    /**
     * Cancels any pending (or potentially active) requests associated with the passed Context.
     * <p>&nbsp;</p> <b>Note:</b> This will only affect requests which were created with a non-null
     * android Context. This method is intended to be used in the onDestroy method of your android
     * activities to destroy all requests which are no longer required.
     *
     * @param context               the android Context instance associated to the request.
     * @param mayInterruptIfRunning specifies if active requests should be cancelled along with
     *                              pending requests.
     */
    public void cancelRequests(final Context context, final boolean mayInterruptIfRunning) {
        if (context == null) {
            log.e(LOG_TAG, "Passed null Context to cancelRequests");
            return;
        }

        final List<RequestHandle> requestList = requestMap.get(context);
        requestMap.remove(context);

        if (Looper.myLooper() == Looper.getMainLooper()) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    cancelRequests(requestList, mayInterruptIfRunning);
                }
            };
            threadPool.submit(runnable);
        } else {
            cancelRequests(requestList, mayInterruptIfRunning);
        }
    }

    private void cancelRequests(final List<RequestHandle> requestList, final boolean mayInterruptIfRunning) {
        if (requestList != null) {
            for (RequestHandle requestHandle : requestList) {
                requestHandle.cancel(mayInterruptIfRunning);
            }
        }
    }

    /**
     * Cancels all pending (or potentially active) requests. <p>&nbsp;</p> <b>Note:</b> This will
     * only affect requests which were created with a non-null android Context. This method is
     * intended to be used in the onDestroy method of your android activities to destroy all
     * requests which are no longer required.
     *
     * @param mayInterruptIfRunning specifies if active requests should be cancelled along with
     *                              pending requests.
     */
    public void cancelAllRequests(boolean mayInterruptIfRunning) {
        for (List<RequestHandle> requestList : requestMap.values()) {
            if (requestList != null) {
                for (RequestHandle requestHandle : requestList) {
                    requestHandle.cancel(mayInterruptIfRunning);
                }
            }
        }
        requestMap.clear();
    }

    /**
     * Allows you to cancel all requests currently in queue or running, by set TAG,
     * if passed TAG is null, will not attempt to cancel any requests, if TAG is null
     * on RequestHandle, it cannot be canceled by this call
     *
     * @param TAG                   TAG to be matched in RequestHandle
     * @param mayInterruptIfRunning specifies if active requests should be cancelled along with
     *                              pending requests.
     */
    public void cancelRequestsByTAG(Object TAG, boolean mayInterruptIfRunning) {
        if (TAG == null) {
            log.d(LOG_TAG, "cancelRequestsByTAG, passed TAG is null, cannot proceed");
            return;
        }
        for (List<RequestHandle> requestList : requestMap.values()) {
            if (requestList != null) {
                for (RequestHandle requestHandle : requestList) {
                    if (TAG.equals(requestHandle.getTag()))
                        requestHandle.cancel(mayInterruptIfRunning);
                }
            }
        }
    }
}
