package com.loopj.android.http.sample;

public class CancelAllRequestsSample extends ThreadingTimeoutSample {

    @Override
    protected int getSampleTitle() {
        return R.string.title_cancel_all;
    }

    @Override
    protected void onCancelButtonPressed() {
        getAsyncHttpClient().cancelAllRequests(true);
    }
}
