package com.loopj.android.http.sample.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleJSON {

    private String Accept;
    private String Referer;
    private String AcceptLanguage;
    private String Connection;
    private String UserAgent;

    public String getAccept() {
        return Accept;
    }

    @JsonProperty("Accept")
    public void setAccept(String accept) {
        Accept = accept;
    }

    public String getReferer() {
        return Referer;
    }

    @JsonProperty("Referer")
    public void setReferer(String referer) {
        Referer = referer;
    }

    public String getAcceptLanguage() {
        return AcceptLanguage;
    }

    @JsonProperty("Accept-Language")
    public void setAcceptLanguage(String acceptLanguage) {
        AcceptLanguage = acceptLanguage;
    }

    public String getConnection() {
        return Connection;
    }

    @JsonProperty("Connection")
    public void setConnection(String connection) {
        Connection = connection;
    }

    public String getUserAgent() {
        return UserAgent;
    }

    @JsonProperty("User-Agent")
    public void setUserAgent(String userAgent) {
        UserAgent = userAgent;
    }
}
