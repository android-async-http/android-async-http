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