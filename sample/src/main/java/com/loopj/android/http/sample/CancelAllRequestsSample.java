package com.loopj.android.http.sample;

public class CancelAllRequestsSample extends ThreadingTimeoutSample {

    @Override
    public int getSampleTitle() {
        return R.string.title_cancel_all;
    }

    @Override
    public void onCancelButtonPressed() {
        getAsyncHttpClient().cancelAllRequests(true);
    }
}
