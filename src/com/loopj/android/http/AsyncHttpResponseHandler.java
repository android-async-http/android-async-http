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

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * Used to intercept and handle the responses from requests made using 
 * {@link AsyncHttpClient}. The {@link #onSuccess(String)} method is 
 * designed to be anonymously overridden with your own response handling code.
 * <p>
 * Additionally, you can override the {@link #onFailure(Throwable, String)},
 * {@link #onStart()}, and {@link #onFinish()} methods as required.
 * <p>
 * For example:
 * <p>
 * <pre>
 * AsyncHttpClient client = new AsyncHttpClient();
 * client.get("http://www.google.com", new AsyncHttpResponseHandler() {
 *     &#064;Override
 *     public void onStart() {
 *         // Initiated the request
 *     }
 *
 *     &#064;Override
 *     public void onSuccess(int statusCode, byte[] responseBody ) {
 *         // Successfully got a response
 *     }
 * 
 *     &#064;Override
 *     public void onFailure(int statusCode, byte[] responseBody, Throwable e) {
 *         // Response failed :(
 *     }
 *
 *     &#064;Override
 *     public void onFinish() {
 *         // Completed the request (either success or failure)
 *     }
 * });
 * </pre>
 */
public class AsyncHttpResponseHandler {
    protected static final int SUCCESS_MESSAGE = 0;
    protected static final int FAILURE_MESSAGE = 1;
    protected static final int START_MESSAGE = 2;
    protected static final int FINISH_MESSAGE = 3;
    protected static final int PROGRESS_MESSAGE = 4;
    protected static final int RETRY_MESSAGE = 5;

    protected static final int BUFFER_SIZE = 4096;

    private Handler handler;

    // avoid leaks by using a non-anonymous handler class
    // with a weak reference
    static class ResponderHandler extends Handler {
        private final WeakReference<AsyncHttpResponseHandler> mResponder; 

        ResponderHandler(AsyncHttpResponseHandler service) {
            mResponder = new WeakReference<AsyncHttpResponseHandler>(service);
        }
        @Override
        public void handleMessage(Message msg)
        {
            AsyncHttpResponseHandler service = mResponder.get();
             if (service != null) {
                  service.handleMessage(msg);
             }
        }
    } 
    
    /**
     * Creates a new AsyncHttpResponseHandler
     */
    public AsyncHttpResponseHandler() {
        // Set up a handler to post events back to the correct thread if
        // possible
        if (Looper.myLooper() != null) {
            handler = new ResponderHandler(this);
        }
    }


    //
    // Callbacks to be overridden, typically anonymously
    //

    /**
     * Fired when the request is started, override to handle in your own code
     */
    public void onStart() {}

    /**
     * Fired in all cases when the request is finished, after both success and failure, override to handle in your own code
     */
    public void onFinish() {}

    /**
     * Fired when a request returns successfully, override to handle in your own
     * code
     * 
     * @param statusCode the status code of the response
     * @param responseBody the body of the HTTP response from the server
     */
    public void onSuccess(int statusCode, byte[] responseBody) {
    }


    /**
     * Fired when a request fails to complete, override to handle in your own
     * code
     * 
     * @param statusCode the status code of the response
     * @param responseBody the response body, if any
     * @param error the underlying cause of the failure
     */
    public void onFailure(int statusCode, byte[] responseBody, Throwable error) {
    }

    /**
     * Fired when a bytes are received, override to handle in your own
     * code
     * 
     * @param current the current number of bytes loaded from the response
     * @param total the total number of bytes in the response
     */
    public void onProgress(int current, int total) {
    }

    /**
     * Fired when a retry occurs, override to handle in your own
     * code
     * 
     */
    public void onRetry() {
    }
    

    //
    // Pre-processing of messages (executes in background threadpool thread)
    //

    protected void sendSuccessMessage(int statusCode, byte[] responseBody) {
        sendMessage(obtainMessage(SUCCESS_MESSAGE, new Object[] { Integer.valueOf(statusCode), responseBody }));
    }

    protected void sendFailureMessage(int statusCode, byte[] responseBody, Throwable error) {
        sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[] { Integer.valueOf(statusCode), responseBody, error }));
    }

    protected void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE, null));
    }

    protected void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE, null));
    }

    protected void sendProgressMessage(int current, int total) {
        sendMessage(obtainMessage(PROGRESS_MESSAGE, new Object[] { current, total }));
    }

    protected void sendRetryMessage() {
      sendMessage(obtainMessage(RETRY_MESSAGE, null));
  }
    
    //
    // Pre-processing of messages (in original calling thread, typically the UI thread)
    //

    protected void handleSuccessMessage(int statusCode, byte[] responseBody) {
        onSuccess(statusCode, responseBody);
    }

    protected void handleFailureMessage(int statusCode, byte[] responseBody, Throwable error) {
        onFailure(statusCode, responseBody, error);
    }

    protected void handleProgressMessage(int current, int total) {
        onProgress(current, total);
    }

    protected void handleRetryMessage() {
      onRetry();
  }
    
    // Methods which emulate android's Handler and Message methods
    protected void handleMessage(Message msg) {
        Object[] response;

        switch (msg.what) {
        case SUCCESS_MESSAGE:
            response = (Object[]) msg.obj;
            handleSuccessMessage(((Integer) response[0]).intValue(), (byte[]) response[1]);
            break;
        case FAILURE_MESSAGE:
            response = (Object[]) msg.obj;
            handleFailureMessage(((Integer) response[0]).intValue(), (byte[]) response[1], (Throwable) response[2]);
            break;
        case START_MESSAGE:
            onStart();
            break;
        case FINISH_MESSAGE:
            onFinish();
            break;
        case PROGRESS_MESSAGE:
            response = (Object[]) msg.obj;
            onProgress(((Integer) response[0]).intValue(), ((Integer) response[1]).intValue());
            break;
        case RETRY_MESSAGE:
            onRetry();
          break;
        }
    }

    protected void sendMessage(Message msg) {
        // do not send messages if request has been cancelled
        if (!Thread.currentThread().isInterrupted()) {
            if (handler != null) {
                handler.sendMessage(msg);
            } else {
                handleMessage(msg);
            }
        }
    }

    protected Message obtainMessage(int responseMessage, Object response) {
        Message msg = null;
        if(handler != null){
            msg = this.handler.obtainMessage(responseMessage, response);
        }else{
            msg = new Message();
            msg.what = responseMessage;
            msg.obj = response;
        }
        return msg;
    }

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
                try{
                    ByteArrayBuffer buffer = new ByteArrayBuffer((int) contentLength);
                    try {
                        byte[] tmp = new byte[BUFFER_SIZE];
                        int l;
                        // do not send messages if request has been cancelled
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            buffer.append(tmp, 0, l);
                            sendProgressMessage(buffer.length(), (int) contentLength);
                        }
                    } finally {
                        instream.close();
                    }
                    responseBody = buffer.buffer();
                } catch( OutOfMemoryError e ) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }
        return (responseBody);
    }
    
    // Interface to AsyncHttpRequest
    void sendResponseMessage(HttpResponse response) throws IOException {
        // do not process if request has been cancelled
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            byte[] responseBody = null;
            responseBody = getResponseData(response.getEntity());
            if (status.getStatusCode() >= 300) {
                sendFailureMessage(status.getStatusCode(), responseBody, new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
            } else {
                sendSuccessMessage(status.getStatusCode(), responseBody);
            }
        }
    }
}