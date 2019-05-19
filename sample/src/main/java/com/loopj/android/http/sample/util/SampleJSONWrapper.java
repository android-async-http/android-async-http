package com.loopj.android.http.sample.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleJSONWrapper {

    private SampleJSON Headers;

    @JsonProperty("headers")
    public void setHeaders(SampleJSON headers) {
        Headers = headers;
    }

    public SampleJSON getHeaders() {
        return Headers;
    }
}
