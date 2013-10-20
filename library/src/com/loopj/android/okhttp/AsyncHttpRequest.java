package com.loopj.android.okhttp;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

public class AsyncHttpRequest implements Runnable {
	
	private AsyncHttpResponseHandler mResponse;
	private HttpURLConnection mClient;
	private RequestParams mRequestParams;
	private Request mRequest;
	
	public AsyncHttpRequest(HttpURLConnection client, AsyncHttpResponseHandler responseHandler, 
			RequestParams params, Request request) {
		this.mResponse = responseHandler;
		this.mClient = client;
		this.mRequest = request;
		this.mRequestParams = params;
	}
	
	@Override
	public void run() {
		try {
			if(this.mResponse != null) this.mResponse.sendStartMessage();
			this.makeRequest();
			if(this.mResponse != null) this.mResponse.sendEndMessage();
		} catch(Exception e) {
			if(this.mResponse != null) this.mResponse.sendEndMessage();
		}
	}
	
	private void makeRequest() throws Exception {
		if(!Thread.currentThread().isInterrupted()) {
			try {
				if(!Thread.currentThread().isInterrupted()) {
					if(this.mResponse != null) {
						this.mClient.setRequestMethod(this.mRequest.getRequestMethod());
						for(Map.Entry<String, String> entry : this.mRequest.getHeaders().entrySet()) {
							this.mClient.setRequestProperty(entry.getKey(), entry.getValue());
						}
						if((this.mRequestParams != null) && !(this.mRequest.getRequestMethod().equals("GET"))) {
							OutputStream params = this.mClient.getOutputStream();
							params.write(this.mRequestParams.getParams().getBytes());
							params.close();
						}
						this.mResponse.sendResponseMessage(this.mClient);
					}
				}
			} catch(Exception e) {
				if(!Thread.currentThread().isInterrupted()) {
					throw e;
				}
			}
		}
	}
	
}