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
import java.io.InputStream;
import java.net.HttpURLConnection;

class AsyncHttpRequest implements Runnable {
    private final HttpURLConnection client;
    private final ResponseHandlerInterface responseHandler;

    public AsyncHttpRequest(HttpURLConnection client, ResponseHandlerInterface responseHandler) {
        this.client = client;
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        if (responseHandler != null) {
            responseHandler.sendStartMessage();
        }

        try {
        	makeRequest();
        } catch (IOException e) {
            if (responseHandler != null) {
                responseHandler.sendFailureMessage(0, null, null, e);
            }
        }

        if (responseHandler != null) {
            responseHandler.sendFinishMessage();
        }
    }

    private void makeRequest() throws IOException {
        if (!Thread.currentThread().isInterrupted()) {

        	InputStream response = client.getInputStream();

            if (!Thread.currentThread().isInterrupted()) {
                if (responseHandler != null) {
                    responseHandler.sendResponseMessage(response);
                }
            }
        }
    }
    
}
