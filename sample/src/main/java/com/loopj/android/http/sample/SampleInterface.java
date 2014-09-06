/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.loopj.android.http.sample;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpRequest;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.util.List;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

public interface SampleInterface {

    List<RequestHandle> getRequestHandles();

    void addRequestHandle(RequestHandle handle);

    void onRunButtonPressed();

    void onCancelButtonPressed();

    Header[] getRequestHeaders();

    HttpEntity getRequestEntity();

    AsyncHttpClient getAsyncHttpClient();

    void setAsyncHttpClient(AsyncHttpClient client);

    AsyncHttpRequest getHttpRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, ResponseHandlerInterface responseHandler, Context context);

    ResponseHandlerInterface getResponseHandler();

    String getDefaultURL();

    String getDefaultHeaders();

    boolean isRequestHeadersAllowed();

    boolean isRequestBodyAllowed();

    int getSampleTitle();

    boolean isCancelButtonAllowed();

    RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler);
}
