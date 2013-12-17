package com.loopj.android.http;

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

import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public abstract class DataAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    private static final String LOG_TAG = "DataAsyncHttpResponseHandler";

    protected static final int PROGRESS_DATA_MESSAGE = 6;

    /**
     * Creates a new AsyncHttpResponseHandler
     */
    public DataAsyncHttpResponseHandler() {
        super();
    }

    /**
     * Fired when the request progress, override to handle in your own code
     *
     * @param responseBody
     */
    public void onProgressData(byte[] responseBody) {
    }


    final public void sendProgressDataMessage(byte[] responseBytes) {
        sendMessage(obtainMessage(PROGRESS_DATA_MESSAGE, new Object[]{responseBytes}));
    }

    // Methods which emulate android's Handler and Message methods
    @Override
    protected void handleMessage(Message message) {
        super.handleMessage(message);
        Object[] response;

        switch (message.what) {
            case PROGRESS_DATA_MESSAGE:
                response = (Object[]) message.obj;
                if (response != null && response.length >= 1) {
                    try {
                        onProgressData((byte[])response[0]);
                    } catch (Throwable t) {
                        Log.e(LOG_TAG, "custom onProgressData contains an error", t);
                    }
                } else {
                    Log.e(LOG_TAG, "PROGRESS_DATA_MESSAGE didn't got enough params");
                }
                break;
        }
    }

    /**
     * Returns byte array of response HttpEntity contents
     *
     * @param entity can be null
     * @return response entity body or null
     * @throws java.io.IOException if reading entity or creating byte array failed
     */
    @Override
    byte[] getResponseData(HttpEntity entity) throws IOException {

        byte[] responseBody = null;
        if (entity != null) {
            InputStream instream = entity.getContent();
            if (instream != null) {
                long contentLength = entity.getContentLength();
                if (contentLength > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                if (contentLength < 0) {
                    contentLength = BUFFER_SIZE;
                }
                try {
                    ByteArrayBuffer buffer = new ByteArrayBuffer((int) contentLength);
                    try {
                        byte[] tmp = new byte[BUFFER_SIZE];
                        int l, count = 0;
                        // do not send messages if request has been cancelled
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            count += l;
                            buffer.append(tmp, 0, l);
                            sendProgressDataMessage(Arrays.copyOfRange(tmp, 0, l));
                        }
                    } finally {
                        instream.close();
                    }
                    responseBody = buffer.toByteArray();
                } catch (OutOfMemoryError e) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }
        return responseBody;
    }
}

