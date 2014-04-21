package com.loopj.android.http.sample;

public class GzipSample extends JsonSample {

    @Override
    public int getSampleTitle() {
        return R.string.title_gzip_sample;
    }

    @Override
    public String getDefaultURL() {
        return "http://httpbin.org/gzip";
    }
}
