package com.loopj.android.http.sample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.loopj.android.http.sample.services.ExampleIntentService;
import com.loopj.android.http.sample.util.IntentUtil;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

public class IntentServiceSample extends SampleParentActivity {

    public static final String LOG_TAG = "IntentServiceSample";
    public static final String ACTION_START = "SYNC_START";
    public static final String ACTION_RETRY = "SYNC_RETRY";
    public static final String ACTION_CANCEL = "SYNC_CANCEL";
    public static final String ACTION_SUCCESS = "SYNC_SUCCESS";
    public static final String ACTION_FAILURE = "SYNC_FAILURE";
    public static final String ACTION_FINISH = "SYNC_FINISH";
    public static final String[] ALLOWED_ACTIONS = {ACTION_START,
            ACTION_RETRY, ACTION_CANCEL, ACTION_SUCCESS, ACTION_FAILURE, ACTION_FINISH};
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_START:
                    clearOutputs();
                    addView(getColoredView(LIGHTBLUE, "Request started"));
                    break;
                case ACTION_FINISH:
                    addView(getColoredView(LIGHTBLUE, "Request finished"));
                    break;
                case ACTION_CANCEL:
                    addView(getColoredView(LIGHTBLUE, "Request cancelled"));
                    break;
                case ACTION_RETRY:
                    addView(getColoredView(LIGHTBLUE, "Request retried"));
                    break;
                case ACTION_FAILURE:
                    debugThrowable(LOG_TAG, (Throwable) intent.getSerializableExtra(ExampleIntentService.INTENT_THROWABLE));
                case ACTION_SUCCESS:
                    debugStatusCode(LOG_TAG, intent.getIntExtra(ExampleIntentService.INTENT_STATUS_CODE, 0));
                    debugHeaders(LOG_TAG, IntentUtil.deserializeHeaders(intent.getStringArrayExtra(ExampleIntentService.INTENT_HEADERS)));
                    byte[] returnedBytes = intent.getByteArrayExtra(ExampleIntentService.INTENT_DATA);
                    if (returnedBytes != null) {
                        debugResponse(LOG_TAG, new String(returnedBytes));
                    }
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter iFilter = new IntentFilter();
        for (String action : ALLOWED_ACTIONS) {
            iFilter.addAction(action);
        }
        registerReceiver(broadcastReceiver, iFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public ResponseHandlerInterface getResponseHandler() {
        // no response handler on activity
        return null;
    }

    @Override
    public String getDefaultURL() {
        return "https://httpbin.org/get";
    }

    @Override
    public boolean isRequestHeadersAllowed() {
        return false;
    }

    @Override
    public boolean isRequestBodyAllowed() {
        return false;
    }

    @Override
    public int getSampleTitle() {
        return R.string.title_intent_service_sample;
    }

    @Override
    public RequestHandle executeSample(AsyncHttpClient client, String URL, Header[] headers, HttpEntity entity, ResponseHandlerInterface responseHandler) {
        Intent serviceCall = new Intent(this, ExampleIntentService.class);
        serviceCall.putExtra(ExampleIntentService.INTENT_URL, URL);
        startService(serviceCall);
        return null;
    }
}
