package com.loopj.android.http.sample;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class DeleteSample extends SampleParentActivity {
	private static final String LOG_TAG = "DeleteSample";

	@Override
	protected void executeSample(AsyncHttpClient client, String URL, AsyncHttpResponseHandler responseHandler) {
		client.delete(this, URL, null, responseHandler);
	}

	@Override
	protected int getSampleTitle() {
		return R.string.title_delete_sample;
	}

	@Override
	protected boolean isRequestBodyAllowed() {
		return false;
	}

	@Override
	protected boolean isRequestHeadersAllowed() {
		return true;
	}

	@Override
	protected String getDefaultURL() {
		return "http://www.google.com";
	}

	@Override
	protected AsyncHttpResponseHandler getResponseHandler() {
		return new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				clearOutputs();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				debugHeaders(LOG_TAG, headers);
				debugStatusCode(LOG_TAG, statusCode);
				debugResponse(LOG_TAG, new String(response));
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,	byte[] errorResponse, Throwable e) {
				debugHeaders(LOG_TAG, headers);
				debugStatusCode(LOG_TAG, statusCode);
				debugThrowable(LOG_TAG, e);
				if (errorResponse != null) {
					debugResponse(LOG_TAG, new String(errorResponse));
				}
			}
		};
	}
}
