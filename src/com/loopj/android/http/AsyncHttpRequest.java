/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
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

package com.loopj.android.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public class AsyncHttpRequest implements Runnable {
    private HttpClient client;
    private HttpContext context;
    private HttpUriRequest request;
    private AsyncHttpResponseHandler responseHandler;

    public AsyncHttpRequest(HttpClient client, HttpContext context, HttpUriRequest request, AsyncHttpResponseHandler responseHandler) {
        this.client = client;
        this.context = context;
        this.request = request;
        this.responseHandler = responseHandler;
    }

    public void run() {
        try {
            if(responseHandler != null){
                responseHandler.sendStartMessage();
            }

            HttpResponse response = client.execute(request, context);
            if(responseHandler != null) {
                responseHandler.sendFinishMessage();
                responseHandler.sendResponseMessage(response);
            }
        } catch (IOException e) {
            if(responseHandler != null) {
                responseHandler.sendFinishMessage();
                responseHandler.sendErrorMessage(e);
            }
        }
    }
}