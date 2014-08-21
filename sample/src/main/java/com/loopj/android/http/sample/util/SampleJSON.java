/*
    Android Asynchronous Http Client Sample
    Copyright (c) 2014 Marek Sebera <marek.sebera@gmail.com>
    http://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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
