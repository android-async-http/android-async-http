package com.loopj.android.http.sample;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;

public class PatchSample extends SampleParentActivity {

	private static final String LOG_TAG = "PatchSample";
	 
	@Override
	public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
		return client.patch(this, URL, entity, null, responseHandler);
	}

	@Override
	public int getSampleTitle() {
		return R.string.title_patch_sample;
	}

	@Override
	public boolean isRequestBodyAllowed() {
		return false;
	}

	@Override
	public boolean isRequestHeadersAllowed() {
		return false;
	}

	@Override
	public String getDefaultURL() {
		return PROTOCOL + "httpbin.org/patch";
	}
	
	@Override
	public ResponseHandlerInterface getResponseHandler() {
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
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
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
