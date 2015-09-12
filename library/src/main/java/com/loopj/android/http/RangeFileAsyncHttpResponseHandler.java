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

package com.loopj.android.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.StatusLine;
import cz.msebera.android.httpclient.client.HttpResponseException;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;


public abstract class RangeFileAsyncHttpResponseHandler extends FileAsyncHttpResponseHandler {
    private static final String LOG_TAG = "RangeFileAsyncHttpRH";

    private long current = 0;
    private boolean append = false;

    /**
     * Obtains new RangeFileAsyncHttpResponseHandler and stores response in passed file
     *
     * @param file File to store response within, must not be null
     */
    public RangeFileAsyncHttpResponseHandler(File file) {
        super(file);
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            if (status.getStatusCode() == HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE) {
                //already finished
                if (!Thread.currentThread().isInterrupted())
                    sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), null);
            } else if (status.getStatusCode() >= 300) {
                if (!Thread.currentThread().isInterrupted())
                    sendFailureMessage(status.getStatusCode(), response.getAllHeaders(), null, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
            } else {
                if (!Thread.currentThread().isInterrupted()) {
                    Header header = response.getFirstHeader(AsyncHttpClient.HEADER_CONTENT_RANGE);
                    if (header == null) {
                        append = false;
                        current = 0;
                    } else {
                        AsyncHttpClient.log.v(LOG_TAG, AsyncHttpClient.HEADER_CONTENT_RANGE + ": " + header.getValue());
                    }
                    sendSuccessMessage(status.getStatusCode(), response.getAllHeaders(), getResponseData(response.getEntity()));
                }
            }
        }
    }

    @Override
    protected byte[] getResponseData(HttpEntity entity) throws IOException {
        if (entity != null) {
            InputStream instream = entity.getContent();
            long contentLength = entity.getContentLength() + current;
            FileOutputStream buffer = new FileOutputStream(getTargetFile(), append);
            if (instream != null) {
                try {
                    byte[] tmp = new byte[BUFFER_SIZE];
                    int l;
                    while (current < contentLength && (l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                        current += l;
                        buffer.write(tmp, 0, l);
                        sendProgressMessage(current, contentLength);
                    }
                } finally {
                    instream.close();
                    buffer.flush();
                    buffer.close();
                }
            }
        }
        return null;
    }

    public void updateRequestHeaders(HttpUriRequest uriRequest) {
        if (file.exists() && file.canWrite())
            current = file.length();
        if (current > 0) {
            append = true;
            uriRequest.setHeader("Range", "bytes=" + current + "-");
        }
    }
}
