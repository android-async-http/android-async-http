package com.loopj.android.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

public class AsyncHttpRequest implements Runnable {
    private static final int RESPONSE_MESSAGE = 0;
    private static final int ERROR_MESSAGE = 1;
    private static final int START_MESSAGE = 2;
    private static final int FINISH_MESSAGE = 3;

    private HttpClient client;
    private HttpContext context;
    private HttpUriRequest request;
    private OnResponseHandler responseHandler;

    public static class OnResponseHandler extends Handler {
        public OnResponseHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case RESPONSE_MESSAGE:
                    handleResponseMessage((HttpResponse)msg.obj);
                    break;
                case ERROR_MESSAGE:
                    handleErrorMessage((Throwable)msg.obj);
                    break;
                case START_MESSAGE:
                    onStart();
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }
        }

        private void handleResponseMessage(HttpResponse response) {
            StatusLine status = response.getStatusLine();
            if(status.getStatusCode() >= 300) {
                onFailure(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
            } else {
                try {
                    HttpEntity entity = null;
                    HttpEntity temp = response.getEntity();
                    if(temp != null) {
                        entity = new BufferedHttpEntity(temp);
                    }

                    onSuccess(EntityUtils.toString(entity));
                } catch(IOException e) {
                    onFailure(e);
                }
            }
        }

        private void handleErrorMessage(Throwable e) {
            onFailure(e);
        }

        // Public callbacks
        public void onStart() {}
        public void onFinish() {}
        public void onSuccess(String content) {}
        public void onFailure(Throwable error) {}
    }

    public AsyncHttpRequest(HttpClient client, HttpContext context, HttpUriRequest request, OnResponseHandler responseHandler) {
        this.client = client;
        this.context = context;
        this.request = request;
        this.responseHandler = responseHandler;
    }

    public void run() {
        try {
            if(responseHandler != null){
                responseHandler.sendMessage(responseHandler.obtainMessage(START_MESSAGE));
            }

            HttpResponse response = client.execute(request, context);
            if(responseHandler != null) {
                responseHandler.sendMessage(responseHandler.obtainMessage(FINISH_MESSAGE));
                responseHandler.sendMessage(responseHandler.obtainMessage(RESPONSE_MESSAGE, response));
            }
        } catch (IOException e) {
            if(responseHandler != null) {
                responseHandler.sendMessage(responseHandler.obtainMessage(FINISH_MESSAGE));
                responseHandler.sendMessage(responseHandler.obtainMessage(ERROR_MESSAGE, e));
            }
        }
    }
}