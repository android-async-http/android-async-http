package com.loopj.android.http.sample.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.sample.IntentServiceSample;
import com.loopj.android.http.sample.util.IntentUtil;

import org.apache.http.Header;

public class ExampleIntentService extends IntentService {

    public static final String LOG_TAG = "ExampleIntentService:IntentServiceSample";
    public static final String INTENT_URL = "INTENT_URL";
    public static final String INTENT_STATUS_CODE = "INTENT_STATUS_CODE";
    public static final String INTENT_HEADERS = "INTENT_HEADERS";
    public static final String INTENT_DATA = "INTENT_DATA";
    public static final String INTENT_THROWABLE = "INTENT_THROWABLE";

    private AsyncHttpClient aClient = new SyncHttpClient();

    public ExampleIntentService() {
        super("ExampleIntentService");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d(LOG_TAG, "onStart()");
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.hasExtra(INTENT_URL)) {
            aClient.get(this, intent.getStringExtra(INTENT_URL), new AsyncHttpResponseHandler() {
                @Override
                public void onStart() {
                    sendBroadcast(new Intent(IntentServiceSample.ACTION_START));
                    Log.d(LOG_TAG, "onStart");
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Intent broadcast = new Intent(IntentServiceSample.ACTION_SUCCESS);
                    broadcast.putExtra(INTENT_STATUS_CODE, statusCode);
                    broadcast.putExtra(INTENT_HEADERS, IntentUtil.serializeHeaders(headers));
                    broadcast.putExtra(INTENT_DATA, responseBody);
                    sendBroadcast(broadcast);
                    Log.d(LOG_TAG, "onSuccess");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Intent broadcast = new Intent(IntentServiceSample.ACTION_FAILURE);
                    broadcast.putExtra(INTENT_STATUS_CODE, statusCode);
                    broadcast.putExtra(INTENT_HEADERS, IntentUtil.serializeHeaders(headers));
                    broadcast.putExtra(INTENT_DATA, responseBody);
                    broadcast.putExtra(INTENT_THROWABLE, error);
                    sendBroadcast(broadcast);
                    Log.d(LOG_TAG, "onFailure");
                }

                @Override
                public void onCancel() {
                    sendBroadcast(new Intent(IntentServiceSample.ACTION_CANCEL));
                    Log.d(LOG_TAG, "onCancel");
                }

                @Override
                public void onRetry(int retryNo) {
                    sendBroadcast(new Intent(IntentServiceSample.ACTION_RETRY));
                    Log.d(LOG_TAG, String.format("onRetry: %d", retryNo));
                }

                @Override
                public void onFinish() {
                    sendBroadcast(new Intent(IntentServiceSample.ACTION_FINISH));
                    Log.d(LOG_TAG, "onFinish");
                }
            });
        }
    }
}
