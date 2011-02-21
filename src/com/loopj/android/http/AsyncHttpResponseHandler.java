package com.loopj.android.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.Message;
import android.os.Looper;

public class AsyncHttpResponseHandler extends Handler {
    private static final int RESPONSE_MESSAGE = 0;
    private static final int ERROR_MESSAGE = 1;
    private static final int START_MESSAGE = 2;
    private static final int FINISH_MESSAGE = 3;

    public AsyncHttpResponseHandler() {
        super(Looper.getMainLooper());
    }

    public void sendStartMessage() {
        sendMessage(obtainMessage(START_MESSAGE));
    }

    public void sendFinishMessage() {
        sendMessage(obtainMessage(FINISH_MESSAGE));
    }

    public void sendResponseMessage(HttpResponse response) {
        sendMessage(obtainMessage(RESPONSE_MESSAGE, response));
    }

    public void sendErrorMessage(Throwable e) {
        sendMessage(obtainMessage(ERROR_MESSAGE, e));
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

    protected void handleResponseMessage(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        if(status.getStatusCode() >= 300) {
            onFailure(new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()));
        } else {
            try {
                onSuccess(getResponseBody(response));
            } catch(IOException e) {
                onFailure(e);
            }
        }
    }

    protected void handleErrorMessage(Throwable e) {
        onFailure(e);
    }

    protected String getResponseBody(HttpResponse response) throws IOException {
        HttpEntity entity = null;
        HttpEntity temp = response.getEntity();
        if(temp != null) {
            entity = new BufferedHttpEntity(temp);
        }

        return EntityUtils.toString(entity);
    }

    // Public callbacks
    public void onStart() {}
    public void onFinish() {}
    public void onSuccess(String content) {}
    public void onFailure(Throwable error) {}
}