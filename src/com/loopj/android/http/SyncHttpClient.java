package com.loopj.android.http;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;

import android.content.Context;

public abstract class SyncHttpClient extends AsyncHttpClient {

	/*
	 * as this is a synchronous request this is just a helping mechanism to pass
	 * the result back to this method. Therefore the result object has to be a
	 * field to be accessible
	 */
	private String result;
	AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
		@Override
		public void onSuccess(String content) {
			result = content;
		}

		@Override
		public void onFailure(Throwable error, String content) {
			result = onRequestFailed(error, content);
		}
	};

	// Private stuff
	protected void sendRequest(DefaultHttpClient client,
			HttpContext httpContext, HttpUriRequest uriRequest,
			String contentType, AsyncHttpResponseHandler responseHandler,
			Context context) {
		if (contentType != null) {
			uriRequest.addHeader("Content-Type", contentType);
		}

		/*
		 * will execute the request directly
		 */
		new AsyncHttpRequest(client, httpContext, uriRequest, responseHandler)
				.run();

	}

	public abstract String onRequestFailed(Throwable error, String content);

	public void delete(String url, RequestParams queryParams,
			AsyncHttpResponseHandler responseHandler) {
		// TODO what about query params??
		delete(url, responseHandler);
	}

	public String get(String url, RequestParams params) {
		this.get(url, params, responseHandler);
		/*
		 * the response handler will have set the result when this line is
		 * reached
		 */
		return result;
	}

	public String put(String url, RequestParams params) {
		this.put(url, params, responseHandler);
		return result;
	}

	public String post(String url, RequestParams params) {
		this.post(url, params, responseHandler);
		return result;
	}

	public String delete(String url, RequestParams params) {
		this.delete(url, params, responseHandler);
		return result;
	}
}
